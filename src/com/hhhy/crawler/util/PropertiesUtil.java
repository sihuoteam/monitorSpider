package com.hhhy.crawler.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA. User: Ghost Date: 14-8-10 Time: 下午1:05 To change
 * this template use File | Settings | File Templates.
 */
public class PropertiesUtil {
	static Properties properties = new Properties();

	public static boolean loadFile(String fileName) {
		try {
			properties.load(PropertiesUtil.class.getClassLoader()
					.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
			return false;
		}
		return true;
	}

	public static String getPropertyValue(String key) {
		return properties.getProperty(key);
	}

	public static void setPropertyValue(String key, String value,
			String fileName) {
		String comments = key + "=" + value;
		// new
		// File(PropertiesUtil.class.getClassLoader().getResource(fileName).toString()
		try {
			properties.store(new FileOutputStream(new File(PropertiesUtil.class
					.getClassLoader().getResource("spiderConf.properties")
					.toString().replace("file:/", "")),true), comments);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws URISyntaxException {
		PropertiesUtil.setPropertyValue("blankingTime", "22",
				"spiderConf.properties");
		System.out.println(PropertiesUtil.getPropertyValue("blankingTime"));
	}
}
