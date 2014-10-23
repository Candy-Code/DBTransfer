package com.candy.dbtransfer;

import com.candy.dbtransfer.config.R;
import com.candy.dbtransfer.connector.MongoConnector;
import com.candy.dbtransfer.connector.MySqlConnector;
import com.candy.dbtransfer.exception.TooManyRecordsException;
import com.candy.dbtransfer.mapping.*;
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

    Connection mysql_conn = null;
    public void  mysql2mongo(String src_db,String tar_db){

        MySqlConnector mySqlConnector = new MySqlConnector();

        mysql_conn = mySqlConnector.getConnection(src_db);
        PropertiesReader p = PropertiesReader.getInstance();
        MongoConnector mongoConnector = new MongoConnector();
        Mongo mongo = mongoConnector.getDb();
        DB mongodb = mongo.getDB(tar_db);

        try{
            for(String table_name : MySqlHelper.getTables(mysql_conn)){
                ResultSet records = MySqlHelper.selectAll(mysql_conn,table_name);
                transfer(records,table_name,mongodb);
            }
            log.info(String.format("copy %s tables",table_count));
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
    private long table_count = 0L;
    private void transfer(ResultSet records,String table_name,DB mongodb){
        try {
            MappingConfiguration configuration = MappingConfiguration.getInstance();
            ResultSetMetaData metaData = records.getMetaData();

            //被排除的entity，不进行copy操作
            if(exclude(configuration,table_name)){
                return;
            }
            log.info("copy table " + table_name + "....");
            table_count++;
            //处理具体表名的映射
            EntityMapping entity = configuration.getMappings().get(table_name);
            if(entity!=null && entity.getType().equalsIgnoreCase(R.entity.type.transfer)){
                if(!entity.getTar_name().equals(table_name)){
                    log.debug(String.format("transfer table %s to %s",table_name,entity.getTar_name()));
                    table_name = entity.getTar_name();
                }
            }

            DBCollection collection = mongodb.getCollection(table_name);
            //添加表
            //add table here
            long records_count = 0L;
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
                        document.put(pair.getKey(),pair.getValue());
                    }
                }
                if(document.isEmpty() ){
                    continue;
                }

                //全局新增字段
                for (ColumnMapping column : configuration.getGlobal_mappings().getColumnMaps().values()) {
                    if(R.entity.type.add.equalsIgnoreCase(column.getType())){
                        Value value = column.getValue();
                        document.put(column.getSrc_name(),
                                executeExpression(value, configuration.getGlobal_mappings(), column, records));

                    }
                }

                //具体实体新增字段
                if(entity != null){
                    for(ColumnMapping column : entity.getColumnMaps().values()){
                        if(R.entity.type.add.equalsIgnoreCase(column.getType())){
                            Value value = column.getValue();
                            document.put(column.getSrc_name(),
                                    executeExpression(value, entity, column, records));
                        }
                    }
                }

                log.debug("copy record " + document.toString());
                records_count ++;
                collection.save(document);
            }
            log.info(String.format("copy %s record ", records_count));
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
                    log.debug(String.format("transfer %s to %s", column.getSrc_name(), column.getTar_name()));
                }
            }else if(R.entity.type.exclude.equalsIgnoreCase(column.getType())){
                pair = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pair;
    }
    private Object executeSql(Value value, EntityMapping entity, ColumnMapping column, ResultSet records){
        Object result_value = null;
        String expression = value.getValue();
        SqlValue sqlValue = (SqlValue)value;
        if(value.getValue().contains("$")){
            expression = replaceVariables(expression,entity.getVariables(),entity,records);
        }
        try {
            PreparedStatement statement = mysql_conn.prepareStatement(expression);
            ResultSet results = statement.executeQuery();
            switch (sqlValue.getResultType()){
                case list:
                    result_value = new ArrayList<HashMap<String,Object>>();
                    while(results.next()){
                        ResultSetMetaData metaData = results.getMetaData();
                        Map record = new LinkedHashMap();
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            record.put(metaData.getColumnLabel(i),
                                    dataConvert(getColumnValue(results, i),ColumnMapping.DataType.valueOf(column.getData_type())));
                        }
                        ((ArrayList)result_value).add(record);
                    }
                    break;
                case record:
                    if(results.getRow()>1){
                        throw new TooManyRecordsException("expected single record,exactly "+results.getRow()+" records");
                    }else if(results.getRow()<0){
                        result_value = new HashMap();
                    }else if(results.next()){
                        ResultSetMetaData metaData = results.getMetaData();
                        Map record = new LinkedHashMap();
                        for (int i = 1; i <= metaData.getColumnCount(); i++) {
                            record.put(metaData.getColumnLabel(i),
                                    dataConvert(getColumnValue(results, i),ColumnMapping.DataType.valueOf(column.getData_type())));
                        }
                        result_value = record;
                    }
                    break;
                case column:
                    if(results.getRow()>1){
                        throw new TooManyRecordsException("expected single record,exactly "+results.getRow()+" records");
                    }else if(results.getRow()<0){
                        result_value = new HashMap();
                    }else if(results.next()){
                        result_value = dataConvert(getColumnValue(results,1),ColumnMapping.DataType.valueOf(column.getData_type()));
                    }
                    break;
                default:
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result_value;
    }
    private Object executeExpression(Value value, EntityMapping entity, ColumnMapping column, ResultSet records){
        Object result_value = null;
        if(value == null){
            return result_value;
        }
        if(value instanceof SqlValue){
            return executeSql(value,entity,column,records);
        }else if(value instanceof ExpValue){
            if(value.getValue().contains("$")){
                result_value = replaceVariables(value.getValue(),entity.getVariables(),entity,records);
            }
        }

        return result_value;
    }
    private Object dataConvert(Object data,ColumnMapping.DataType type){
        switch (type){
            case auto:
                return data;
            case bigint:
                return Long.parseLong(data+"");
            case integer:
                return Integer.parseInt(data+"");
            case time:
                return TimeUtils.parse(data+"");
            case string:
                return data+"";
            default:
                return data;
        }
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
                case Types.TINYINT:
                    if(resultSet.getObject(i)==null){
                        return 0;
                    }
                    return resultSet.getInt(i);
                case Types.INTEGER:
                    if(resultSet.getObject(i)==null){
                        return 0;
                    }
                    return resultSet.getInt(i);
                case Types.BIGINT:
                    if(resultSet.getObject(i)==null){
                        return 0;
                    }
                    return resultSet.getLong(i);
                case Types.FLOAT:
                    if(resultSet.getObject(i)==null){
                        return 0.0;
                    }
                    return resultSet.getObject(i);
                default:
                    return resultSet.getObject(i);
            }
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }
    public static void main(String[] args) {
        if(args == null && args.length < 2){
            printHelp();
            return;
        }
       new Transfer().mysql2mongo(args[0],args[1]);
    }

    private static void printHelp() {
        System.out.println("dbtransfer src_name tar_name");
    }
}
