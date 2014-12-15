package com.hhhy.crawler.commenSpider;

import com.hhhy.crawler.*;
import com.hhhy.crawler.util.DateFormatUtils;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.crawler.util.MyLog;
import com.hhhy.db.beans.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Set;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommenSpider extends TimerTask {
    @Override
    public void run() {
        Set<String> keywords = Crawler.keyWords.keySet();
        crawl(keywords, Crawler.newSites.split(";"));
    }

    public void crawl(Set<String> args,String[] sites){
        for(String arg:args){
            for(String site:sites){

                String keyWord = arg.split(";")[0];
                String[] additionWords = Crawler.keyWords.get(arg).split(";");
                parseBoardBaidu(keyWord,additionWords,site);
                parseBoardSougou(keyWord,additionWords,site);
                parseChinaso(keyWord,additionWords,site);
                parseBoard360(keyWord,additionWords,site);
            }
        }
    }

    public void parseBoardBaidu(String keyWord,String[] additionWords,String site) {
        String transKey = "";
        try {
            transKey = URLEncoder.encode(keyWord+" site:"+site,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String html = GetHTML.getHtml("http://news.baidu.com/ns?word="+transKey+"&bt=0&cl=2&ct=0&et=0&ie=utf-8&pn=0&rn=50&tn=news","utf-8");
        html = html.replaceAll("&nbsp;", "");
        Document document = Jsoup.parse(html);

        Elements flag = document.select("div#content_left").select("ul").select("li");
        if (flag.size()==0) {
            // Todo ??
            System.out.println("未搜到");
        } else {
            for(Element ele:flag){
                String url = ele.select("h3").select("a").attr("href");
                String title = ele.select("h3").select(".c-title").text();
                String src = ele.select(".c-author").text();
                String summary =ele.select(".c-summary").text();
                ArrayList<Integer> fnum = new ArrayList<Integer>();
                if(Transmition.contentFilter(additionWords,summary,summary,keyWord,fnum)) {
                    if (src.length() < 19) continue;
                    String time = src.substring(src.length() - 19);
                    String source = src.substring(0,src.length() - 19).trim();
                    String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
                    if(!time.startsWith(time2))continue;
                    int type = 1;
                    long ctime = 0;
                    try {
                        ctime = DateFormatUtils.getTime(time, "yyyy-MM-dd HH:mm:ss");
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                    Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,fnum.get(0));
                    Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, fnum.get(0));
                    Transmition.transmit(article);
                }
            }
        }
    }
    public void parseBoardSougou(String keyWord,String[] additionWords,String site) {
        String transKey = "";
        try {
            transKey = URLEncoder.encode(keyWord+" site:"+site,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String html = GetHTML.getHtml("http://news.sogou.com/news?dp=1&mode=1&page=1&time=0&query="+transKey+"&sort=1","GBK");
        html = html.replaceAll("&nbsp;", "");
        Document document = Jsoup.parse(html);

        Elements flag = document.select(".results").select(".rb");
        if (flag.size()==0) {
            System.out.println("未搜到");
        } else {
            for(Element ele:flag){
                String url = ele.select("h3").select("a").attr("href");
                String title = ele.select("h3").select("a").text();
                String src = ele.select("cite").text();
                String summary =ele.select(".thumb_news").text();
                ArrayList<Integer> fnum = new ArrayList<Integer>();
                if(Transmition.contentFilter(additionWords,summary,summary,keyWord,fnum)) {
                    if (src.length() < 16) continue;
                    String source = ele.select("cite").attr("title");
                    String time = src.substring(src.length() - 16);
                    String time2 = DateFormatUtils.formatTime(System.currentTimeMillis(), "yyyy-MM-dd");
                    if (!time.startsWith(time2)) continue;
                    long ctime = 0;
                    try {
                        ctime = DateFormatUtils.getTime(time, "yyyy-MM-dd hh:mm");
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue;
                    }
                    int type = 1;
                    Transmition.showDebug(type, title, summary, url, "" + ctime, summary, source, fnum.get(0));
                    Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, fnum.get(0));
                    Transmition.transmit(article);
                }
            }
        }
    }
    public void parseChinaso(String keyWord,String[] additionWords,String site){
        String transKey = "";
        try {
            transKey = URLEncoder.encode(keyWord+" site:"+site, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String html = GetHTML.getHtml("http://news.chinaso.com/search?order=time&page=1&wd="+transKey, "utf-8");

        html = html.replaceAll("&nbsp;","");
        Document document = Jsoup.parse(html);
    	    /*
    	    搜索关键词是否存在
    	     */
        Elements flag = document.select("ol.results").select("li");
        if(flag.size()==0){
            //TODO ??
            System.out.println("nothing to found.....");
        }
        else{
            int type = 1;
            String website = "中国搜索";
            for(Element ele:flag){
                String title = ele.select("div.newsList").select("td").select("div.T1").select("a").text();
                String time = FormatTime.getTime(ele.select("div.newsList").select("td>div").text(), "(\\d{4}-\\d{2}-\\d{2})", 1);
                if(time == null && (ele.select("div.newsList").select("td>div").last().text().contains("小时前")|| ele.select("div.newslist").select("td>div").last().text().contains("分钟前")))
                    time = FormatTime.getTime(FormatTime.getCurrentFormatTime(),"(\\d+-\\d+-\\d+)",1);
                if(time==null)
                    continue;
                long ctime = 0;
                try {
                    ctime = DateFormatUtils.getTime(time, DateFormatUtils.yyyyMMdd);
                    System.out.println("time is "+time+"   "+ctime+"   "+ele.select("div.newsList").select("td>div").text());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Pattern pattern = Pattern.compile("(.*网)");
                Matcher matcher = pattern.matcher(ele.select("div.newsList").select("td>div").last().text());
                String source = website;
                if(matcher.find()){
                    source = matcher.group(1);
                }
                String summary = ele.select("p").text();
                String url = ele.select("div.newsList").select("td").select("div.T1").select("a").attr("href");
                if(url.contains("http")){
                    String content = summary;
                    ArrayList<Integer> FNum = new ArrayList<Integer>();
                    if(Transmition.contentFilter(additionWords,summary,content,keyWord,FNum)){
                        Transmition.showDebug(type, title, content, url, time, summary, source, FNum.get(0));
                        //调接口~~~~~
                        Article article = Transmition.getArticle(type, title, content, url, ctime, summary, source,keyWord, FNum.get(0));
                        Transmition.transmit(article);
                    }
                }
            }
        }
    }
    public void parseBoard360(String keyWord,String[] additionWords,String site){
        String transKey = "";
        try {
            transKey = URLEncoder.encode(keyWord+" site:"+site, "gb2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
            String html = GetHTML.getHtml("http://news.so.com/ns?j=0&rank=pdate&src=srp&q="+transKey+"&pn=1", "utf-8");

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);
    	        /*
    	        搜索关键词是否存在
    	         */
            Elements flag = document.select("ul#news").select("li");
            if(flag.size()==0){
                //TODO ??
                System.out.println("nothing to found.....");
            }
            else{
                int type = 2;
                String website = "360搜索";
                for(Element ele:flag){
                    String title = ele.select("h3").select("a").text();
                    String time = FormatTime.getTime(ele.select("h3").select("span").attr("title"), "(\\d+-\\d+-\\d+)", 1);
                    String summary = ele.select("p").text();
                    String url = ele.select("h3").select("a").attr("href");
                    String content = summary;
                    String source = ele.select("h3").select("span").select("em").text();
                    if(source==null)
                        source=website;
                    ArrayList<Integer> FNum = new ArrayList<Integer>();
                    if(time==null)
                        continue;
                    long ctime = 0;
                    try {
                        ctime = DateFormatUtils.getTime(time,DateFormatUtils.yyyyMMdd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(Transmition.contentFilter(additionWords,summary,content,keyWord,FNum) && Transmition.timeFilter(time)){
                        Transmition.showDebug(type, title, content, url, time, summary, source, FNum.get(0));
                        Article article = Transmition.getArticle(type, title, content, url, ctime, summary, source,keyWord, FNum.get(0));
                        Transmition.transmit(article);
                    }
                }
            }
    }
}
