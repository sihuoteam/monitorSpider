package com.hhhy.crawler.util;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.Match;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-6-4
 * Time: 上午11:11
 * To change this template use File | Settings | File Templates.
 */
public class GetSubString {
    public static String getSubString(String begin,boolean containBegin,String end,boolean containEnd,String orgString){
        String regex00 = begin+"(.*)"+end;
        String regex01 = begin+"(.*"+end+")";
        String regex10 = "("+begin+".*)"+end;
        String regex11 = "("+begin+".*"+end+")";
        Pattern pattern00 = Pattern.compile(regex00);
        Pattern pattern01 = Pattern.compile(regex01);
        Pattern pattern10 = Pattern.compile(regex10);
        Pattern pattern11 = Pattern.compile(regex11);
        if(!containBegin && !containEnd){
            Matcher match = pattern00.matcher(orgString);
            if(match.find()){
                String sub = match.group(1);
                return sub;
            }
            return null;
        }
        else if(!containBegin && containEnd){
            Matcher match = pattern01.matcher(orgString);
            if(match.find()){
                String sub = match.group(1);
                return sub;
            }
            return null;
        }
        else if(containBegin && !containEnd){
            Matcher match = pattern10.matcher(orgString);
            if(match.find()){
                String sub = match.group(1);
                return sub;
            }
            return null;
        }
        else{
            Matcher match = pattern11.matcher(orgString);
            if(match.find()){
                String sub = match.group(1);
                return sub;
            }
            return null;
        }
    }
}
