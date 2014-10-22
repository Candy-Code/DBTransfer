package com.candy.dbtransfer.exception;

/**
 * Created by yantingjun on 2014/10/22.
 */
public class TooManyRecordsException extends RuntimeException{
    public TooManyRecordsException(String msg){
        super(msg);
    }
}
