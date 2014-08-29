package com.hhhy.crawler.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MyLog {

    public static String LOGFILE = "myInfo.log";
    public static String FILE = "files/";
    public static final int MAXSIZE = 64*1024*1024;
    public static boolean DEBUG = true;

    public static void logINFO(String message) {
        if(DEBUG) {
            try {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                OutputStreamWriter osw = new OutputStreamWriter(
                        new FileOutputStream(LOGFILE, true), "UTF-8");
                BufferedWriter writer = new BufferedWriter(osw); //true代表追加

                writer.write("DEBUG [" + df.format(new Date()) + "] " + message + "\n");
                writer.close();
            } catch(IOException e) {
                e.printStackTrace(); //dddd
            }
        }
    }

    public static void deleteLogFile() {
        if(DEBUG) {
            File file = new File(LOGFILE);
            if(file.exists() && file.isFile()) {
                if(file.length() > MAXSIZE ) {
                    System.out.println("logFile size: " + Long.toString(file.length()));
                    file.delete();
                }
            }
        }
    }

    public static void saveFile(String fileContent, String fileName) {
        if(DEBUG) {
            try {
                OutputStreamWriter osw = new OutputStreamWriter(
                        new FileOutputStream(FILE + fileName), "UTF-8");
                BufferedWriter writer = new BufferedWriter(osw);
                writer.write(fileContent);
                writer.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
}
