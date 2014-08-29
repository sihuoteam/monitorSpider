package com.hhhy.crawler.guba_sina_com_cn;

import com.hhhy.crawler.util.FormatTime;

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
        if(txt.contains("今天") || txt.contains("分钟前"))
            return FormatTime.getCurrentFormatTime();
        else{
            return null;

        }

    }
}
