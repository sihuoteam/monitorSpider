package com.hhhy.crawler.finance_ifeng_com;

import com.hhhy.crawler.Crawler;
import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Page;
import com.hhhy.crawler.Transmition;
import com.hhhy.crawler.util.DateFormatUtils;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: Ghost Date: 14-6-6 Time: 涓嬪崍3:12 To change
 * this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
	public final String BASE_URL = "http://search.ifeng.com/sofeng/search.action?";

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

			String html = GetHTML.getHtml(
                    "http://search.ifeng.com/sofeng/search.action?q="
                            + transKey + "&c=1", "UTF-8");
			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			String flag = document.select("div.mainContent")
					.select("div.mainM").select("h1").text();
			if (flag.equals("找不到和您的查询相符的网页")) {
				// Todo ??
				System.out.println("nothing to show");
			} else {
				Elements tableEles = document.select("div.mainContent")
						.select("div.mainM").select("div.searchResults");
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
        int type = 1;
        String website = "凤凰网";
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele :(ArrayList<Element>) tableList) {
            String title = ele.select("p").first().text();
            String orgTime = ele.select("p").get(2).text();
            String timeS = FormatTime.getTime(tool.getTimeStr(orgTime), "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
            if(!timeS.startsWith(time2))continue;
            long time = 0;
            try {
                time = DateFormatUtils.getTime(timeS, "yyyy-MM-dd HH:mm:ss");
            } catch (ParseException e) {
                System.out.println(timeS);
            }
            String summary = ele.select("p").get(1).text();
            String url = ele.select("p").first().select("a")
                    .attr("href");
            String content = Page.getContent(url, "div#artical",
                    "utf-8");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words, summary, content, key, FNum)){
                Transmition.showDebug(type, title, content, url, ""+time, summary, website, FNum.get(0));

                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
