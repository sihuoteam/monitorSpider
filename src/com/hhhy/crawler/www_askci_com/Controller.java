package com.hhhy.crawler.www_askci_com;

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
 * Date: 14-7-6
 * Time: 下午3:47
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
    public final String BASE_URL = "http://www.askci.com/";
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
				transKey = URLEncoder.encode(keyWord, "utf-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String html = GetHTML.getHtml(
					"http://www.askci.com/search/1/zixun/" + transKey + "/",
					"utf-8");
			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
			Elements flag = document.select("div.reportlist").select("ul");
			if (flag.size() == 0) {
				// TODO ??
				System.out.println("nothing to found.....");
			} else {
				Elements tableEles = flag.select("li");
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
        String website = "中商网";
        int type= 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h2").text();
            String time = FormatTime.getTime(FormatTime.getCurrentFormatTime(), "\\d{4}-\\d{2}-\\d{2}");
            String summary = ele.select("div.listcontext").select("p").text();
            String url = ele.select("h2").select("a").attr("href");
            String content = Page.getContent(url, "div.detail_left", "utf-8");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory10, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
