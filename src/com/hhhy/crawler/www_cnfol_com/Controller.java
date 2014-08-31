package com.hhhy.crawler.www_cnfol_com;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import com.hhhy.crawler.*;
import com.hhhy.crawler.util.DateFormatUtils;
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

           String html = GetHTML.getHtml("http://so.cnfol.com/cse/search?q="+
                   transKey+"&s=12596448179979580087&srt=lds&sti=1440&nsid=0","utf-8");

//			String html = GetHTML.getHtml("http://so.cnfol.com/cse/search?q="
//					+ transKey + "&s=12596448179979580087", "UTF-8");

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			/*
			 * 搜索关键词是否存在
			 */
			Elements elements = document.select("#results").select(".result");
			if (elements.size()==0) {
				// Todo ??
			} else {


				ArrayList<Element> tableList = new ArrayList<Element>();
				for (Element ele : elements) {
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
                    .text(), "(\\d{4}-\\d{1,2}-\\d{1,2})");
            if(time==null)continue;
            long ctime = 0;
            try {
                ctime = DateFormatUtils.getTime(time,"yyyy-MM-dd");
            } catch (ParseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                System.out.println(time);
                continue;
            }
            if(ctime==0)continue;
            time  = DateFormatUtils.formatTime(ctime,"yyyy-MM-dd");
            String today = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
//            System.out.println("format: "+time+" today"+ today);
            if(!today.equals(time))continue;
//            time = time==null?FormatTime.getTime(FormatTime.getCurrentFormatTime(), "(\\d{4}-\\d{2}-\\d{2})",1):time;
            String summary = ele.select("div").select("div.c-content")
                    .select("div.c-abstract").text();
            String url = ele.select("h3.c-title").select("a")
                    .attr("href");
            String content = Page.getContent(url, "div#__content",
                    "utf-8");
//            System.out.println(key+"title:"+title+"time:"+time);

            ArrayList<Integer> FNum = new ArrayList<Integer>();

            if(Transmition.contentFilter(words,summary,content,key,FNum)){
//                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, System.currentTimeMillis(), summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
