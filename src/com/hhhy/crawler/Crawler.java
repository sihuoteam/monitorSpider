package com.hhhy.crawler;

import com.hhhy.crawler.commenSpider.CommenSpider;
import com.hhhy.crawler.util.JsonUtils;
import com.hhhy.crawler.util.PropertiesUtil;
import com.hhhy.web.client.thrift.ThriftClient;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import java.util.*;

public class Crawler extends TimerTask {
	static {
		try {
			ThriftClient.init("localhost", 12306);
		} catch (TTransportException e) {
			e.printStackTrace();
		}
	}
    public static HashMap<String,String> keyWords = null;
    String[] webs;
    public static String newSites = null;
    class KWChange extends TimerTask{
        @Override
        public void run() {
            ThriftClient client = ThriftClient.getInstance();
            try {
                keyWords =  (HashMap<String,String>) JsonUtils.fromJson(client.getKeywords(), HashMap.class);
                newSites = client.getUrls();
            } catch (TException e) {
                e.printStackTrace();
            }
        }
    }

    public Crawler() {
        Timer timer = new Timer();
        timer.schedule(new KWChange(),0,30*60*1000);
    }

	LinkedList<Timer> crawlList= null;
	public static Set<String> spyHistory = new HashSet<String>();
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		crawlList = new LinkedList<Timer>();
		spyHistory.clear();
		PropertiesUtil.loadFile("spiderConf.properties");
		String webNames = PropertiesUtil.getPropertyValue("webNames");
		webs = webNames.split(";");
        while(keyWords==null || keyWords.isEmpty()){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int blankTime = keyWords.size()*30*3000;
		if(crawlList.size()==0){
			for(String webName:webs){
				Timer timer = new Timer();
				timer.schedule(new Crawl(webName),0,blankTime);
				crawlList.add(timer);
			}
            Timer timer = new Timer();
            timer.schedule(new CommenSpider(),0,blankTime);
            crawlList.add(timer);
		}
		else{
			for(Timer timer:crawlList){
				timer.cancel();
			}
			crawlList.clear();
			for(String webName:webs){
				Timer timer = new Timer();
				timer.schedule(new Crawl(webName),0,blankTime);
				crawlList.add(timer);
			}
            Timer timer = new Timer();
            timer.schedule(new CommenSpider(),0,blankTime);
            crawlList.add(timer);
		}
	}
	public static void main(String[] args) throws TException {
		Crawler crawler = new Crawler();
		Timer timer = new Timer();
		timer.schedule(crawler, 0, 24 * 60 * 60 * 1000);
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new com.hhhy.historySpider.Crawler();
	}
}
