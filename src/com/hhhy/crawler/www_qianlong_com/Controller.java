package com.hhhy.crawler.www_qianlong_com;


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
 * Date: 14-7-3
 * Time: 上午10:45
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{
    public final String BASE_URL = "http://www.chinaso.com/search/pagesearch.htm";
    public Controller() {
    }
    @Override
    public void parseBoard(){
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
			String html = GetHTML.getHtmlGzip(
					"http://www.chinaso.com/search/pagesearch.htm?q="
							+ transKey, "UTF-8");
			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
			String flag = document.select("li.reItem").text();
			if (flag.length() == 0) {
				// Todo ??
			} else {
				Elements tableEles = document.select("li.reItem");
				ArrayList<Element> tableList = new ArrayList<Element>();
				for (Element ele : tableEles) {
					tableList.add(ele);
				}
				parsePages(tableList,entry);
			}
    	}
        
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "千龙网";
        int type = 4;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h2").select("a").text();
            String time = FormatTime.getTime(ele.select("p.snapshot").text(), "(\\d{4}-\\d+-\\d+)",1);
            if(time!=null){
                String summary = ele.select("div").select("p").first().text();
                String url = "http://www.chinaso.com"+ele.select("h2").select("a").attr("href");
                String content = Page.getAllHtmlContent(url);
                ArrayList<Integer> FNum = new ArrayList<Integer>();
                System.out.println("TIME IS :"+time);
                System.out.println("type:" + type);
                System.out.println("title:" + title);
                System.out.println("content:" + content);
                System.out.println("url:" + url);
                System.out.println("time:" + time);
                System.out.println("summary:" + summary);
                System.out.println("website:" + website);
                System.out.println("----------------");
                if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time)){
                    Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                    //调接口~~~~~
                    Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                    Transmition.transmit(article);
                }
            }
        }
    }

}
