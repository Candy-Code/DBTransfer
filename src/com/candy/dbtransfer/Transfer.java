package com.candy.dbtransfer;

import com.candy.dbtransfer.config.R;
import com.candy.dbtransfer.connector.MongoConnector;
import com.candy.dbtransfer.connector.MySqlConnector;
import com.candy.dbtransfer.mapping.ColumnMapping;
import com.candy.dbtransfer.mapping.EntityMapping;
import com.candy.dbtransfer.mapping.KeyValuePair;
import com.candy.dbtransfer.mapping.MappingConfiguration;
import com.candy.dbtransfer.property.PropertiesReader;
import com.candy.dbtransfer.util.*;
import com.mongodb.*;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class Transfer {
    Log log = Log.getLog(Transfer.class);

    public void  mysql2mongo(){

        MySqlConnector mySqlConnector = new MySqlConnector();

        Connection mysql_conn = mySqlConnector.getConnection();
        PropertiesReader p = PropertiesReader.getInstance();
        MongoConnector mongoConnector = new MongoConnector();
        Mongo mongo = mongoConnector.getDb();
        DB mongodb = mongo.getDB((String)p.getValue("mongo.db"));

        try{
            for(String table_name : MySqlHelper.getTables(mysql_conn)){
                ResultSet records = MySqlHelper.selectAll(mysql_conn,table_name);
                transfer(records,table_name,mongodb);
            }
        }catch (Exception e){
            log.error(e);
        }
        finally {
             IOUtils.close(mysql_conn);
             mongo.close();
        }
    }
    private boolean exclude(MappingConfiguration configuration,String table_name){
        EntityMapping entity = configuration.getMappings().get(table_name);
        if(entity!=null && entity.getType().equalsIgnoreCase(R.entity.type.exclude)){
            return true;
        }
        return false;
    }
    private void transfer(ResultSet records,String table_name,DB mongodb){
        try {
            MappingConfiguration configuration = MappingConfiguration.getInstance();
            ResultSetMetaData metaData = records.getMetaData();

            //被排除的entity，不进行copy操作
            if(exclude(configuration,table_name)){
                return;
            }
            log.info("copy table " + table_name + "....");
            //处理具体表名的映射
            EntityMapping entity = configuration.getMappings().get(table_name);
            if(entity!=null && entity.getType().equalsIgnoreCase(R.entity.type.transfer)){
                if(!entity.getTar_name().equals(table_name)){
                    log.info(String.format("transfer table %s to %s",table_name,entity.getTar_name()));
                    table_name = entity.getTar_name();
                }
            }

            DBCollection collection = mongodb.getCollection(table_name);
            //添加表
            //add table here

            while(records.next()) {
                BasicDBObject document = new BasicDBObject();

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    KeyValuePair pair = new KeyValuePair();
                    pair.setKey(metaData.getColumnName(i));
                    pair.setValue(getColumnValue(records,i));
                    //处理全局映射
                    for (ColumnMapping column : configuration.getGlobal_mappings().getColumnMaps().values()) {
                        pair = transferColumn(column, records, i, pair);
                    }

                    //处理具体表的映射
                    if(entity!=null){
                        for(ColumnMapping column : entity.getColumnMaps().values()){
                            pair = transferColumn(column, records, i, pair);
                        }
                    }


                    if(pair != null && StringUtils.isNotBlank(pair.getKey())){
                        document.put(pair.getKey(),pair.getValue()==null?"":pair.getValue());
                    }
                }
                if(document.isEmpty() ){
                    continue;
                }

                //全局新增字段
                for (ColumnMapping column : configuration.getGlobal_mappings().getColumnMaps().values()) {
                    if(R.entity.type.add.equalsIgnoreCase(column.getType())){
                        String select_expression = column.getSelect();
                        document.put(column.getSrc_name(),executeSelectExpression(select_expression,configuration.getGlobal_mappings(),column,records));
                    }
                }

                //具体实体新增字段
                if(entity != null){
                    for(ColumnMapping column : entity.getColumnMaps().values()){
                        if(R.entity.type.add.equalsIgnoreCase(column.getType())){
                            String select_expression = column.getSelect();
                            document.put(column.getSrc_name(),executeSelectExpression(select_expression,entity,column,records));
                        }
                    }
                }

                log.info("copy record " + document.toString());

                collection.save(document);
            }
        }catch (SQLException e) {
            log.error(e);
        }
    }

    private KeyValuePair transferColumn(ColumnMapping column,ResultSet records,int i,KeyValuePair pair){
        ResultSetMetaData metaData = null;
        try {
            metaData = records.getMetaData();
            if(R.entity.type.transfer.equalsIgnoreCase(column.getType())){
                if(column.getSrc_name().equalsIgnoreCase(metaData.getColumnName(i))){
                    pair.setKey(column.getTar_name());
                    pair.setValue(getColumnValue(records,i));
                    log.info(String.format("transfer %s to %s", column.getSrc_name(), column.getTar_name()));
                }
            }else if(R.entity.type.exclude.equalsIgnoreCase(column.getType())){
                pair = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pair;
    }
    private Object executeSelectExpression(String select_expression,EntityMapping entity,ColumnMapping column,ResultSet records){
        Object value = null;
        if(select_expression.contains("$")){
            select_expression = replaceVariables(select_expression,entity.getVariables(),entity,records);
        }
        MySqlConnector connector = new MySqlConnector();
        Connection connection = connector.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(select_expression);
            ResultSet results = statement.executeQuery();
            value = new ArrayList<HashMap<String,Object>>();
            while(results.next()){
                ResultSetMetaData metaData = results.getMetaData();
                Map record = new LinkedHashMap();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    record.put(metaData.getColumnName(i),getColumnValue(results,i));
                }
                ((ArrayList)value).add(record);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            IOUtils.close(connection);
        }
        return value;
    }
    public String replaceVariables(String src,Map<String,String> variables,EntityMapping entity,ResultSet records){
        Pattern pattern = Pattern.compile("\\$([^\\?,\\]\\}\\s}]+)");
        Matcher matcher=pattern.matcher(src);
        while(matcher.find()){
            String key = matcher.group(1);
            if(variables.containsKey(key)){
                String value = variables.get(key);
                Object real_value = null;
                if(value.startsWith("this.")){
                    value = value.substring("this.".length());
                    try {
                        int index = records.findColumn(value);
                        real_value = getColumnValue(records, index);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }else{
                    real_value = value;
                }
                if(real_value!=null){
                    src = src.replaceAll("\\$"+key+"",real_value.toString());
                }
            }
        }
        return src;
    }
    private Object getColumnValue(ResultSet resultSet,int i){
        try {
            ResultSetMetaData metaData = resultSet.getMetaData();
            switch (metaData.getColumnType(i)) {
                case Types.TIME:
                    return LeapDateUtils.formatYMDHMS(resultSet.getTime(i));
                case Types.TIMESTAMP:
                    return LeapDateUtils.formatYMDHMS(resultSet.getTimestamp(i));
                case Types.DATE:
                    return LeapDateUtils.formatYMDHMS(resultSet.getDate(i));
                default:
                    return resultSet.getObject(i);
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
    public static void main(String[] args) {
       new Transfer().mysql2mongo();
    }
}
