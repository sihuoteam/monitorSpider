package com.hhhy.crawler.guba_hexun_com;

import com.hhhy.crawler.*;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: Ghost Date: 14-7-8 Time: 下午3:36 To change
 * this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
	private final String BASE_URL = "http://news.search.hexun.com/cgi-bin/search/info_search.cgi?f=1&key=%B9%C9%C6%B1&s=1&pg=1&t=0&rel=";

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
				transKey = URLEncoder.encode(keyWord, "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String html = GetHTML.getHtml(
                    "http://news.search.hexun.com/cgi-bin/search/info_search.cgi?f=1&key="
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
        String website = "和讯财经";
        int type = 2;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>)tableList) {
            String title = ele.select("div.ul_t").select("h3").select("a")
                    .text();
//            String time = FormatTime.getTime(ele.select("div.ul_t")
//                    .select("h4").text(), "\\d+分钟前");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String timeContent = ele.select("div.ul_t").select("h4").text();
            String timeS = FormatTime.getTime(timeContent, "\\d+分钟前");

            if(timeS == null || timeS.equals("")){
                timeS = FormatTime.getTime(timeContent, "\\d+小时前");
                if(timeS == null || timeS.equals("")){
                    timeS = FormatTime.getTime(timeContent, "\\d{4}年\\d{2}月\\d{2}日");
                    if(timeS == null || timeS.equals("")){timeS = null;}
                    else {timeS = timeS.replaceAll("年","-").replaceAll("月","-").replace("日","");}
                }
            }
            long time = 0;
            if(timeS != null) {
                if (timeS.contains("分钟前")) {
                    timeS = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
                } else if (timeS.contains("小时前")) {
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    timeS = sdf2.format(new Date());
                    String day = timeS.split(":")[0].split(" ")[0];
                    long timeH = Long.valueOf(timeS.split(":")[0].split(" ")[1]);
                    String f = timeS.split(":")[1];
                    String m = timeS.split(":")[2];
                    long timeHN = Long.valueOf(FormatTime.getTime(timeContent, "\\d+(?=小时前)"));
                    timeHN = timeH - timeHN;
                    timeS = day + " " + timeHN + ":" + f + ":" + m;
                }

                String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
                if(!timeS.startsWith(time2))continue;
                try {
                    time = DateFormatUtils.getTime(timeS, "yyyy-MM-dd HH:mm:ss");
                } catch (ParseException e) {
                    System.out.println(timeS);
                }
            }


            String summary = ele.select("div.cont").text();
            String url = ele.select("div.ul_t").select("h3")
                    .select("a").attr("href");
            String content = Page.getContent(url, "div#artibody",
                    "gb2312");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words, summary, content, key, FNum)){
                Transmition.showDebug(type, title, content, url, ""+time, summary, website, FNum.get(0));
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
