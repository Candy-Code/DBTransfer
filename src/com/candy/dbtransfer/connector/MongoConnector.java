package com.candy.dbtransfer.connector;

import com.candy.dbtransfer.property.PropertiesReader;
import com.candy.dbtransfer.util.Log;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

import java.net.UnknownHostException;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class MongoConnector {
    Log log = Log.getLog(MongoConnector.class);

    public Mongo getDb(){
        PropertiesReader p = PropertiesReader.getInstance();
        try {
            return new Mongo((String)p.getValue("mongo.ip"));
        } catch (UnknownHostException e) {
            log.error(e);
            return null;
        }
    }
}
