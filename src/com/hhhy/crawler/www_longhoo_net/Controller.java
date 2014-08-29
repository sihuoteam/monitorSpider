package com.hhhy.crawler.www_longhoo_net;

import com.hhhy.crawler.Crawl;
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

public class Controller extends CtrController{
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
            String html = GetHTML.getHtml("http://house.longhoo.net/plus/search.php?kwtype=0&keyword=" + transKey, "UTF-8");
            html = html.replaceAll("&nbsp;", "");
            Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
            Elements flag = document.select("div.resultlist").select("div.item");
            if (flag.size() == 0) {
                // Todo ??
            } else {
                ArrayList<Element> tableList = new ArrayList<Element>();
                for (Element ele : flag) {
                    tableList.add(ele);
                }
                parsePages(tableList,entry);
            }
        }
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "龙虎网";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("h3").text();
            String time = "20"+FormatTime.getTime(ele.select("p.info").text(),"(\\d{2}-\\d{2}-\\d{2})",1);
            String summary = ele.select("p.intro").text();
            String url ="http://house.longhoo.net"+ele.select("h3").select("a").attr("href");
            String content = Page.getContent(url,"div.content_nr","utf-8");
            System.out.println("TIME IS :"+time);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(time!=null){
                if(Transmition.contentFilter(words, content, key, FNum) && Transmition.timeFilter(time, Crawl.spyHistory26, title)){
                    spyHistory.add(title);
                    Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                    //调接口~~~~~
                    Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                    Transmition.transmit(article);
                }
            }
        }
    }
}
