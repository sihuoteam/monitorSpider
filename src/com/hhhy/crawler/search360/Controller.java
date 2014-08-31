package com.hhhy.crawler.search360;

import com.hhhy.crawler.*;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
   
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
                transKey = URLEncoder.encode(keyWord, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = GetHTML.getHtml("http://news.so.com/ns?j=0&rank=pdate&src=srp&q="+transKey+"&pn=1", "utf-8");

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);
    	        /*
    	        搜索关键词是否存在
    	         */
            Elements flag = document.select("ul#news").select("li");
            if(flag.size()==0){
                //TODO ??
                System.out.println("nothing to found.....");
            }
            else{
                ArrayList<Element> tableList = new ArrayList<Element>();
                for(Element ele:flag){
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
        int type = 2;
        String website = "360搜索";
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h3").text();
            String time = FormatTime.getTime(ele.select("h3").select("span").attr("title"),"(\\d+-\\d+-\\d+)",1);
            String summary = ele.select("p").text();
            String url = ele.select("h3").select("a").attr("href");
            String content = Page.getAllHtmlContent(url);
            System.out.println("TIME IS :"+time);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,summary,content,key,FNum) && Transmition.timeFilter(time)){
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }

        }
    }
    public static void main(String[] args){
        System.out.println(GetHTML.getHtml("http://news.so.com/ns?q=%E6%8A%95%E8%B5%84&src=newhome","utf-8"));
    }
}
