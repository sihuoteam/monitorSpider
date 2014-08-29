package com.hhhy.crawler.bbs_hexun_com;

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
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
   
    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
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
            String html = GetHTML.getHtml("http://bbs.hexun.com/search/?q=" + transKey + "&type=1&Submit=", "gb2312");

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);
    	        /*
    	        搜索关键词是否存在
    	         */
            Elements flag = document.select("tr.bg");
            if(flag.size()==0){
                //TODO ??
                System.out.println("nothing to found.....");
            }
            else{
                System.out.println("found");
                Elements tableEles =  document.select("tr.bg");
                ArrayList<Element> tableList = new ArrayList<Element>();
                for(Element ele:tableEles){
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
        int type = 2;
        String website = "和讯论坛";
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("td.f14").select("a").text();
            String time = "20"+ele.select("td").last().select("p").text()+":00";
            String summary = "";
            String url = ele.select("td.f14").select("a").attr("href");
            String content ="";

            if(url.length()>0){
                content = Page.getContent(url, "div.txtmain", "gb2312");
                summary=content.length()>20?content.substring(0,20):(content.length()>0?content:"");
            }

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
