package com.hhhy.crawler.www_djtz_net;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.hhhy.crawler.Crawl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Page;
import com.hhhy.crawler.Transmition;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
    private final String BASE_URL = "http://search.discuz.qq.com/f/discuz?mod=forum&formhash=4c7b8745&srchtype=title&srhfid=0&srhlocality=portal%3A%3Aindex&sId=20541128&ts=1405322218&cuId=0&cuName=&gId=7&agId=0&egIds=&fmSign=&ugSign7=&sign=4b6eeca00d0cce45b5df4253fbb9ffc7&charset=gbk&source=discuz&fId=0&q="+"&srchtxt=%CD%B6%D7%CA&searchsubmit=true";
    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator = this.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String transKey = "";
            String keyWord = entry.getKey().split(";")[0];
			try {
				transKey = URLEncoder.encode(keyWord, "gbk");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String location = GetHTML.getHeaderValue("Location",
					"http://www.djtz.net/search.php?mod=my&q=" + transKey);
			String html = GetHTML.getHtml(location, "utf-8");
			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
			Elements flag = document.select("span#result-items").select("ul")
					.select("li");
			if (flag.size() == 0) {
				// TODO ??
				System.out.println("nothing to found.....");
			} else {
				Elements tableEles = document.select("div.result")
						.select("span#result-items").select("ul").select("li");
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
        String website = "点金投资";
        int type = 2;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h3.title").select("a").text();
            String time = FormatTime.getTime(ele.select("p.meta").last().text(),"(\\d{4}-\\d{2}-\\d{2})",1);
            String summary = ele.select("p.content").text();
            String url = ele.select("h3.title").select("a").attr("href");

            String content = Page.getContent(url, "div#jiathis_share_CODE_HTML3", "gbk");
            System.out.println("TIME IS :"+time);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory17, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
