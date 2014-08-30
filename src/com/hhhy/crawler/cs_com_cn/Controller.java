package com.hhhy.crawler.cs_com_cn;

import com.hhhy.crawler.Crawler;
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
 * Created with IntelliJ IDEA. User: Ghost Date: 14-5-30 Time: 涓婂崍9:29 To change
 * this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
    public final String BASE_URL = "http://search.cs.com.cn/newsSimpleSearch.do?";

    public Controller() {
    }

    @Override
    public void parseBoard() {
        Iterator<Map.Entry<String, String>> iterator = Crawler.keyWords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String transKey = "";
            String keyWord = entry.getKey().split(";")[0];
            try {
                transKey = URLEncoder.encode(keyWord, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String url = "http://search.cs.com.cn/newsSimpleSearch.do?searchword="
                    + transKey + "&time=2&contentType=Content";
            String html = GetHTML.getHtml(url, "UTF-8");
            String html1 = html.replaceAll("&nbsp;", "");

            Document document = Jsoup.parse(html1);
            Element tableEles = document.select("table").last();
            String flag = tableEles.text();
            if (flag.contains("[没有检索到任何结果]")) {
                System.out.println("nothing to show...");
            } else {
                ArrayList<Element> tableList = new ArrayList<Element>();

                Elements tables = document.select("div:has(div.hei12)");
                for (Element ele : tables) {
                    tableList.add(ele);
                }
                System.out.println("搜索出" + tableList.size() + "个结果");
                parsePages(tableList, entry);
            }
        }

    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key =entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>) tableList) {
            String timeS = ele.select("tbody").select("td").last().text()
                    .replace("&nbsp", "");
            String title = ele.select("tbody").select("td").first().select("a")
                    .text();
            timeS = FormatTime.getTime(timeS, "(\\d{4}\\.\\d{2}\\.\\d{2} \\d{2}:\\d{2}:\\d{2})", 1).replaceAll("\\.", "-");
            String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
            System.out.println("页面上找到时间timeS: " + timeS);
            System.out.println("今天时间time: " + time2);
            if(!timeS.startsWith(time2)) continue;
            System.out.println("确认是今天的timeS: " + timeS);
            long time = 0;
            try {
                time = DateFormatUtils.getTime(timeS, "yyyy-MM-dd HH:mm:ss");
                System.out.println("转换格式的time: " + time);
                System.out.println("现在时间time: " + System.currentTimeMillis());
            } catch (ParseException e) {
                System.out.println(timeS);
            }
            String url = ele.select("tbody").select("td").first()
                    .select("a").attr("href");
            String summary = ele.select("div.hei12").text();
            String website = "中证网";
            String content = Page.getContent(url, "div#ozoom1",
                    "gb2312");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words, summary, content, key, FNum)){
//                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                System.out.println("存储时间："+time);
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}