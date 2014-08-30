package com.hhhy.crawler.www_caijing_com_cn;


import com.hhhy.crawler.*;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.lang.String;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-6
 * Time: 下午4:50
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{
    public final String BASE_URL = "http://search.caijing.com.cn/search.jsp";
    public Controller() {
    }
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator= Crawler.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            HashMap<String,String> params = new HashMap<String, String>();
            params.put("item", "3");
            params.put("key",keyWord);
            params.put("range","0");
            HashMap<String,String> headParams = new HashMap<String, String>();
            headParams.put("Referer","http://www.caijing.com.cn/");
            String html = null;
            html = GetHTML.postHtml("http://search.caijing.com.cn/search.jsp","utf-8",params,headParams);

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);


            Elements flag = document.select("div.searchtext").select("ul").select("li");
            System.out.println("keyword: "+keyWord);
            if(flag.size()==0){
                //TODO ??
                System.out.println("nothing to found.....");
                System.out.println(html);
            }
            else{
                Elements tableEles = document.select("div.searchtext").select("ul").select("li");
                ArrayList<Element> tableList = new ArrayList<Element>();
                for(Element ele:tableEles){
                    tableList.add(ele);
                }
                parsePages(tableList,entry);
            }
        }
    }
    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String key = entry.getKey().split(";")[0];
        String[] words = entry.getValue().split(";");
    	String website = "财经网";
    	int type = 1;
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("a").text();
            System.out.println(ele.select("span").text());
            String time = FormatTime.getTime(ele.select("span").text(),"\\d{4}-\\d{2}-\\d{2}");
            String summary = ele.select("p").text();
            String url = ele.select("a").attr("href");
            String content = Page.getContent(url,"div#the_content","UTF-8");
            System.out.println("title: "+title);
            System.out.println("time: "+time);
            System.out.println("summary: "+summary);
            System.out.println(content);
            System.out.println("url: "+url);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,summary, content, key, FNum) && Transmition.timeFilter(time)){
//                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
