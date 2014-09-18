package gubaSpider;

import com.hhhy.crawler.util.*;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by Ghost on 2014/9/18 0018.
 */
public class eastmoney {
    String url;
    private static HttpGet setHttpGet(String url){
        HttpGet httpGet = null;
        httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.2) Gecko/2008070208 Firefox/3.0.1");
        httpGet.setHeader("Referer","http://guba.eastmoney.com/");
        httpGet.setHeader("Host","passport.eastmoney.com");
        return httpGet;
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
    public eastmoney() throws IOException {
        String dlm = "ghostinmatrix";
        String password = "19901109abc";


        url = "http://passport.eastmoney.com/guba/AjaxAction.ashx?cb=jQuery18309460038826800883_1411024516265&op=login&dlm="+dlm+"&mm="+password+"&vcode=&_=1411024534921";
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpGet httpGet = setHttpGet(url);
        HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
        int statuscode = httpResponse.getStatusLine().getStatusCode();
        if(statuscode==200){
            System.out.println("aa");
        }

        EntityUtils.consume(httpResponse.getEntity());

        CookieStore cookie = defaultHttpClient.getCookieStore();
        List<Cookie> cookieList = cookie.getCookies();
        for(Cookie cookie1:cookieList){
            System.out.println(cookie1.getName());
            System.out.println(cookie1.getPath());
            System.out.println(cookie1.getComment());
            System.out.println(cookie1.getCommentURL());
            System.out.println(cookie1.getDomain());
            System.out.println(cookie1.getValue());
        }
        String targetUrl = "http://guba.eastmoney.com/action.aspx";
        HashMap<String,String> params = new HashMap<String, String>();



        params.put("action","review3");
        params.put("code","hxnc");
        params.put("text","shjj");
        params.put("topic_id","123643213");
        params.put("huifu_id","");
        params.put("yzm","");
        params.put("yzm_id","");

        HashMap<String,String> headParams = new HashMap<String, String>();
        headParams.put("Host","guba.eastmoney.com");
        headParams.put("Referer","http://guba.eastmoney.com/news,hxnc,123643213.html");
        headParams.put("Origin","http://guba.eastmoney.com");
        headParams.put("X-Requested-With","XMLHttpRequest");


        HttpPost httpPost = postForm(targetUrl,params,headParams);
        DefaultHttpClient defaultHttpClient1 = new DefaultHttpClient();
        defaultHttpClient1.setCookieStore(cookie);
        HttpResponse response = defaultHttpClient1.execute(httpPost);
        int code = response.getStatusLine().getStatusCode();
        if(code == 200)
            System.out.println("ssssssssssssssssss");

    }
    public static void main(String[] args) throws IOException {
        eastmoney e = new eastmoney();

    }
}
