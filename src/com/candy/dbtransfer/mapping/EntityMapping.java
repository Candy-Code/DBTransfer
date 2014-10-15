package com.candy.dbtransfer.mapping;

import java.util.*;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class EntityMapping {
    private String src_name;
    private String tar_name;
    private String type = "transfer";
    private Map<String,String> variables = new HashMap<String,String>();
    private Map<String,ColumnMapping> columnMaps = new LinkedHashMap<String, ColumnMapping>();

    public Map<String,ColumnMapping> getColumnMaps() {
        return columnMaps;
    }

    public void setColumnMaps(Map<String,ColumnMapping> columnMaps) {
        this.columnMaps = columnMaps;
    }
    public void addColumn(ColumnMapping column){
        columnMaps.put(column.getSrc_name(),column);
    }

    public String getSrc_name() {
        return src_name;
    }

    public void setSrc_name(String src_name) {
        this.src_name = src_name;
    }

    public String getTar_name() {
        return tar_name;
    }

    public void setTar_name(String tar_name) {
        this.tar_name = tar_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addVariable(String key,String value){
        variables.put(key,value);
    }
    public Map<String,String> getVariables(){
        return variables;
    }

}
