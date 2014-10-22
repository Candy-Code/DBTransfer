package com.candy.dbtransfer.mapping;

/**
 * Created by yantingjun on 2014/10/21.
 */
public class SqlValue implements Value{
    private int type;
    private ResultType result_type;
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
        expression = value;
    }

    public ResultType getResultType() {
        return result_type;
    }

    public void setResultType(ResultType result_type) {
        this.result_type = result_type;
    }
    public enum ResultType{
        list(1),record(2),column(3);
        private int value;
        ResultType(int value){
            this.value = value;
        }
        public int value(){
            return value;
        }
    }

    public static void main(String[] args) {
        ResultType.valueOf("list").value();
    }
}
