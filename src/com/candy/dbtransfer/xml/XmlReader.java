package com.candy.dbtransfer.xml;

import com.candy.dbtransfer.config.R;
import com.candy.dbtransfer.property.PropertiesReader;
import com.candy.dbtransfer.util.IOUtils;
import com.candy.dbtransfer.util.Log;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;

import javax.xml.parsers.SAXParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by yantingjun on 2014/10/14.
 */
public class XmlReader {
    private static Log log = Log.getLog(PropertiesReader.class);
    public static Document read() {
        InputStream in = null;
        try {
            in = XmlReader.class.getClassLoader().getResourceAsStream(R.Constants.default_mapping_file);
            if(in == null ){
                in = new FileInputStream(System.getProperty("user.dir")+ File.separator+R.Constants.default_mapping_file);
            }
            SAXReader reader = new SAXReader();
            return reader.read(in);
        }catch (FileNotFoundException e){
            throw new RuntimeException(R.Constants.default_mapping_file+" was not found!");
        }
        catch (Exception e) {
            log.error(e);
            return null;
        }finally {
            IOUtils.close(in);
        }
    }
}
