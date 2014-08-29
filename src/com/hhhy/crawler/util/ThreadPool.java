package com.hhhy.crawler.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-5-20
 * Time: 下午12:48
 * To change this template use File | Settings | File Templates.
 */
public class ThreadPool {
    public static ExecutorService getThreadPool(int num){
        ExecutorService pool = Executors.newFixedThreadPool(num);
        return pool;
    }
}
