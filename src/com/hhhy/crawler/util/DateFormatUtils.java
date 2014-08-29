package com.hhhy.crawler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateFormatUtils {
    private static final Logger logger = Logger
            .getLogger(DateFormatUtils.class);
    public static final String yyyyMMddhhmmss = "yyyy-MM-dd hh:mm:ss";
    public static final String yyyyMMdd = "yyyy-MM-dd";

    public static String formatTime(final long time, final String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(pattern);
        return sdf.format(time);
    }

    public static String getTodayFormat(final String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(pattern);
        return sdf.format(new Date());
    }
    
    public static long getTime(String date, String pattern) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern(pattern);
        return sdf.parse(date).getTime();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        logger.info(getTodayFormat(DateFormatUtils.yyyyMMddhhmmss));
        String s = "sdfsd&nbspdsfsdf";
        logger.info(s.replaceAll("&nbsp", ""));
    }

}