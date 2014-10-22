package com.candy.dbtransfer.util;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yantingjun on 2014/10/22.
 */
public class TimeUtils {
    public static final SimpleDateFormat FORMAT_YYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final SimpleDateFormat FORMAT_MMDDHHMMSS = new  SimpleDateFormat("MMddHHmmss");

    public static final SimpleDateFormat FORMAT_YMDHMS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static final SimpleDateFormat FORMAT_YYYYMMDD = new SimpleDateFormat("yyyyMMdd");

    public static final SimpleDateFormat FORMAT_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parse(String time){
        Date date = null;
        try{
            if(date==null){date = FORMAT_YYYYMMDDHHMMSS.parse(time);}
            else{return date;}
        }catch (ParseException e){}
        try{
            if(date==null){date = FORMAT_MMDDHHMMSS.parse(time);}
            else{return date;}
        }catch (ParseException e){}
        try{
            if(date==null){date = FORMAT_YMDHMS.parse(time);}
            else{return date;}
        }catch (ParseException e){}
        try{
            if(date==null){date = FORMAT_YYYYMMDD.parse(time);}
            else{return date;}
        }catch (ParseException e){}
        try{
            if(date==null){date = FORMAT_YYYY_MM_DD.parse(time);}
            else{return date;}
        }catch (ParseException e){}
        return date;
    }
}
