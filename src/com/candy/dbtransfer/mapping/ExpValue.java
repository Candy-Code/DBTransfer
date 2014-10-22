package com.candy.dbtransfer.mapping;

/**
 * Created by yantingjun on 2014/10/21.
 */
public class ExpValue implements Value{
    private int type;
    private String expression;

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String getValue() {
        return expression;
    }

    @Override
    public void setValue(String value) {
        this.expression = value;
    }

}
