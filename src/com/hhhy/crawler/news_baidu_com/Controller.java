package com.hhhy.crawler.news_baidu_com;

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
 * Created by Ghost on 2014/8/28 0028.
 * <p/>
 * █████▒█     ██     ▄████▄     ██  ▄█▀      ██████╗  ██╗      ██╗ ██████╗
 * ▓██    ▒  ██  ▓██ ▒▒██▀  ▀█    ██▄█▒        ██╔══██╗██║      ██║ ██╔════╝
 * ▒████  ▓██  ▒██  ░▒▓█           ▓███▄        ██████╔╝██║      ██║ ██║  ███╗
 * ▓█▒    ▓▓█  ░██  ░▒▓▓▄  ▄█   ▒▓██  █▄    ██╔══██╗██║      ██║ ██║   ██║
 * ▒█      ▒▒████▓  ▒  ▓███▀     ▒██▒  █▄    ██████╔╝╚██████╔╝╚██████╔╝
 * ▒          ░▓▒▒ ▒ ░ ░▒ ▒  ░▒ ▒▒ ▓▒             ╚═════╝    ╚═════╝    ╚═════╝
 * ░░        ▒░ ░ ░   ░  ▒   ░ ░▒ ▒░
 * ░ ░        ░░░ ░ ░ ░        ░ ░░ ░
 * ░          ░ ░      ░  ░
 * ░
 */
public class Controller extends CtrController {
    @Override
    public void parseBoard() {
        Iterator<Map.Entry<String,String>> iterator = this.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = GetHTML.getHtml("http://news.baidu.com/ns?word="+transKey+"&tn=news&from=news&cl=2&rn=20&ct=1","utf-8");
            html = html.replaceAll("&nbsp;", "");

            Document document = Jsoup.parse(html);

            Elements flag = document.select("div#content_left").select("ul").select("li");
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
        String website = "百度新闻搜索";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element li : (ArrayList<Element>) tableList) {
            String title = li.select("h3.c-title").text();
            String time = FormatTime.getTime(li.select("span.c-author").text(),"(\\d{4}-\\d{2}-\\d{2})",1);
            String summary = li.select("div.c-summary").text();
            String url = li.select("h3.c-title").select("a").attr("href");
            String content = Page.getAllHtmlContent(url);

            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if (Transmition.contentFilter(words, content, key, FNum) && Transmition.timeFilter(time, Crawl.spyHistory28, title)) {
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }
}
