package com.hhhy.crawler.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.hhhy.db.beans.Article;

public class JsonUtils {
    private static Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        return gson.fromJson(json, typeOfT);
    }

    public static Map<String, String> toMap(String jsonStr)
            throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonStr);
        Map<String, String> result = new HashMap<String, String>();
        Iterator iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = (String) iterator.next();
            String val = jsonObject.getString(key);
            result.put(key, val);
        }
        return result;
    }

    public static void main(String[] args) {
        Map<String, String> s = new HashMap<String, String>();
        s.put("s1", "v1");
        s.put("s2", "v2");
        s.put("s3", "v3");
        System.out.println(JsonUtils.toJson(s));
        Article art = new Article("title", "content", "url", "website");
        art.setSummary("summary");
        Map<String, Object> map = JsonUtils.fromJson(JsonUtils.toJson(s), Map.class);
        System.out.println(map.get("s1"));
        System.out.println(map.get("s2"));
        System.out.println(map.get("s3"));
        
        Article art2 = JsonUtils.fromJson(JsonUtils.toJson(art), Article.class);
        System.out.println(JsonUtils.toJson(art));
        System.out.println(art2.getTitle());
        System.out.println(art2.getContent());
        System.out.println(art2.getKeyword());
        System.out.println(art2.getSummary());
        Article art3 = JsonUtils.fromJson("{\"hehe\":123}", Article.class);
//        System.out.println(JsonUtils.toJson("haha"));
    }

}
