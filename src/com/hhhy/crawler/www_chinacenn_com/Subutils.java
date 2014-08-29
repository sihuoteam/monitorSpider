package com.hhhy.crawler.www_chinacenn_com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-12
 * Time: 下午1:24
 * To change this template use File | Settings | File Templates.
 */
public class Subutils {
    public static String getTime(String txt){
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        Pattern pattern  = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(txt);
        if(matcher.find()){
            String time = matcher.group();
            return time;
        }
        return null;
    }
    public static void main(String[] args){
        System.out.println(getTime("2020-11-11 12:22:32 woshaif  dska "));
    }
}
