package com.hhhy.crawler.www_stcn_com;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-6-4
 * Time: 下午3:57
 * To change this template use File | Settings | File Templates.
 */


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

public class Controller extends CtrController {
    public final String BASE_URL = "http://app.stcn.com/?app=search&controller=index&action=search&type=article&wd=%E8%AF%81%E5%88%B8&advanced=1&catid=1&order=time&before=2014-06-04&after=2014-06-05";
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
			String before = FormatTime.getCurrentFormatTime().split(" ")[0];
			String after = FormatTime.getFormatTimeAfterXDays(1).split(" ")[0];

			String html = GetHTML
					.getHtml(
							"http://app.stcn.com/?app=search&controller=index&action=search&type=article&wd="
									+ transKey
									+ "&advanced=1&catid=1&order=time&before="
									+ before + "&after=" + after, "UTF-8");

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			String flag = document.select("form#form1").attr("action");
			if (flag.equals("SearchNotFound.aspx")) {
				// Todo ??
			} else {
				Elements tableEles = document.select("div#search_list").select(
						"dl");
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
        String website = "证券时报";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("dt").select("a").text();
            String time = FormatTime.getTime(ele.select("dd").last().select("span").first().text(),"(\\d+-\\d+-\\d+)",1);
            String summary = ele.select("dd.info").text();
            String url = ele.select("dt").select("a").attr("href");
            String content = Page.getContent(url, "div#ctrlfscont", "utf-8");
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

