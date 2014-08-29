package com.hhhy.crawler;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.JsonUtils;
import com.hhhy.crawler.util.MyLog;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import com.hhhy.crawler.util.DateFormatUtils;
import com.hhhy.db.beans.Article;
import com.hhhy.web.client.thrift.ThriftClient;

public class Transmition {

    public static boolean contentFilter(String[] words,String content,String key,ArrayList<Integer> FNum){

        FNum.add(0);
        if(words!=null && words[0].length()>0){
            int findNum = 0;
            for(String word:words){
                if(content.contains(word))
                    findNum++;
            }
            if(content!=null && content.contains(key)){
                FNum.set(0,findNum);
                return true;

            }
            else
                return false;
        }
        else{
            if(content!=null && content.contains(key)){
                FNum.set(0,0);
                return true;
            }
            else
                return false;
        }

    }
    public static boolean timeFilter(String time, List<String> spyHistory, String title){
        if(time==null)
            return false;
        if(FormatTime.isAfterToday(time) && !spyHistory.contains(title))
            return true;
        else return false;
    }
	public static void showDebug(int type, String title, String content,
			String url, String time, String summary, String website, int findNum) {
		boolean debug = false;
		if (debug) {
			System.out.println("type:" + type);
			System.out.println("title:" + title);
			System.out.println("content:" + content);
			System.out.println("url:" + url);
			System.out.println("time:" + time);
			System.out.println("summary:" + summary);
			System.out.println("website:" + website);
            System.out.println("findNum:"+ findNum);
			System.out.println("----------------");
		}
	}
    public static Article getArticle(int type, String title, String content,
                                     String url, String time, String summary, String website, String keyword, int findNum) {
        try {
            return getArticle(type, title, content,
                    url, DateFormatUtils.getTime(time, DateFormatUtils.yyyyMMdd), summary, website, keyword, findNum);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
	public static Article getArticle(int type, String title, String content,
			String url, long time, String summary, String website,String keyword, int findNum) {
		Article article = new Article();
        article.setTitle(title);
        article.setType(type);
        article.setSummary(summary);
        article.setTime(time);
        article.setContent(content);
        article.setUrl(url);
        article.setWebsite(website);
        article.setKeyword(keyword);
        article.setCnt(findNum);
        return article;
	}

	public static void transmit(Article article) {
		if (article!=null
				&& article.getContent().length() > 0
				&& article.getSummary().length() > 0
				&& article.getTitle().length() > 0
				&& article.getUrl().length() > 0) {
			String jsonArticleStr = JsonUtils.toJson(article);
            MyLog.logINFO(jsonArticleStr);
			try {
				ThriftClient client = ThriftClient.getInstance();
				client.addArticle(jsonArticleStr);

			} catch (TTransportException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
