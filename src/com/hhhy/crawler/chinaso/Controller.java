package com.hhhy.crawler.chinaso;

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

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
   
    public Controller(HashMap<String, String> kW, LinkedList<String> spyHistory) {
    	super(kW,spyHistory);
	}
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator= this.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = GetHTML.getHtml("http://news.chinaso.com/search?wd="+transKey, "utf-8");

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);
    	        /*
    	        搜索关键词是否存在
    	         */
            Elements flag = document.select("ol.results").select("li");
            if(flag.size()==0){
                //TODO ??
                System.out.println("nothing to found.....");
            }
            else{
                ArrayList<Element> tableList = new ArrayList<Element>();
                for(Element ele:flag){
                    tableList.add(ele);
                }
                parsePages(tableList,entry);
            }
        }
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String key = entry.getKey().split(";")[0];
        String[] words = entry.getValue().split(";");
        int type = 1;
        String website = "中国搜索";
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("div.newsList").select("td").select("div.T1").text();
            String time = FormatTime.getTime(ele.select("div.newsList").select("td>div").text(), "(\\d{4}-\\d{2}-\\d{2})", 1);
            if(time == null && (ele.select("div.newsList").select("td>div").last().text().contains("小时前")|| ele.select("div.newslist").select("td>div").last().text().contains("天前")))
                time = FormatTime.getTime(FormatTime.getCurrentFormatTime(),"(\\d+-\\d+-\\d+)",1);
            String summary = ele.select("p").text();
            String url = ele.select("div.newsList").select("td").select("div.T1").select("a").attr("href");
            System.out.println("title:"+title);
            System.out.println("url:"+url); System.out.println("TIME IS :"+time);
            if(url.contains("http")){
                String content = Page.getAllHtmlContent(url);

                ArrayList<Integer> FNum = new ArrayList<Integer>();
                if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory1, title)){
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
