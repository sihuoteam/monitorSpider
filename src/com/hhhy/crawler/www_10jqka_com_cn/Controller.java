package com.hhhy.crawler.www_10jqka_com_cn;

import java.io.IOException;
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
 * Date: 14-7-6
 * Time: 下午2:55
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController{
    public final String BASE_URL = "http://search1.hebei.com.cn/m_fullsearch/utf8/full_search.jsp";
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
				transKey = URLEncoder.encode(keyWord, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("keywords", keyWord);
			params.put("news_type_id", "" + 1);
			params.put("x", "" + 37);
			params.put("y", "" + 14);

			String html = GetHTML
					.postHtml(
							"http://search1.hebei.com.cn/m_fullsearch/utf8/full_search.jsp",
							"UTF-8", params, null);
			html = html.replaceAll("&nbsp;", "");
			Document document = Jsoup.parse(html);

			/*
			 * 搜索关键词是否存在
			 */
			Elements flag = document.select("table").first().select("tbody")
					.select("tr");
			if (flag.size() == 0) {
				// Todo ??
				System.out.println("nothing to show....");
			} else {
				Elements tableEles = document.select("table").first()
						.select("tbody").select("tr");
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
        String website = "长城网";
        int type = 1;
        String[] words = entry.getValue().split(";");
        String key = entry.getKey().split(";")[0];
        for(int i=0;i<((ArrayList<Element>)tableList).size();i++){
            Element ele1 = (Element) tableList.get(i);
            String title = ele1.select("td.searchTitle").text();
            i++;
            Element ele2 =(Element) tableList.get(i);
            i++;
            Element ele3 = (Element) tableList.get(i);

            String time = FormatTime.getTime(ele3.select("td.searchBotton").text(), "\\d{4}-\\d{2}-\\d{2}");
            String summary = ele2.select("td.searchMain").text();
            String url = ele1.select("td.searchTitle").select("a").first().attr("href");

            String content = Page.getContent(url, "div#doc", "utf-8");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words,content,key,FNum) && Transmition.timeFilter(time, Crawl.spyHistory8, title)){
                spyHistory.add(title);
                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website,key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }

}
