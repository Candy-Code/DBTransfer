package com.candy.dbtransfer.property;


import com.candy.dbtransfer.config.R;
import com.candy.dbtransfer.util.IOUtils;
import com.candy.dbtransfer.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by yantingjun on 2014/9/21.
 */
public class PropertiesReader {
    private PropertiesReader(){
        try {
            loadProperties(R.Constants.default_system_file);
        } catch (FileNotFoundException e) {
            log.error(e);
        }
    }
    private static PropertiesReader propertiesReader = null;
    private static Log log = Log.getLog(PropertiesReader.class);

    private static Properties properties = new Properties();

    public static PropertiesReader getInstance(){
        if(propertiesReader == null){
            propertiesReader = new PropertiesReader();
        }
        return propertiesReader;
    }
    public void loadProperties(String fileName) throws FileNotFoundException {
        InputStream in = null;
        try {
            in = PropertiesReader.class.getClassLoader().getResourceAsStream(fileName);
            if(in == null ){
                in = new FileInputStream(System.getProperty("user.dir")+ File.separator+fileName);
            }
            if(in != null){
                properties.load(in);
            }
        }catch (FileNotFoundException e){
            throw e;
        }
        catch (Exception e) {
            log.error(e);
        }finally {
            IOUtils.close(in);
        }
    }

    public static PropertiesReader getProperties(){
        return propertiesReader;
    }

    public <T> T getValue(String key){
        return (T) properties.get(key);
    }
    public <T> T getValue(String key,T default_value){
        try{
            Object o = properties.get(key);
            return o == null?default_value:(T)o;
        }catch(Exception e){
            return default_value;
        }
    }
    public void setValue(String key,Object value){
        properties.put(key,value);
    }
}
