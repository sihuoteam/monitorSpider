package com.hhhy.crawler.guba_sina_com_cn;

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

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{
    private final String BASE_URL = "";
    public Controller(HashMap<String,String> kW,LinkedList<String> spyHistory) {
        super(kW,spyHistory);
    }
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator = this.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String transKey = "";
            String keyWord = entry.getKey().split(";")[0];
			try {
				transKey = URLEncoder.encode(keyWord, "gb2312");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			String html = GetHTML
					.getHtml(
							"http://guba.sina.com.cn/?type=1&s=search&key2=%C7%EB%CA%E4%C8%EB%C6%B4%D2%F4%2F%B4%FA%C2%EB%2F%C3%FB%B3%C6&key1="
									+ transKey
									+ "&key3=%C7%EB%CA%E4%C8%EB%D7%F7%D5%DF%B9%D8%BC%FC%D7%D6",
							"gb2312");

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);
			/*
			 * 搜索关键词是否存在
			 */
			Elements flag = document.select("div.blk_list").select("div");
			if (flag.size() == 0) {
				// Todo ??
				System.out.println("nothing to found.....");
			} else {
				Elements tableEles = document.select("div.blk_list").select(
						"div");
				ArrayList<Element> tableList = new ArrayList<Element>();
				for (Element ele : tableEles) {
					tableList.add(ele);
				}
				parsePages(tableList,entry);
			}
    	}
        
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "新浪股吧";
        int type = 2;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Element ele:(ArrayList<Element>)tableList){
            String title = ele.select("div.il_txt").select("h4.ilt_tit").select("a").text();
            String time = Subutils.getTime(ele.select("div.ilt_panel").select("div.fl_left").select("a").text());
            String summary = ele.select("div.il_txt").select("p.ilt_p").text();
            String url = "http://guba.sina.com.cn"+ele.select("div.il_txt").select("h4.ilt_tit").select("a").attr("href");
            String content = Page.getContent(url, "div.ilt_p", "gb2312");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory7, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
