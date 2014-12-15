package com.hhhy.crawler.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-4-17
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */

public class GetHTML {
    private static ArrayList<String> proxy = new ArrayList<String>();
    static{
        try{
            if (proxy.size() == 0) {
                BufferedReader br = new BufferedReader(new FileReader("./proxy.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    proxy.add(line);
                }
                br.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static String getHeaderValue(String key,String url){
    	DefaultHttpClient httpclient = new DefaultHttpClient();  
    	HttpParams params = httpclient.getParams();  
    	params.setParameter(ClientPNames.HANDLE_REDIRECTS, false); 
    	HttpGet httpGet = setHttpGet(url);
        String back = "";
        try {
           // httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,);
            HttpResponse response = httpclient.execute(httpGet);

            if (response != null) {
                Header header = response.getFirstHeader(key);
                if(header.getName().equals(key))
                	return header.getValue();
                else back = "error";
            }
            else
                back = "error";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return back;
    }
    private static DefaultHttpClient getHttpClient(){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        String p = proxy.get(((int) (10000 * Math.random())) % proxy.size());
        String proxy = p.split(":")[0];
        Integer port = Integer.parseInt(p.split(":")[1]);
        HttpHost Proxy = new HttpHost(proxy, port);
        httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY, Proxy);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
        httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
        return httpClient;
    }
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

    private static HttpGet setHttpGet(String url){
        HttpGet httpGet = null;
        httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1");
        return httpGet;
    }
    private static HttpGet setHttpGet(String url,Map<String,String> headParams){
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1");
        Iterator<Map.Entry<String,String>> iterator = headParams.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String,String> entry = iterator.next();
            httpGet.setHeader(entry.getKey(),entry.getValue());
        }
        return httpGet;
    }

    public static String postHtml(String url,String charSet,Map<String,String> param,Map<String,String> headParams){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = postForm(url,param,headParams);
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity rspEntity = null;
        if (httpResponse != null) {
            rspEntity = httpResponse.getEntity();
            String html = null;
            try {
                html = InputStreamUtils.inputStream2String(rspEntity.getContent(),charSet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return html;
        }
        else {
            return "";
        }

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

    private static String getCharset(String html){
        String charset = "gbk";
        Document document = Jsoup.parse(html);
        String charsetStr = document.select("head>meta[http-equiv=Content-Type]").toString();
        if(!charsetStr.contains("charset"))
            charsetStr = document.select("head>meta").toString();
        System.out.println("the charsetStr is :"+charsetStr);
        String regex = "charset=\"?([\\w-\\d]*)\"";
        Pattern pattern = Pattern.compile(regex,Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(charsetStr);
        if(matcher.find()){
            charset = matcher.group(1);
            System.out.println("charset got in getCharset Method:" + charset);
        }
        return charset;
    }

    public static String getHtmlWithCookie(String url,org.apache.http.client.CookieStore cookieStore,String charSet, HashMap<String, String> headParams){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCookieStore(cookieStore);
        HttpGet httpGet = setHttpGet(url,headParams);
        String back = "";
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) { // 2XX状态码
                    HttpEntity entity = response.getEntity();
                    String html ="";
                    if(charSet.length()==0){
                        InputStream eC = entity.getContent();
                        String isStr = InputStreamUtils.inputStream2String(eC);
                        charSet = getCharset(isStr);
                        InputStream iss = new ByteArrayInputStream(isStr.getBytes());
                        html = InputStreamUtils.inputStream2String(iss,charSet);
                    }
                    else{
                        InputStream eC = entity.getContent();
                        html = InputStreamUtils.inputStream2String(eC,charSet);
                    }
                    EntityUtils.consume(entity);
                    if (html.contains("</html>")||html.contains("</HTML>")){
                        System.out.println(url + " has got totally");
                        back = html;
                    } else
                        back = "error";
                }
            }
            else
                back = "error";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return back;
    }

    public static String getHtml(String url,String charSet){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = setHttpGet(url);
        String back = "";
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) { // 2XX状态码
                    HttpEntity entity = response.getEntity();
                    String html ="";
                    if(charSet.length()==0){
                        InputStream eC = entity.getContent();
                        String isStr = InputStreamUtils.inputStream2String(eC);
                        charSet = getCharset(isStr);
                        InputStream iss = new ByteArrayInputStream(isStr.getBytes());
                        html = InputStreamUtils.inputStream2String(iss,charSet);
                    }
                    else{
                        InputStream eC = entity.getContent();
                        html = InputStreamUtils.inputStream2String(eC,charSet);
                    }
                    EntityUtils.consume(entity);
                    if (html.contains("</html>")||html.contains("</HTML>")){
                        System.out.println(url + " has got totally");
                        back = html;
                    } else
                        back = "error";
                }
            }
            else
                back = "error";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return back;
    }
    public static String getHtml(String url,String charSet,Map<String,String> headParams){
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = setHttpGet(url,headParams);
        String back = "";
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) { // 2XX状态码
                    HttpEntity entity = response.getEntity();
                    String html ="";
                    if(charSet.length()==0){
                        InputStream eC = entity.getContent();
                        String isStr = InputStreamUtils.inputStream2String(eC);
                        charSet = getCharset(isStr);
                        InputStream iss = new ByteArrayInputStream(isStr.getBytes());
                        html = InputStreamUtils.inputStream2String(iss,charSet);
                    }
                    else{
                        InputStream eC = entity.getContent();
                        html = InputStreamUtils.inputStream2String(eC,charSet);
                    }
                    EntityUtils.consume(entity);
                    if (html.contains("</html>")||html.contains("</HTML>")){
                        System.out.println(url + " has got totally");
                        back = html;
                    } else
                        back = "error";
                }
            }
            else
                back = "error";
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return back;
    }
    public static String getHtmlGzip(String url,String charSet){
        HttpClient httpClient = new HttpClient();
        GetMethod getMethod = new GetMethod(url);
        getMethod.setRequestHeader("Connection", "Keep-Alive");
        getMethod.setRequestHeader("Accept", "*/*");
       // getMethod.setRequestHeader("From", "goolebot@googlebot.com");
        getMethod.setRequestHeader("User-Agent",
                "Mozilla/5.0 (compatible; Baiduspider/2.0; +http://www.baidu.com/search/spider.html)");
        getMethod.setRequestHeader("Accept-Encoding", "gzip, deflate");
        String acceptEncoding = "";
        try {
            httpClient.executeMethod(getMethod);
            if(getMethod.getResponseHeader("Content-Encoding")!=null){
                acceptEncoding = getMethod.getResponseHeader("Content-Encoding").getValue();
                if(acceptEncoding.toLowerCase().contains("gzip")){
                    try{
                        GZIPInputStream gzipin = new GZIPInputStream(getMethod.getResponseBodyAsStream());
                        BufferedReader br = new BufferedReader(new InputStreamReader(gzipin,charSet));
                        String line = "";
                        String html = "";
                        while((line = br.readLine())!=null)
                            html += line+"\r\n";
                        getMethod.releaseConnection();
                        return html;
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static void main(String[] args) throws UnsupportedEncodingException {
        String html = getHtml("http://stock.eastmoney.com/news/1532,20140828417237357.html","gbk");
        Document document = Jsoup.parse(html);
        Elements elements = document.getAllElements();
        String cont = "";
        for(Element ele:elements){
            if(ele.hasText()){
                cont+=ele.text()+"\n";
            }
        }
        System.out.println(cont);
    }
}
