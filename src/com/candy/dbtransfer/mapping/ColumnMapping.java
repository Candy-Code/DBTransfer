package com.candy.dbtransfer.mapping;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class ColumnMapping {
    private String src_name;
    private String tar_name;
    private String type = "transfer";
    private String select = "";

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

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }
}
