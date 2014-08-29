package com.hhhy.crawler.www_china_com_cn;


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

import javax.swing.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-4
 * Time: 上午10:54
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
    public final String BASE_URL = "http://www.baidu.com/baidu";
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

            String html = GetHTML.getHtml(
                    "http://www.baidu.com/baidu?tn=baidu&word=" + transKey,
                    "UTF-8");

            html = html.replaceAll("&nbsp;", "");
            Document document = Jsoup.parse(html);

            Elements flagEles = document.select("div#wrapper")
                    .select("div#wrapper_wrapper").select("div#container")
                    .select("div#content_left").select("div[id]");

            if (flagEles.size() == 0) {
                // Todo ??
                System.out.println("nothing have found...");
            } else {
                Elements tableEles = document.select("div#wrapper")
                        .select("div#wrapper_wrapper").select("div#container")
                        .select("div#content_left").select("div[id]");
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
        String website = "中国网";
        int type = 4;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h3.t").select("a").text();
            String time = FormatTime.getTime(ele.select("span.g").text(),"\\d+-\\d+-\\d+");
            String summary = ele.select("div.c-abstract").text();
            String url = ele.select("h3.t").select("a").attr("href");
            String content = Page.getAllHtmlContent(url);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory12, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
