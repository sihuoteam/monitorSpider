package com.hhhy.crawler.www_financialnews_com_cn;

import com.hhhy.crawler.*;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class Controller extends CtrController{
    @Override
    public void parseBoard() {
        Iterator<Map.Entry<String,String>> iterator = Crawler.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];

            HashMap<String,String> param = new HashMap<String, String>();
            param.put("searchword",keyWord);
            param.put("channelid","234439");
            HashMap<String,String> headParam = new HashMap<String, String>();
            headParam.put("Referer","http://220.194.54.119:8080/was5/web/search");
            String html = GetHTML.postHtml("http://220.194.54.119:8080/was5/web/search","UTF-8",param,headParam);
            html = html.replaceAll("&nbsp;", "");

            Document document = Jsoup.parse(html);

            Elements flag = document.select("td.searchresult").select("ol").select("li");
            if (flag.size()==0) {
                // Todo ??
                System.out.println("未搜到");
            } else {
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
        String website = "中国金融新闻网";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element li : (ArrayList<Element>) tableList) {
            String title = li.select("div").first().text();

            String time = FormatTime.getTime(li.select("div").text(), "(\\d{4}\\.\\d{2}\\.\\d{2}\\s\\d{2}:\\d{2}:\\d{2})", 1);
            String summary = li.select("div").last().text();
            String url = li.select("div").first().select("a").attr("href");
            String content = Page.getContent(url, "div.TRS_Editor", "utf-8");

            ArrayList<Integer> FNum = new ArrayList<Integer>();

            if (Transmition.contentFilter(words,summary, content, key, FNum) && Transmition.timeFilter(time)) {
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
