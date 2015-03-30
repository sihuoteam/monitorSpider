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

import javax.security.auth.login.Configuration;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Crawler {

    public Crawler(){
        ThriftClient client = ThriftClient.getInstance();

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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TException e) {
                    e.printStackTrace();
                }
            }
            String[] args = str.split(";");
            PropertiesUtil.loadFile("spiderConf.properties");
            String[] sites = PropertiesUtil.getPropertyValue("historyNames").split(";");
            crawl(args,sites);
            System.out.println("this role is over");
            try {
                str = client.getKeywordHistory();
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    public void crawl(String[] args,String[] sites){
        System.out.println("jinrucrawlcishu");
        for(String arg:args){
            for(String site:sites){
                String keyWord = arg.split(":")[0];
                String additionWord = arg.split(":")[1];
                long beginTime = Long.parseLong(arg.split(":")[2]);
                long endTime = Long.parseLong(arg.split(":")[3]);

                parseBoardBaidu(keyWord,additionWord,site, beginTime, endTime);
                parseBoardSougou(keyWord,additionWord,site,beginTime,endTime);
                parseChinaso(keyWord,additionWord,site,beginTime,endTime);
                parseBoard360(keyWord,additionWord,site,beginTime,endTime);
            }
            //百度的全量爬取
            String keyWord = arg.split(":")[0];
            String additionWord = arg.split(":")[1];
            long beginTime = Long.parseLong(arg.split(":")[2]);
            long endTime = Long.parseLong(arg.split(":")[3]);
            parseBoardBaidu(keyWord,additionWord,"", beginTime, endTime);
        }
    }

    public void parseBoardBaidu(String keyWord, String additionWord,String site,long beginTime,long endTime) {
        String transKey = "";
        try {
            if(site.length()!=0)
                transKey = URLEncoder.encode(keyWord+" "+additionWord+" site:"+site,"UTF-8");
            else
                transKey = URLEncoder.encode(keyWord+" "+additionWord,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int pn = 0;

        boolean ff = true;
        while(ff){

            String html = GetHTML.getHtml("http://news.baidu.com/ns?ct=0&pn="+pn+"&rn=50&ie=utf-8&bs="+transKey+"&rsv_bp=1&sr=0&cl=2&f=8&prevct=no&tn=news&word="+transKey+"&rsv_sug3=1&rsv_sug4=104&rsv_sug1=1&rsv_sug=1","utf-8");
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
                        String source = src;

                        int type = 1;
                        long ctime = 0;
                        try {
                            Pattern p = Pattern.compile("((\\d{4}).*(\\d{2}).*(\\d{2}).*(\\d{2}):(\\d{2}).*)");

                            Matcher matcher = p.matcher(src);
                            if(matcher.find()){
                                src = matcher.group(2)+"-"+matcher.group(3)+"-"+matcher.group(4)+" "+matcher.group(5)+":"+matcher.group(6)+":00";
                                source = source.replace(matcher.group(1),"");
                            }
                            ctime = DateFormatUtils.getTime(src, "yyyy-MM-dd HH:mm:ss");
                        } catch (ParseException e) {
                            e.printStackTrace();
                            continue;
                        }

                        if(ctime>=beginTime && ctime<=endTime){
                            if(!additionWord.equals("")){
                                String[] adds = new String[1];
                                adds[0] = additionWord;
                                ArrayList<Integer> num = new ArrayList<Integer>();
                                if(Transmition.contentFilter(adds, summary, summary, keyWord, num)){
                                    Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,num.get(0));
                                    Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, num.get(0));
                                    Transmition.transmit(article);
                                }
                            }
                            else{
                                Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,0);
                                Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, 0);
                                Transmition.transmit(article);
                            }

                        }
                        if(ctime<beginTime){
                            ff=false;
                            break;
                        }
                    }
                }
            }
            if(document.select("p#page").size()!=0 && document.select("p#page").select("a").size()!=0){
                String next = document.select("p#page").select("a").last().text();
                if(!next.contains("下一页")){
                    break;
                }
            }
            else
                break;
            pn+=20;
        }
    }
    public void parseBoardSougou(String keyWord,String additionWord,String site,long beginTime,long endTime) {
        String transKey = "";
        try {
            transKey = URLEncoder.encode(keyWord+" "+additionWord+" site:"+site,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        int page = 0;

        boolean ff = true;
        while(ff){
            page++;

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

                    if(title.contains(keyWord) || summary.contains(keyWord)) {
                        if (src.length() < 16) continue;
                        String source = ele.select("cite").attr("title");
                        String time = src.substring(src.length() - 16);
                        long ctime = 0;
                        try {
                            ctime = DateFormatUtils.getTime(time, "yyyy-MM-dd hh:mm");
                        } catch (ParseException e) {
                            e.printStackTrace();
                            continue;
                        }
                        int type = 1;

                        if(ctime>=beginTime && ctime<=endTime){
                            if(!additionWord.equals("")){
                                String[] adds = new String[1];
                                adds[0] = additionWord;
                                ArrayList<Integer> num = new ArrayList<Integer>();
                                if(Transmition.contentFilter(adds, summary, summary, keyWord, num)){
                                    Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,num.get(0));
                                    Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, num.get(0));
                                    Transmition.transmit(article);
                                }
                            }
                            else{
                                Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,0);
                                Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, 0);
                                Transmition.transmit(article);
                            }

                        }
                        if(ctime<beginTime){
                            ff=false;
                            break;
                        }
                    }
                }
            }
            if(document.select("div#pagebar_container").size()!=0 && document.select("div#pagebar_container").select("a").size()!=0){
                String next = document.select("div#pagebar_container").select("a").last().text();
                if(!next.contains("下一页")){
                    break;
                }
            }
            else
                break;

        }

    }
    public void parseChinaso(String keyWord,String additionWord, String site,long beginTime,long endTime){
        String transKey = "";
        try {
            transKey = URLEncoder.encode(keyWord+" "+additionWord+" site:"+site, "gb2312");
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

                        if(ctime>=beginTime && ctime<=endTime){
                            if(!additionWord.equals("")){
                                String[] adds = new String[1];
                                adds[0] = additionWord;
                                ArrayList<Integer> num = new ArrayList<Integer>();
                                if(Transmition.contentFilter(adds, summary, summary, keyWord, num)){
                                    Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,num.get(0));
                                    Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, num.get(0));
                                    Transmition.transmit(article);
                                }
                            }
                            else{
                                Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,0);
                                Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, 0);
                                Transmition.transmit(article);
                            }

                        }
                        if(ctime<beginTime){
                            ff=false;
                            break;
                        }
                    }
                }
            }
            if(document.select("div.pg").size()!=0 && document.select("div.pg").select("a").size()!=0){
                String next = document.select("div.pg").select("a").last().text();
                if(!next.contains("下一页")){
                    break;
                }
            }
            else
                break;
        }
    }
    public void parseBoard360(String keyWord,String additionWord,String site,long beginTime,long endTime){
            String transKey = "";
            try {
                transKey = URLEncoder.encode(keyWord+" "+additionWord+" site:"+site, "gb2312");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        int pn = 0;
        boolean ff = true;
        while(ff){
            pn++;
            String html = GetHTML.getHtml("http://news.haosou.com/ns?j=0&rank=pdate&src=srp&q="+transKey+"&pn="+pn, "utf-8");
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
                    String time = FormatTime.getTime(ele.select("p.newsinfo").select("span.posttime").attr("title"), "(\\d+-\\d+-\\d+)", 1);
                    String summary = ele.select("p").text();
                    String url = ele.select("h3").select("a").attr("href");
                    String source = ele.select("h3").select("span").select("em").text();
                    if(source==null)
                        source=website;
                    if(time==null){
                        continue;
                    }
                    long ctime = 0;
                    try {
                        ctime = DateFormatUtils.getTime(time,DateFormatUtils.yyyyMMdd);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(ctime>=beginTime && ctime<=endTime){
                        if(!additionWord.equals("")){
                            String[] adds = new String[1];
                            adds[0] = additionWord;
                            ArrayList<Integer> num = new ArrayList<Integer>();
                            if(Transmition.contentFilter(adds, summary, summary, keyWord, num)){
                                Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,num.get(0));
                                Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, num.get(0));
                                Transmition.transmit(article);
                            }
                        }
                        else{
                            Transmition.showDebug(type,title,summary,url,""+ctime,summary,source,0);
                            Article article = Transmition.getArticle(type, title, summary, url, ctime, summary, source, keyWord, 0);
                            Transmition.transmit(article);
                        }

                    }
                    if(ctime<beginTime){
                        ff=false;
                        break;
                    }
                }
            }
            if(document.select("div#page").size()!=0 && document.select("div#page").select("a").size()!=0){
                String next = document.select("div#page").select("a").last().text();
                if(!next.contains("下一页")){
                    break;
                }
            }
            else
                break;
        }
    }

    /*public static void main(String[] args){
        Crawler crawler = new Crawler();
        PropertiesUtil.loadFile("spiderConf.properties");
        String[] sites = PropertiesUtil.getPropertyValue("historyNames").split(";");
            crawler.parseBoardBaidu("先锋电子","","",1425139200000L,1427731199999L);
    }*/
}
