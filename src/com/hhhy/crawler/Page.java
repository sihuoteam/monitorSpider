package com.hhhy.crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.hhhy.crawler.util.GetHTML;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Page {
	public static String getContent(String url,String tag,String charset){
		url = url.trim();
		String html = GetHTML.getHtml(url, charset);
		Document doc = Jsoup.parse(html);
		String content = doc.select(tag).text();
		return content;
	}
    public static String getAllHtmlContent(String url){
        url = url.trim();
        String html = GetHTML.getHtml(url,"");
        Document doc = Jsoup.parse(html);
        String content = "";
        Elements elements = doc.getAllElements();
        for(Element ele:elements){
            content = ele.hasText()?(content+ele.text()+"\n"):content;
        }
        return content;
    }
}
