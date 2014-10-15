package com.candy.dbtransfer.util;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Date;

/**
 * Author: ellios
 * Date: 13-1-10 Time: 下午3:23
 */
public class LeapDateUtils extends DateUtils {

    public static final FastDateFormat FORMAT_YYYYMMDDHHMMSS = FastDateFormat.getInstance("yyyyMMddHHmmss");

    public static final FastDateFormat FORMAT_MMDDHHMMSS = FastDateFormat.getInstance("MMddHHmmss");

    public static final FastDateFormat FORMAT_YMDHMS = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public static final FastDateFormat FORMAT_YYYYMMDD = FastDateFormat.getInstance("yyyyMMdd");

    public static final FastDateFormat FORMAT_YYYY_MM_DD = FastDateFormat.getInstance("yyyy-MM-dd");

    /**
     * 输出yyyyMMddHHmmss格式的日期字符串
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDDHHMMSS(Date date) {
        if (date == null) {
            return "";
        }
        return FORMAT_YYYYMMDDHHMMSS.format(date);
    }

    /**
     * 输出格式为yyyy-MM-dd HH:mm:ss的日期字符串
     *
     * @param date
     * @return
     */
    public static String formatYMDHMS(Date date) {
        if (date == null) {
            return "";
        }
        return FORMAT_YMDHMS.format(date);
    }
}
