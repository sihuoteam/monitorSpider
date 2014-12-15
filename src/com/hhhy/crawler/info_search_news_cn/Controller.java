package com.hhhy.crawler.info_search_news_cn;

import com.hhhy.crawler.Crawler;
import com.hhhy.crawler.CtrController;
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
        Iterator<Map.Entry<String,String>> iterator = Crawler.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord,"GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = GetHTML.getHtml("http://info.search.news.cn/result.jspa?pno=1&t=1&t1=24&rp=10&t1=24&t=1&n1="+transKey+"&np=1&ss=2","gbk");
            html = html.replaceAll("&nbsp;", "");
            Document document = Jsoup.parse(html);

            Elements flag = document.select("div[align=left]");
            if (flag == null || flag.size()==0) {
                System.out.println("未搜到");
            } else {
                for(Element ele:flag){

                    String url = ele.select("a").attr("href");
                    String title = ele.select("a").text();
                    String src = ele.select(".style2a").text();
                    String summary =ele.select(".cc").text();
                    if(title.contains(keyWord) || summary.contains(keyWord)) {
                        if (src.length() < 10) continue;
                        String time = src.substring(src.length() - 10);
                        String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
                        if (!time.startsWith(time2)) continue;
                        long ctime = 0;
                        try {
                          ctime = DateFormatUtils.getTime(time, "yyyy-MM-dd");
                        } catch (ParseException e) {
                          e.printStackTrace();
                          continue;
                        }
                        int type = 1;
                        Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, "新华网", keyWord, 1);
                        Transmition.transmit(article);
                    }
                }
            }
        }
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {

    }

    public Controller() {
    }
}
