package com.hhhy.crawler.www_chinanews_com;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.hhhy.crawler.Crawl;
import com.hhhy.crawler.Page;
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
 * Created with IntelliJ IDEA. User: Ghost Date: 14-7-3 Time: 下午5:59 To change
 * this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
	public final String BASE_URL = "http://sou.chinanews.com/search.do";

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

			String html = "error";
			int i = 0;
			for (i = 0; i < 5 && html.equals("error"); i++) {
				html = GetHTML.getHtml("http://sou.chinanews.com/search.do?q="
						+ transKey, "UTF-8");
			}
			if (i < 5) {
				html = html.replaceAll("&nbsp;", "");
				Document document = Jsoup.parse(html);

				String flag = document.select("table[style]").first()
						.select("tbody").select("div#news_list").select("span")
						.text();
				if (flag.contains("对不起，没有找到相关内容，请更换关键字后重试")) {
					// Todo ??
					System.out.println("nothing have found...");
				} else {
					Elements tableEles = document.select("table[style]")
							.first().select("tbody").select("div#news_list")
							.select("table");
					ArrayList<Element> tableList = new ArrayList<Element>();
					for (Element ele : tableEles) {
						tableList.add(ele);
					}
					parsePages(tableList,entry);
				}
			}

		}

	}

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "中国企业新闻";
        int type = 4;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>)tableList) {
            String title = ele.select("li.news_title").select("a").text();
            String time = FormatTime.getTime(
                    ele.select("li.news_other").text(), "\\d{4}-\\d{2}-\\d{2}");
            String summary = ele.select("li.news_content").text();
            String url = ele.select("li.news_title").select("a")
                    .attr("href");
            String content = Page.getAllHtmlContent(url);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory14, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
