package com.hhhy.crawler.www_ce_cn;


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
 * Time: 下午5:37
 * To change this template use File | Settings | File Templates.
 */
public class Controller  extends CtrController{
    public final String BASE_URL = "http://www.so.com/s";
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
				transKey = URLEncoder.encode(keyWord, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String html = GetHTML.getHtml("http://www.so.com/s?q=" + transKey
					+ "&ie=gbk&src=zz_www_ce_cn&site=ce.cn&rg=1", "utf-8");

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			Elements flag = document.select("div#main").select("ul#m-result")
					.select("li.res-list");
			if (flag.size()==0) {
				// Todo ??
				System.out.println("nothing have found...");
			} else {
				ArrayList<Element> tableList = new ArrayList<Element>();
				for (Element ele : flag) {
					tableList.add(ele);
				}
				parsePages(tableList,entry);
			}
    	}
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "中国财经网";
        int type = 4;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h3.res-title").select("a").text();
            String time = FormatTime.getTime(ele.select("p.res-linkinfo").select("cite").text(),"(\\d+-\\d+-\\d+)",1);
            String summary = ele.select("h3.res-title + div").text();
            String url = ele.select("h3.res-title").select("a").attr("href");

            String content = summary;
            ArrayList<Integer> FNum = new ArrayList<Integer>();

            if(Transmition.contentFilter(words,summary,content,key,FNum) && Transmition.timeFilter(time)){
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
