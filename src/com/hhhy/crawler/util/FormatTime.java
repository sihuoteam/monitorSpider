package com.hhhy.crawler.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-6-4
 * Time: 下午3:14
 * To change this template use File | Settings | File Templates.
 */
public class FormatTime {
	
    public static Date getDate(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
           Date date = simpleDateFormat.parse(time);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
    public static String getCurrentFormatTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date());
    }
    public static String getFormatTimeAfterXDays(int xDay){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE,day+xDay);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }
    public static String getFormatTimeBeforeXDays(int xDay){
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        calendar.set(Calendar.DATE,day-xDay);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());
    }

    public static boolean isAfterToday(String time){
        if(time==null){
            return false;
        }
        else if(time.contains("小时前"))
            return true;
        else if(time.split(" ").length==1)
            time = time+" 00:00:00";
        else if(time.split(" ").length==2){
            if(time.split(" ")[1].split(":").length==2){
                time+=":00";
            }
        }
        String today = getCurrentFormatTime().split(" ")[0]+" 00:00:00";
        Date Today = getDate(today);
        Date Time = getDate(time);

        if(Time.before(Today))
            return false;
        else
            return true;
    }
    public static boolean isAfterToday(Date Time){
        String today = getCurrentFormatTime().split(" ")[0]+" 00:00:00";
        Date Today = getDate(today);

        if(Time.before(Today))
            return false;
        else
            return true;
    }
    public static String getTime(String txt,String regex){
        Pattern pattern  = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()){
            String time = matcher.group();
            return time;
        }
        return null;
    }
    public static String getTime(String txt,String regex,int i){
        Pattern pattern  = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(txt);
        if (matcher.find()){
            String time = matcher.group(i);
            return time;
        }
        return null;
    }
    public static void main(String[] args){
      //  System.out.println(getTime("http://news.hebei.com.cn/system/2014/07/14/013669577.shtml    &nbsp2014-07-14", "\\d{4}-\\d{2}-\\d{2}"));
        System.out.println(isAfterToday("2014-07-14 00:00:00"));
    }
}
