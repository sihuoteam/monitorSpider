package com.hhhy.crawler.news_search_hexun_com;

import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Page;
import com.hhhy.crawler.Transmition;
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
 * Created with IntelliJ IDEA. User: Ghost Date: 14-7-8 Time: 下午3:36 To change
 * this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
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
				transKey = URLEncoder.encode(keyWord, "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String html = GetHTML.getHtml(
					"http://news.search.hexun.com/cgi-bin/search/info_search.cgi?f=0&key="
							+ transKey + "&s=1&pg=1&t=0&rel=", "gb2312");

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
			Elements flag = document.select("div.list");
			if (flag.size() == 0) {
				// Todo ??
				System.out.println("nothing to found.....");
			} else {
				Elements tableEles = document.select("div.list > ul").select(
						"li");
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
        String website = "和讯网";
        int type = 2;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>)tableList) {
            String title = ele.select("div.ul_t").select("h3").select("a")
                    .text();
            String time = FormatTime.getTime(ele.select("div.ul_t")
                    .select("h4").text(), "\\d+分钟前");
            if (time == null || time.length() == 0 || time.contains("分钟前") || time.contains("小时前")	)
                time = FormatTime.getCurrentFormatTime().split(" ")[0];
            String summary = ele.select("div.cont").text();
            String url = ele.select("div.ul_t").select("h3")
                    .select("a").attr("href");
            String content = Page.getContent(url, "div#artibody",
                    "gb2312");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, this.spyHistory, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
