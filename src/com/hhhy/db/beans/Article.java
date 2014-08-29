package com.hhhy.db.beans;

import java.io.Serializable;

import com.hhhy.db.beans.item.SrcType;

/**
 * 网页文章 对应a_webpage表
 *
 * @author chenlingpeng
 *
 */
public class Article implements Comparable<Article>, Serializable{
    private long id;
    private String title;//not null
    private String summary;//may be null
    private String content;//may be null,given url instead in some cases...
    private long time;//may be null
    private String url;//not null
    private String website;//name of the website
    //    private SrcType type;
    private int type;   //currently ,spider hasn't find a way to find type.
    private int emotion;


    // 因为哪个关键词得到这篇文章
    private String keyword;  //一个文章可能对于多个关键词，所以存在数据库中可能无效
    private int cnt;
//    private long ctime;// 获取网页时间

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public static void main(String[] args) {
        SrcType t = SrcType.elePaper;
        System.out.println(t.getName());
        System.out.println(t.getIndex());
        System.out.println(SrcType.getName(1));
    }

    public Article(String title, String content, String url, String website) {
        this.title = title;
        this.content = content;
        this.url = url;
        this.website = website;
    }

    public Article() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

//    public int getEmotion() {
//        return emotion;
//    }
//
//    public void setEmotion(int emotion) {
//        this.emotion = emotion;
//    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }



    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    @Override
    public String toString() {
        return "Article [url=" + url + "]";
    }

    @Override
    public int compareTo(Article that){
        return (int) (that.getTime() - this.getTime());
    }

}