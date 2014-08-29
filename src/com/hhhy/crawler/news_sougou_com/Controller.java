package com.hhhy.crawler.news_sougou_com;

import com.hhhy.crawler.Crawl;
import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Page;
import com.hhhy.crawler.Transmition;
import com.hhhy.crawler.util.DateFormatUtils;
import com.hhhy.crawler.util.FormatTime;
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
        Iterator<Map.Entry<String,String>> iterator = this.keyWords.entrySet().iterator();
      System.out.println("kaishi");
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String html = GetHTML.getHtml("http://news.sogou.com/news?time=0&query="+transKey+"&mode=1&sort=1","GBK");
            html = html.replaceAll("&nbsp;", "");

            Document document = Jsoup.parse(html);

            Elements flag = document.select(".results").select(".rb");
            if (flag.size()==0) {
                System.out.println("未搜到");
            } else {
                for(Element ele:flag){
                    String url = ele.select("a").attr("href");
                  String title = ele.select("a").text();
                  String src = ele.select("cite").text();
                  String summary =ele.select(".thumb_news").text();
                  if(title.contains(keyWord) || summary.contains(keyWord)) {
                    System.out.println(entry.getKey().split(";")[0] + "url: " + url + ", title: " + title + ", src: " + src + ", summary: " + summary);
                    if (src.length() < 16) continue;
                    String time = src.substring(src.length() - 16);
                    String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
                    if (!time.startsWith(time2)) continue;
                    System.out.println("time: " + time);
                    long ctime = 0;
                    try {
                      ctime = DateFormatUtils.getTime(time, "yyyy-MM-dd hh:mm");
                    } catch (ParseException e) {
                      e.printStackTrace();
                      continue;
                    }
                    System.out.println("搜狗time: " + ctime);
                    int type = 1;
                    Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, "搜狗新闻搜索", keyWord, 1);
                    Transmition.transmit(article);
                  }
                }
//                parsePages(tableList,entry);
            }
        }
      System.out.println("************************8");
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {

    }

    public Controller(HashMap<String, String> kW, LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }
}
