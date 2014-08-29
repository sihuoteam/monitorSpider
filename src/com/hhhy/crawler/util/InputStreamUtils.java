package com.hhhy.crawler.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: chenlingpeng
 * Date: 13-8-16
 * Time: 上午9:46
 * To change this template use File | Settings | File Templates.
 */
public class InputStreamUtils {
    private static final String defaultCharSet="UTF-8";
    public static String inputStream2String(InputStream is, String charSet){
        if(is!=null){
            StringBuilder sb = new StringBuilder();
            String line;
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, charSet));
                while((line=reader.readLine())!=null){
                    sb.append(line).append("\n");
                }
            } catch (Exception e){}
            finally {
                try {
                    is.close();
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public static String inputStream2String(InputStream is){
        if(is!=null){
            StringBuilder sb = new StringBuilder();
            String line;
            try{
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                while((line=reader.readLine())!=null){
                    sb.append(line).append("\n");
                }
            } catch (Exception e){}
            finally {
                try {
                    is.close();
                    return sb.toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void inputStream2File(InputStream is, String fileName, String charSet){

    }

    public static void inputStream2File(InputStream is, String fileName){
        inputStream2File(is, fileName, defaultCharSet);
    }


}
