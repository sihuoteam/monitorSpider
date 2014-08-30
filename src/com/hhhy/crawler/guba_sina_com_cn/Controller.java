package com.hhhy.crawler.guba_sina_com_cn;

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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-7-8
 * Time: 下午3:36
 * To change this template use File | Settings | File Templates.
 */
public class Controller extends CtrController {
    private final String BASE_URL = "";
    public Controller() {
    }
    @Override
    public void parseBoard(){
        Iterator<Map.Entry<String,String>> iterator = Crawler.keyWords.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            String transKey = "";
            String keyWord = entry.getKey().split(";")[0];
            System.out.println("keyword:" + keyWord);
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

			Elements flag = document.select("div.blk_list").select("div[tid]");
            if(keyWord.equals("百度")) System.out.println(document.select("div.blk_list").html());
			if (flag.size() == 0) {
				// Todo ??
				System.out.println("nothing to found.....");
			} else {
//                System.out.println(document.select("div.blk_list"));
//				Elements tableEles = document.select("div.blk_list").select(
//						"div");
				ArrayList<Element> tableList = new ArrayList<Element>();
				for (Element ele : flag) {
					tableList.add(ele);
				}
                System.out.println("找到页面上的size：" + tableList.size());
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

            String timeContent = ele.select(".fl_left").select("a").text();
            System.out.println("页面上找到的时间："+timeContent);
            String timeS = FormatTime.getTime(timeContent, "\\d+分钟前");
            if(timeS == null || timeS.equals("")){
                timeS = FormatTime.getTime(timeContent, "今天\\d{2}:\\d{2}");
                if(timeS == null || timeS.equals("")){
                    timeS = null;
                }
            }
            long time = 0;
            if(timeS != null) {

                if (timeS.contains("分钟前")) {
                    timeS = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm");
                } else if(timeS.contains("今天")){
                    String hh_ff = FormatTime.getTime(timeS, "(?<=今天)\\d{2}:\\d{2}");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
                    timeS = sdf2.format(new Date());
                    timeS += " " + hh_ff;
                }
                String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
                if(!timeS.startsWith(time2)) continue;
                System.out.println("今天的时间："+ time2);
                System.out.println("确认是今天的timeS: " + timeS);

                try {
                    time = DateFormatUtils.getTime(timeS, "yyyy-MM-dd HH:mm");
                    System.out.println("格式转化time："+time);
                    System.out.println("现在时间：" + System.currentTimeMillis());
                } catch (ParseException e) {
                    System.out.println(timeS);
                }
            }else continue;



            String summary = ele.select("div.il_txt").select("p.ilt_p").text();
            String url = "http://guba.sina.com.cn"+ele.select("div.il_txt").select("h4.ilt_tit").select("a").attr("href");
            String content = Page.getContent(url, "div.ilt_p", "gb2312");
            ArrayList<Integer> FNum = new ArrayList<Integer>();
            if(Transmition.contentFilter(words, summary, content, key, FNum)){
//                Transmition.showDebug(type, title, content, url, time, summary, website, FNum.get(0));
                //调接口~~~~~
                System.out.println("存储的时间："+ time);
                System.out.println("内容："+ content);
                System.out.println("存储的时间："+ summary);
                System.out.println("存储的时间："+ url);
                System.out.println("存储的时间："+ title);
                Article article = Transmition.getArticle(type, title, content, url, time, summary, website, key, FNum.get(0));
                Transmition.transmit(article);
            }
        }
    }
}
