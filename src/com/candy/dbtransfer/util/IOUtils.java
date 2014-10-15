package com.candy.dbtransfer.util;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by yantingjun on 2014/9/21.
 */
public class IOUtils {
    private static Log log = Log.getLog(IOUtils.class);

    public static void close(AutoCloseable closeable){
        if(closeable!=null){
            try {
                closeable.close();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }
}
