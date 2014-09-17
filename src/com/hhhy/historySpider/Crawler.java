package com.hhhy.historySpider;

import com.hhhy.crawler.*;
import com.hhhy.crawler.util.DateFormatUtils;
import com.hhhy.crawler.util.FormatTime;
import com.hhhy.crawler.util.GetHTML;
import com.hhhy.crawler.util.PropertiesUtil;
import com.hhhy.db.beans.Article;
import com.hhhy.web.client.thrift.ThriftClient;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {
    static {
        try {
            ThriftClient.init("10.1.1.31",12306);
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
    public Crawler(){
        /*ThriftClient client = ThriftClient.getInstance();

        String str = null;
        try {
            str = client.getKeywordHistory();
        } catch (TException e) {
            e.printStackTrace();
        }
        while(true){
            while(str==null || str.split(";").length==0 ||str.equals("")){
                try {
                    Thread.sleep(1000*5);
                    str = client.getKeywordHistory();
                    System.out.println("去一次");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("chulaila");
            String[] args = str.split(";");
            PropertiesUtil.loadFile("spiderConf.properties");
            String[] sites = PropertiesUtil.getPropertyValue("historyNames").split(";");
            crawl(args,sites);
            try {
                str = client.getKeywordHistory();
            } catch (TException e) {
                e.printStackTrace();
            }
        }*/
        String[] aaa = new String[1];
        aaa[0]="百度:1356969600000:1409587199999";
        PropertiesUtil.loadFile("spiderConf.properties");
        String[] sites = PropertiesUtil.getPropertyValue("historyNames").split(";");
        crawl(aaa, sites);
    }

    public void crawl(String[] args,String[] sites){
        for(String arg:args){
            System.out.println(arg);
            for(String site:sites){
                System.out.println(site);
                String keyWord = arg.split(":")[0];
                long beginTime = Long.parseLong(arg.split(":")[1]);
                long endTime = Long.parseLong(arg.split(":")[2]);

                parseBoardBaidu(keyWord,site, beginTime, endTime);
                parseBoardSougou(keyWord,site,beginTime,endTime);
                parseChinaso(keyWord,site,beginTime,endTime);
                parseBoard360(keyWord,site,beginTime,endTime);
            }

        }
    }

    public void parseBoardBaidu(String keyWord,String site,long beginTime,long endTime) {
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord+" site:"+site,"UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        int pn = -1;

        boolean ff = true;
        while(ff){
            pn+=50;
            String html = GetHTML.getHtml("http://news.baidu.com/ns?word="+transKey+"&bt=0&cl=2&ct=0&et=0&ie=utf-8&pn="+pn+"&rn=50&tn=news","utf-8");
            html = html.replaceAll("&nbsp;", "");
            Document document = Jsoup.parse(html);

            Elements flag = document.select("div#content_left").select("ul").select("li");
            if (flag.size()==0) {
                // Todo ??
                System.out.println("未搜到");
                ff=false;
            } else {
                for(Element ele:flag){
                    String url = ele.select("h3").select("a").attr("href");
                    String title = ele.select("h3").select(".c-title").text();
                    String src = ele.select(".c-author").text();
                    String summary =ele.select(".c-summary").text();
                    if(title.contains(keyWord) || summary.contains(keyWord)) {
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
                        Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,1);
                        if(ctime>=beginTime && ctime<=endTime){
                            Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, 1);
                            Transmition.transmit(article);
                        }
                        else{
                            ff = false;
                            break;
                        }
                    }
                }
            }
        }
    }
    public void parseBoardSougou(String keyWord,String site,long beginTime,long endTime) {
        String transKey = "";
        try {
            transKey = URLEncoder.encode(keyWord+" site:"+site,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int page = 0;

        boolean ff = true;
        while(ff){
            page++;
            if(page>100)
                break;
            String html = GetHTML.getHtml("http://news.sogou.com/news?dp=1&mode=1&page="+page+"&time=0&query="+transKey+"&sort=1","GBK");
            html = html.replaceAll("&nbsp;", "");
            Document document = Jsoup.parse(html);

            Elements flag = document.select(".results").select(".rb");
            if (flag.size()==0) {
                System.out.println("未搜到");
                ff=false;
            } else {
                for(Element ele:flag){
                    String url = ele.select("h3").select("a").attr("href");
                    String title = ele.select("h3").select("a").text();
                    String src = ele.select("cite").text();
                    String summary =ele.select(".thumb_news").text();
                    System.out.println(summary);
                    if(title.contains(keyWord) || summary.contains(keyWord)) {
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
                        Transmition.showDebug(type, title, summary, url, "" + ctime, summary, source, 1);
                        if(ctime>=beginTime && ctime<=endTime){
                            Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, 1);
                            Transmition.transmit(article);
                        }
                        else{
                            ff=false;
                            break;
                        }
                    }
                }
            }
        }

    }
    public void parseChinaso(String keyWord,String site,long beginTime,long endTime){
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord+" site:"+site, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        int page=0;
        boolean ff = true;
        while(ff){
            page++;
            String html = GetHTML.getHtml("http://news.chinaso.com/search?order=time&page="+page+"&wd="+transKey, "utf-8");

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);
    	        /*
    	        搜索关键词是否存在
    	         */
            Elements flag = document.select("ol.results").select("li");
            if(flag.size()==0){
                //TODO ??
                System.out.println("nothing to found.....");
                ff=false;
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
                        if(ctime>=beginTime && ctime<=endTime){
                            if(Transmition.contentFilter(null,summary,content,keyWord,FNum)){
                                Transmition.showDebug(type, title, content, url, time, summary, source, FNum.get(0));
                                //调接口~~~~~
                                Article article = Transmition.getArticle(type, title, content, url, ctime, summary, source,keyWord, FNum.get(0));
                                Transmition.transmit(article);
                            }
                        }else{
                            ff=false;
                            break;
                        }
                    }
                }
            }
        }
    }
    public void parseBoard360(String keyWord,String site,long beginTime,long endTime){
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord+" site:"+site, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        int pn = 0;
        boolean ff = true;
        while(ff){
            pn++;
            String html = GetHTML.getHtml("http://news.so.com/ns?j=0&rank=pdate&src=srp&q="+transKey+"&pn="+pn, "utf-8");

            html = html.replaceAll("&nbsp;","");
            Document document = Jsoup.parse(html);
    	        /*
    	        搜索关键词是否存在
    	         */
            Elements flag = document.select("ul#news").select("li");
            if(flag.size()==0){
                //TODO ??
                System.out.println("nothing to found.....");
                ff = false;
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
                    if(ctime>=beginTime && ctime<endTime){
                        if(Transmition.contentFilter(null,summary,content,keyWord,FNum)){
                            Transmition.showDebug(type, title, content, url, time, summary, source, FNum.get(0));
                            //调接口~~~~~
                            Article article = Transmition.getArticle(type, title, content, url, ctime, summary, source,keyWord, FNum.get(0));
                            Transmition.transmit(article);
                        }
                    }else{
                        ff=false;
                        break;
                    }
                }
            }
        }
    }
    public static void main(String[] args) throws ParseException {

        new Crawler();
    }
}
