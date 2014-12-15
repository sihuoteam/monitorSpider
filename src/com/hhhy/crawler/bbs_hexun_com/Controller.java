package com.hhhy.crawler.bbs_hexun_com;

import com.hhhy.crawler.Crawler;
import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Page;
import com.hhhy.crawler.Transmition;
import com.hhhy.crawler.util.DateFormatUtils;
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
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
   
    public Controller() {
	}
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator= Crawler.keyWords.entrySet().iterator();
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
            }
            else{
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
            String timeS = "20"+ele.select("td").last().select("p").text()+":00";
            String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
            if(!timeS.startsWith(time2))continue;
            long time = 0;
            try {
                time = DateFormatUtils.getTime(timeS, "yy-MM-dd HH:mm");
            } catch (ParseException e) {
                System.out.println(timeS);
            }
            String summary = "";
            String url = ele.select("td.f14").select("a").attr("href");
            String content ="";

            if(url.length()>0){
                content = Page.getContent(url, "div.txtmain", "gb2312");
                summary=content.length()>20?content.substring(0,20):(content.length()>0?content:"");
            }

            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words, summary, content, key, FNum)){
//                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }

        }
    }
}
