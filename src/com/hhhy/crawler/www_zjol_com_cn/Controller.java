package com.hhhy.crawler.www_zjol_com_cn;

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
 * Time: 下午6:32
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{

    public final String BASE_URL = "http://search.zjol.com.cn/";
    public Controller(){
    }

    @Override
    public void parseBoard() {
        Iterator<Map.Entry<String,String>> iterator = Crawler.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord, "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = GetHTML.getHtml("http://search.zjol.com.cn/?q="
                    + transKey + "&Submit=+&wseColumns=S1", "utf-8");

            html = html.replaceAll("&nbsp;", "");
            Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
            String flag = document.select("div#divDiss").select("div.divDis")
                    .text();
            if (flag.length() == 0) {
                // TODO ??
                System.out.println("nothing to found.....");
            } else {
                Elements tableEles = document.select("div#divDiss").select(
                        "div.divDis");
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
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        String website = "浙江在线";
        int type = 1;
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("a.dTit").text();
            String time = FormatTime.getTime(ele.select("p").last().text(),"(\\d+-\\d+-\\d+)",1);
            String summary = ele.select("p.ddpCon").text();

            String url = ele.select("a.dTit").attr("href");
            String content = Page.getContent(url, "div#c1", "gb2312");
            content = content.length()>0?content:Page.getContent(url, "p", "gb2312");
            System.out.println("TIME IS:"+time);
            System.out.println("type:" + type);
            System.out.println("title:" + title);
            System.out.println("content:" + content);
            System.out.println("url:" + url);
            System.out.println("time:" + time);
            System.out.println("summary:" + summary);
            System.out.println("website:" + website);
            System.out.println("----------------");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time)){
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
