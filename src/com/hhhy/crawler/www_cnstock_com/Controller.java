package com.hhhy.crawler.www_cnstock_com;

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
	public final String BASE_URL = "http://search.cnstock.com/Search.aspx";

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

			String html = GetHTML
					.getHtml("http://search.cnstock.com/search/result?t=2&k="
							+ transKey, "UTF-8"); // http://search.cnstock.com/search/result?k=%E8%82%A1%E7%A5%A8

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			Elements flag = document.select("div.result-article");
			if (flag.size() == 0) {
				// Todo ??
				System.out.println("nothing to show");
			} else {
				Elements tableEles = document.select("div.result-article");
				ArrayList<Element> tableList = new ArrayList<Element>();
				for (Element ele : tableEles) {
					tableList.add(ele);
				}
				System.out.println(tableEles.size());
				parsePages(tableList,entry);
			}
		}

	}

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "上海证券";
        int type = 3;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>)tableList) {
            String title = ele.select("h3.t").text();
            String time = FormatTime.getTime(ele.select("p.link")
                    .select("span").text(), "(\\d+-\\d+-\\d+)",1);
            String summary = ele.select("p.des").text();
            String url = ele.select("h3.t").select("a").attr("href");

            String content = Page.getContent(url, "div.logtext",
                    "gb2312");
            content = content.length() > 0 ? content : Page.getContent(
                    url, "div.storycontent", "gb2312");
            content = content.length() > 0 ? content : Page.getContent(
                    url, "p", "gb2312");
            System.out.println("TIME IS :"+time);
            System.out.println("type:" + type);
            System.out.println("title:" + title);
            System.out.println("content:" + content);
            System.out.println("url:" + url);
            System.out.println("time:" + time);
            System.out.println("summary:" + summary);
            System.out.println("website:" + website);
            System.out.println("----------------");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,summary,content,key,FNum) && Transmition.timeFilter(time)){
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
