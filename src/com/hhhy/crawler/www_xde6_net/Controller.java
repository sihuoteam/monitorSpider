package com.hhhy.crawler.www_xde6_net;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.hhhy.crawler.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-4
 * Time: 下午1:58
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{
    public Controller() {
    }
    @Override
    public void parseBoard() {
        Iterator<Map.Entry<String,String>> iterator = Crawler.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String transKey = "";
            String keyWord = entry.getKey().split(";")[0];
            try {
                transKey = URLEncoder.encode(keyWord, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = GetHTML.getHtml(
                    "http://www.xde6.net/s?w=" + transKey, "UTF-8");

            html = html.replaceAll("&nbsp;", " ");
            Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
            Elements flag = document.select("div#result").select("ol")
                    .select("li");
            if (flag.size() == 0) {
                // Todo ??
                System.out.println("nothing to show....");
            } else {
                Elements tableEles = document.select("div#main")
                        .select("div#r").select("div#result").select("ol")
                        .select("li[loc]");
                ArrayList<Element> tableList = new ArrayList<Element>();
                for (Element ele : tableEles) {
                    tableList.add(ele);
                }
                parsePages(tableList, entry);
            }
        }

    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "西电新闻网";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];

        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h3").select("a").text();
            String time = FormatTime.getTime(ele.select("div.result_summary").select("div.url").select("cite").text(), "(\\d{4}-\\d+-\\d+)",1);
            String summary = ele.select("p.ds").text() ;
            String url =ele.select("h3").select("a").attr("href").contains("http")?
                    ele.select("h3").select("a").attr("href"):
                    "http://www.xde6.net"+ele.select("h3").select("a").attr("href");
            String content = Page.getContent(url, "div#wz_zw", "utf-8");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            System.out.println("TIME IS :"+time);
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time)){
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
