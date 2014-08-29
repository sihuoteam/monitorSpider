package com.hhhy.crawler.www_eeo_com_cn;

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
 * Date: 14-6-9
 * Time: 上午11:15
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{
    public final String BASE_URL = "http://app.eeo.com.cn/?app=search&controller=index&action=searchtitle";
    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator = this.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String keyWord = entry.getKey().split(";")[0];

            HashMap<String,String> params = new HashMap<String, String>();
            params.put("t",keyWord);
            HashMap<String,String> headParams = new HashMap<String, String>();
            headParams.put("Referer","http://www.eeo.com.cn/new_dbdh.shtml");
            String html = GetHTML.postHtml("http://app.eeo.com.cn/?app=search&controller=index&action=searchtitle","utf-8",params,headParams);

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);

            Elements lis = document.select("ul.new_list").first().select("li");
            if(lis.isEmpty()){
                //Todo ??
                System.out.println("没找到");
            }
            else{
                ArrayList<Element> tableList = new ArrayList<Element>();
                for(Element ele:lis){
                    tableList.add(ele);
                }
                parsePages(tableList,entry);
            }
        }
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "经济观察网";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(int i=0;i<((ArrayList<Element>)tableList).size();i++){
            Element li1 = (Element) tableList.get(i);
            i++;
            Element li2 = (Element) tableList.get(i);
            String title = li1.select("a").text();
            String time = FormatTime.getTime(li1.select("span").text(),"(\\d+年\\d+月\\d+日)",1).replace("年","-").replace("月","-").replace("日","");
            String url = li1.select("a").attr("href");

            String summary = li2.text();
            String content = Page.getContent(url,"div#text_content","utf-8");
            System.out.println("TIME IS:"+time);
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words, content, key, FNum) && Transmition.timeFilter(time, Crawl.spyHistory24, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
