package com.hhhy.crawler.finance_qq_com;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.hhhy.crawler.Crawl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Transmition;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;

/**
 * Created with IntelliJ IDEA. User: Ghost Date: 14-7-6 Time: 下午4:25 To change
 * this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
	public final String BASE_URL = "http://www.sogou.com/sogou";

    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }
    @Override
	public void parseBoard() {
    Iterator<Map.Entry<String,String>> iterator = this.keyWords.entrySet().iterator();
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
					.getHtml(
							"http://www.sogou.com/sogou?site=finance.qq.com&query="
									+ transKey
									+ "&pid=sogou-wsse-b58ac8403eb9cf17-0017&sourceid=&idx=f",
							"UTF-8");
			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			/*
			 * 搜索关键词是否存在
			 */

			Elements flag = document.select("div.wrap").select("div#wrapper")
					.select("div#main").select("div.results")
					.select("div[id].rb");
			if (flag.size() == 0) {
				// Todo ??
				System.out.println("nothing to show....");
			} else {
				Elements tableEles = document.select("div.wrap")
						.select("div#wrapper").select("div#main")
						.select("div.results").select("div[id].rb");
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
        String website = "腾讯财经";
        int type = 4;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>)tableList) {
            String title = ele.select("h3.pt").select("a").text();
            String time = FormatTime.getTime(ele.select("div.fb")
                    .select("cite").first().text(), "\\d{2}小时前");
            if (time == null || time.length() == 0 || time.contains("小时前") || time.contains("分钟前"))
                time = FormatTime.getCurrentFormatTime().split(" ")[0];
            String summary = ele.select("div.ft").text();
            String url = ele.select("h3.pt").select("a").attr("href");
            String content = summary;
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory5, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
