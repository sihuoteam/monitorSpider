package com.hhhy.crawler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Ghost
 * Date: 14-8-12
 * Time: 下午7:27
 * To change this template use File | Settings | File Templates.
 */
public class CtrController {
	public LinkedList<String> spyHistory = null;
	public HashMap<String,String> keyWords = null;
	public CtrController(HashMap<String,String> kW,LinkedList<String> spyHistory){
		this.keyWords = kW;
        this.spyHistory = spyHistory;
	}
	public void parseBoard(){};
	public void parsePages(ArrayList<?> tableList,Map.Entry<String,String> entry){};
}
