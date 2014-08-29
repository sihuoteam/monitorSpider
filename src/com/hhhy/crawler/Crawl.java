package com.hhhy.crawler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TimerTask;

import com.hhhy.crawler.util.MyLog;

public class Crawl extends TimerTask{
	
	HashMap<String,String> keyWords = null;
	String webName = null;
	CtrController controller;
    LinkedList<String> spyHistory = null;
    public static LinkedList<String> spyHistory1 = new LinkedList<String>();
    public static LinkedList<String> spyHistory2 = new LinkedList<String>();
    public static LinkedList<String> spyHistory3 = new LinkedList<String>();
    public static LinkedList<String> spyHistory4 = new LinkedList<String>();
    public static LinkedList<String> spyHistory5 = new LinkedList<String>();
    public static LinkedList<String> spyHistory6 = new LinkedList<String>();
    public static LinkedList<String> spyHistory7 = new LinkedList<String>();
    public static LinkedList<String> spyHistory8 = new LinkedList<String>();
    public static LinkedList<String> spyHistory9 = new LinkedList<String>();
    public static LinkedList<String> spyHistory10 = new LinkedList<String>();
    public static LinkedList<String> spyHistory11 = new LinkedList<String>();
    public static LinkedList<String> spyHistory12 = new LinkedList<String>();
    public static LinkedList<String> spyHistory13 = new LinkedList<String>();
    public static LinkedList<String> spyHistory14 = new LinkedList<String>();
    public static LinkedList<String> spyHistory15 = new LinkedList<String>();
    public static LinkedList<String> spyHistory16 = new LinkedList<String>();
    public static LinkedList<String> spyHistory17 = new LinkedList<String>();
    public static LinkedList<String> spyHistory18 = new LinkedList<String>();
    public static LinkedList<String> spyHistory19 = new LinkedList<String>();
    public static LinkedList<String> spyHistory20 = new LinkedList<String>();
    public static LinkedList<String> spyHistory21 = new LinkedList<String>();
    public static LinkedList<String> spyHistory22 = new LinkedList<String>();
    public static LinkedList<String> spyHistory23 = new LinkedList<String>();
    public static LinkedList<String> spyHistory24 = new LinkedList<String>();
    public static LinkedList<String> spyHistory25 = new LinkedList<String>();
    public static LinkedList<String> spyHistory26 = new LinkedList<String>();
    public static LinkedList<String> spyHistory27 = new LinkedList<String>();
    public static LinkedList<String> spyHistory28 = new LinkedList<String>();

	public Crawl(HashMap<String,String> keyWds,String webName,LinkedList<String> spyHistory){
        this.spyHistory = new LinkedList<String>();
        this.keyWords = keyWds;
		this.webName = webName;
    }
	@Override 
	public void run(){
		MyLog.logINFO(this.webName+" is running");

		if(this.webName.equals("bbs_hexun_com")){
            controller =  new com.hhhy.crawler.bbs_hexun_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("bbs_p5w_net")){
            controller =  new com.hhhy.crawler.bbs_p5w_net.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("cs_com_cn")){
            controller =  new com.hhhy.crawler.cs_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("finance_ifeng_com")){
            controller =  new com.hhhy.crawler.finance_ifeng_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("finance_qq_com")){
            controller =  new com.hhhy.crawler.finance_qq_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("guba_hexun_com")){
            controller =  new com.hhhy.crawler.guba_hexun_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("guba_sina_com_cn")){
            controller =  new com.hhhy.crawler.guba_sina_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_10jqka_com_cn")){
            controller =  new com.hhhy.crawler.www_10jqka_com_cn.Controller(this.keyWords,this.spyHistory);

            controller.parseBoard();
		}
		if(this.webName.equals("www_55188_com")){
            controller =  new com.hhhy.crawler.www_55188_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_askci_com")){
            controller = new com.hhhy.crawler.www_askci_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("wwww_ce_cn")){
            controller = new com.hhhy.crawler.www_ce_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_china_com_cn")){
            controller = new com.hhhy.crawler.www_china_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_chinacenn_com")){
            controller = new com.hhhy.crawler.www_chinacenn_com.Controller(this.keyWords,this.spyHistory);

            controller.parseBoard();
		}
		if(this.webName.equals("www_chinanews_com")){
            controller = new com.hhhy.crawler.www_chinanews_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_cnfol_com")){
            controller = new com.hhhy.crawler.www_cnfol_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_cnstock_com")){
            controller = new com.hhhy.crawler.www_cnstock_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_djtz_net")){
            controller = new com.hhhy.crawler.www_djtz_net.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_ftchinese_com")){
            controller = new com.hhhy.crawler.www_ftchinese_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_qianlong_com")){
            controller = new com.hhhy.crawler.www_qianlong_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_stcn_com")){
            controller = new com.hhhy.crawler.www_stcn_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_xde6_net")){
            controller = new com.hhhy.crawler.www_xde6_net.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
		if(this.webName.equals("www_zjol_com_cn")){
            controller = new com.hhhy.crawler.www_zjol_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
		}
        if(this.webName.equals("www_caijing_com_cn")){
            controller = new com.hhhy.crawler.www_caijing_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
        if(this.webName.equals("www_eeo_com_cn")){
            controller = new com.hhhy.crawler.www_eeo_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
        if(this.webName.equals("www_jrj_com_cn")){
            controller = new com.hhhy.crawler.www_jrj_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
        if(this.webName.equals("www_longhoo_net")){
            controller = new com.hhhy.crawler.www_longhoo_net.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
        if(this.webName.equals("www_financialnews_com_cn")){
            controller = new com.hhhy.crawler.www_financialnews_com_cn.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
        if(this.webName.equals("news_baidu_com")){
            controller = new com.hhhy.crawler.news_baidu_com.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
        if(this.webName.equals("search360")){
            controller = new com.hhhy.crawler.search360.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
        if(this.webName.equals("chinaso")){
            controller = new com.hhhy.crawler.chinaso.Controller(this.keyWords,this.spyHistory);
            controller.parseBoard();
        }
	}
}
