package com.hhhy.crawler.finance_ifeng_com;

import com.hhhy.crawler.util.FormatTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-6-6
 * Time: 下午2:15
 * To change this template use File | Settings | File Templates.
 */
public class tool {
    public static String getTimeStr(String orgTime){
        String regex = "(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(orgTime);
        if(matcher.find()){
            return matcher.group();
        }
        else return FormatTime.getCurrentFormatTime();
    }
}
