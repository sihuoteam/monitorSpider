package com.hhhy.guba;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by Ghost on 2014/9/19 0019.
 */
public class JinrongjieGuba {
    String url;

    private static HttpPost setHttpPost(String url,Map<String,String> headParams){
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:29.0) Gecko/20100101 Firefox/29.0");
        if(headParams!=null){
            Iterator<Map.Entry<String,String>> iterator = headParams.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,String> entry = iterator.next();
                httpPost.setHeader(entry.getKey(),entry.getValue());
            }
        }
        return httpPost;
    }
    private static HttpPost postForm(String url,Map<String,String> param,Map<String,String> headParams){
        HttpPost httpPost = setHttpPost(url,headParams);
        Iterator<Map.Entry<String,String>> iterator = param.entrySet().iterator();
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        while (iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));

        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpPost;
    }
    private static HttpPost setBody(HttpPost bodyEmptyHttpPost, Map<String, String> params){
        Iterator<Map.Entry<String,String>> iterator = params.entrySet().iterator();
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();

        while (iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            nvps.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));

        }
        try {
            bodyEmptyHttpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return bodyEmptyHttpPost;
    }
    public CookieStore login() throws IOException {
        String dlm = "ghostinmatrix";
        String password = "password1";


        url = "http://sso.jrj.com.cn/sso/ssologin";


        String targetUrl = url;
        HashMap<String,String> params = new HashMap<String, String>();


        params.put("LoginID",dlm);
        params.put("Passwd",password);
        params.put("ReturnURL","http://www.jrj.com.cn");
        params.put("isVerifyCode","false");


        HashMap<String,String> headParams = new HashMap<String, String>();
        headParams.put("Host","sso.jrj.com.cn");
        headParams.put("Referer","http://sso.jrj.com.cn/sso/ssologin?ReturnURL=http://www.jrj.com.cn");


        HttpPost httpPost = postForm(targetUrl, params, headParams);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpResponse response = defaultHttpClient.execute(httpPost);
        int code = response.getStatusLine().getStatusCode();
        System.out.println("loginCode:"+code);

        CookieStore cookie = defaultHttpClient.getCookieStore();
        List<Cookie> cookieList = cookie.getCookies();
        for(Cookie cookie1:cookieList){
            System.out.println(cookie1.getName());
            System.out.println(cookie1.getPath());
            System.out.println(cookie1.getComment());
            System.out.println(cookie1.getCommentURL());
            System.out.println(cookie1.getDomain());
            System.out.println(cookie1.getValue());
            System.out.println("---------------");
        }

        return cookie;
    }

    public void dingTie(String Detail, CookieStore cookieStore) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCookieStore(cookieStore);

        String targetUrl = "http://istock.jrj.com.cn/postadd.jspa";

        HttpPost httpPost = new HttpPost(targetUrl);
        httpPost.setHeader("User-Agent","Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1");
        httpPost.setHeader("Host","istock.jrj.com.cn");
        httpPost.setHeader("Referer","http://istock.jrj.com.cn/article,002314,26592646.html");
        httpPost.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        httpPost.setHeader("Connection","keep-alive");

        HashMap<String,String> params = new HashMap<String, String>();
        params.put("Detail",Detail);
        try {
            params.put("Title", URLEncoder.encode("雅致股份","gb2312"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        params.put("TopicID","26592646");
        params.put("forumid","002314");
        params.put("anonym","0");
        params.put("hiddenYinYong","");
        params.put("upfilelink","");
        params.put("upfilename","");
        params.put("upfilepath","");

        HttpPost fullHttpPost = setBody(httpPost, params);


        try {
            HttpResponse httpResponse = httpClient.execute(fullHttpPost);
            int code = httpResponse.getStatusLine().getStatusCode();
            System.out.println(code);
            if(code==200){
                System.out.println("add id successful");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        JinrongjieGuba e = new JinrongjieGuba();
        CookieStore cookieStore = e.login();
        //e.dingTie("yes yes yes",cookieStore);
    }
}
