package com.hhhy.crawler.www_chinacenn_com;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-2
 * Time: 下午2:44
 * To change this template use File | Settings | File Templates.
 */


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.hhhy.crawler.Crawl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hhhy.crawler.CtrController;
import com.hhhy.crawler.Page;
import com.hhhy.crawler.Transmition;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;

public class Controller extends CtrController {
    public final String BASE_URL = "http://www.chinacenn.com/SerchList.aspx";

    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }

    @Override
    public void parseBoard() {
        Iterator<Map.Entry<String, String>> iterator = this.keyWords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String transKey = "";
            String keyWord = entry.getKey().split(";")[0];
            try {
                transKey = URLEncoder.encode(keyWord, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            String html = GetHTML.getHtml(
                    "http://www.chinacenn.com/SerchList.aspx?Strtitle="
                            + transKey, "UTF-8");

            html = html.replaceAll("&nbsp;", "");
            Document document = Jsoup.parse(html);

            String flag = document.select("form#form1").select("div#y1_t4")
                    .select("div.y2t4_right").select("div.y2t4_wz").text();
            if (flag.equals("")) {
                // Todo ??
                System.out.println("nothing have found...");
            } else {
                Elements tableEles = document.select("form#form1")
                        .select("div#y1_t4").select("div.y2t4_right")
                        .select("div.y2t4_wz").select("div.y2_tpp1");
                ArrayList<Element> tableList = new ArrayList<Element>();
                for (Element ele : tableEles) {
                    tableList.add(ele);
                }
                parsePages(tableList, entry);
            }
        }

    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "中国企业新闻";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for (Element ele : (ArrayList<Element>) tableList) {
            String title = ele.select("div.y2_wz11").select("h4.STYLE5").select("a").text();
            String time = Subutils.getTime(ele.select("div.y2_wz11").select("h5").text());
            String summary = ele.select("div.y2_wz11").select("h6").text();
            String url = "http://www.chinacenn.com/" + ele.select("div.y2_wz11").select("h4.STYLE5").select("a").attr("href");
            String content = Page.getContent(url, "span#zoom", "utf-8");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory13, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}

