package com.hhhy.crawler.www_jrj_com_cn;


import com.hhhy.crawler.*;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-2
 * Time: 下午3:13
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{
    private final String BASE_URL = "http://jinsoo.jrj.com.cn/search.jsp";
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
            String html = GetHTML.getHtml("http://jinsoo.jrj.com.cn/search.jsp?q="+transKey+"A&ca=news&o=time&qf=ti","gb2312");
            Document document = Jsoup.parse(html);
            Elements flags = document.select("ul.newlist").select("li");
            if(flags.size()==0){
                //Todo ??
            }
            else{
                ArrayList<Element> tableList = new ArrayList<Element>();
                for(Element ele:flags){
                    tableList.add(ele);
                }
                parsePages(tableList,entry);
            }

        }
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "金融界";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element li : (ArrayList<Element>) tableList) {
            String title = li.select("p").first().text();
            String time = FormatTime.getTime(li.select("p").last().text(), "(\\d{4}-\\d{2}-\\d{2})", 1);
            String summary = li.select("p").get(1).text();
            String url = li.select("p").first().select("a").last().attr("href");
            String content = Page.getContent(url, "div.text-col", "gbk");
            System.out.println("TIME IS:"+time);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if (Transmition.contentFilter(words, content, key, FNum) && Transmition.timeFilter(time)) {
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
