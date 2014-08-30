package com.hhhy.crawler.www_cnfol_com;

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
 * Created with IntelliJ IDEA. User: Ghost Date: 14-6-3 Time: 下午5:12 To change
 * this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
	public final String BASE_URL = "http://so.cnfol.com/cse/search";

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

			String html = GetHTML.getHtml("http://so.cnfol.com/cse/search?q="
					+ transKey + "&s=12596448179979580087", "UTF-8");

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			/*
			 * 搜索关键词是否存在
			 */
			String flag = document.select("div#wrap").select("div#container")
					.select("div#center").select("div#results").text();
			if (flag.equals("抱歉，没有找到")) {
				// Todo ??
			} else {
				Elements tableEles = document.select("div#wrap")
						.select("div#container").select("div#center")
						.select("div#results").select("div.result");

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
        String website = "中金在线 ";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>) tableList) {
            String title = ele.select("h3.c-title").select("a").text();
            String time = FormatTime.getTime(ele.select("span.c-showurl")
                    .text(), "(\\d{4}-\\d{2}-\\d{2})",1);
            time = time==null?FormatTime.getTime(FormatTime.getCurrentFormatTime(), "(\\d{4}-\\d{2}-\\d{2})",1):time;
            String summary = ele.select("div").select("div.c-content")
                    .select("div.c-abstract").text();
            String url = ele.select("h3.c-title").select("a")
                    .attr("href");
            String content = Page.getContent(url, "div#__content",
                    "utf-8");

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
