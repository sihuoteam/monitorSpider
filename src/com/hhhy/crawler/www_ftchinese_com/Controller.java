package com.hhhy.crawler.www_ftchinese_com;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

import com.hhhy.crawler.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.db.beans.Article;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-6-4
 * Time: 下午6:38
 * To change this template use File | Settings | File Templates.
 */

class Tupple{
    public Element h3;
    public Element rl;
    public Element rb;
}
public class Controller extends CtrController {

    /**
     * Created with IntelliJ IDEA.
     * User: Ghost
     * Date: 14-6-3
     * Time: 下午5:12
     * To change this template use File | Settings | File Templates.
     */

    public final String BASE_URL = "http://www.ftchinese.com/search/?keys=";
    public Controller() {
    }
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator = Crawler.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String transKey = "";
            String keyWord = entry.getKey().split(";")[0];
			try {
				transKey = URLEncoder.encode(keyWord, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}

			String html = GetHTML.getHtml(
					"http://www.ftchinese.com/search/?keys=" + transKey,
					"UTF-8");

			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			String flag = document.select("div#bodywrapper")
					.select("div#body-content-col").select("div.columncontent")
					.text();
			if (flag.contains("对不起，我们的搜索引擎没有找到完全符合您的搜索条件的结果，您可以更换一下关键词")) {
				// Todo ??
				System.out.println("duibuqi ");
			} else {
				Element tableEles = document.select("div#bodywrapper")
						.select("div#body-content-col")
						.select("div.columncontent").first();
				ArrayList<Tupple> tableList = new ArrayList<Tupple>();
				Elements h3 = tableEles.select("h3.rh");
				Elements rl = tableEles.select("p.rl");
				Elements rb = tableEles.select("p.rb");
				for (int i = 0; i < h3.size(); i++) {
					Tupple tmpT = new Tupple();
					tmpT.h3 = h3.get(i);
					tmpT.rl = rl.get(i);
					tmpT.rb = rb.get(i);
					tableList.add(tmpT);
				}
				parsePages(tableList,entry); // 分别将三个ele组合成Temp。。。
			}	
    	}
        
    }

    @Override
    public void parsePages(ArrayList<?> tableList, Map.Entry<String, String> entry) {
        String website = "21世纪经济报道";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(Tupple tupple:(ArrayList<Tupple>)tableList){
            String title =tupple.h3.select("a").text();
            String time = FormatTime.getTime(tupple.rb.select("a").last().text(),"(\\d+-\\d+-\\d+)",1);
            String summary = tupple.rl.text();
            String url = "http://www.ftchinese.com"+tupple.h3.select("a").attr("href");

            String content = Page.getContent(url, "div#body-content-col", "utf-8");

            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,summary,content,key,FNum) && Transmition.timeFilter(time)){
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}

