package com.candy.dbtransfer.mapping;

import com.candy.dbtransfer.config.R;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class ColumnMapping {
    private String src_name;
    private String tar_name;
    private String type = R.column.default_type;
    private String data_type = R.column.default_data_type;
    private Value value;

    public ColumnMapping(String src_name, String tar_name) {
        this.src_name = src_name;
        this.tar_name = tar_name;
    }

    public String getTar_name() {
        return tar_name;
    }

    public void setTar_name(String tar_name) {
        this.tar_name = tar_name;
    }

    public String getSrc_name() {
        return src_name;
    }

    public void setSrc_name(String src_name) {
        this.src_name = src_name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public String getData_type() {
        return data_type;
    }

    public void setData_type(String data_type) {
        this.data_type = data_type;
    }

    public enum DataType{
        auto(1),integer(2),bigint(3),time(4),string(5);
        private int type;
        DataType(int value){
            this.type = type;
        }
        public int value(){
            return type;
        }
    }
}
