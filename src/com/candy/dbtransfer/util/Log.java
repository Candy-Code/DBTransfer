package com.candy.dbtransfer.util;

import com.candy.dbtransfer.property.PropertiesReader;

import java.util.*;

/**
 * Created by yantingjun on 2014/9/24.
 */
public class Log {
    private static int level;
    private int debug = 0;
    private int info = 1;
    private int error = 2;
    static List levels = Arrays.asList("debug","info","error");
    public static Log getLog(Class clazz){
        return new Log();
    }
    private static boolean init = false;
    private static void init(){
        if(!init){
            String level_str = PropertiesReader.getInstance().getValue("log.level","info");
            level = levels.indexOf(level_str);
        }
    }

    public static int getLevel() {
        return level;
    }

    public static void setLevel(int level) {
        Log.level = level;
    }

    public void debug(String msg){
        init();
        if(level <= debug){
            System.out.println("[Debug] "+msg);
        }
    }
    public void error(String msg){
        init();
        if(level <= error) {
            System.err.println("[Error] " + msg);
        }
    }
    public void info(String msg){
        init();
        if(level <= info) {
            System.out.println("[Info] " + msg);
        }
    }


    public static void printMap(Map map,String prefix){
        for(Map.Entry entry : (Set<Map.Entry>)map.entrySet()){
            if(entry.getValue() instanceof  Map){
                System.out.println(prefix+"map:"+entry.getKey());
                printMap((Map)entry.getValue(),prefix+"--");
            }else{
                System.out.println(prefix+entry.getKey()+":"+entry.getValue());
            }
        }
    }

    public void error(Exception e) {
        error(e.getMessage());
        e.printStackTrace();
    }
}
