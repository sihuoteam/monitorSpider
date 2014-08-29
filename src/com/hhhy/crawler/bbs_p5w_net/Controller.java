package com.hhhy.crawler.bbs_p5w_net;

import com.hhhy.crawler.Crawl;
import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Page;
import com.hhhy.crawler.Transmition;
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
    private final String BASE_URL = "http://search.discuz.qq.com/f/search?q=%E8%82%A1%E7%A5%A8%E4%BB%B7%E6%A0%BC&sId=5407638&ts=1405320195&mySign=9f5d7f39&menu=1&orderField=default&rfh=1&qs=txt.shome.a";
    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator= this.keyWords.entrySet().iterator();
    	while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];
    		String transKey = "";
    		try {
    			transKey = URLEncoder.encode(keyWord, "utf-8");
    		} catch (UnsupportedEncodingException e) {
    			e.printStackTrace();
    		}
    		String location = GetHTML.getHeaderValue("Location", "http://bbs.p5w.net/search.php?mod=my&q="+transKey);
    		String html = GetHTML.getHtml(location, "utf-8");
    		
    		html = html.replaceAll("&nbsp;","");
    		Document document = Jsoup.parse(html);
    		/*
        	搜索关键词是否存在
    		 */
    		Elements flag = document.select("div.result").select("span#result-items").select("ul").select("li");
    		if(flag.size()==0){
    			//Todo ??
    			System.out.println("nothing to found.....");
    		}
    		else{
    			Elements tableEles =  document.select("div.result").select("span#result-items").select("ul").select("li");
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
        int type = 2;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        String website = "全景社区";
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h3.title").select("a").text();
            String time = Subutils.getTime(ele.select("p.meta").last().text());
            String summary = ele.select("p.content").text();
            String url = ele.select("h3.title").select("a").attr("href");
            String content = Page.getContent(url, "div.pcb", "utf-8");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory2, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
