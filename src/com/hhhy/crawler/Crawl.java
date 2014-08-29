package com.hhhy.crawler;

import java.util.TimerTask;

import com.hhhy.crawler.util.MyLog;

public class Crawl extends TimerTask{
	String webName = null;
	CtrController controller;

	public Crawl(String webName){
		this.webName = webName;
    }
	@Override 
	public void run(){
		MyLog.logINFO(this.webName+" is running");

		if(this.webName.equals("bbs_hexun_com")){
            controller =  new com.hhhy.crawler.bbs_hexun_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("bbs_p5w_net")){
            controller =  new com.hhhy.crawler.bbs_p5w_net.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("cs_com_cn")){
            controller =  new com.hhhy.crawler.cs_com_cn.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("finance_ifeng_com")){
            controller =  new com.hhhy.crawler.finance_ifeng_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("finance_qq_com")){
            controller =  new com.hhhy.crawler.finance_qq_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("guba_hexun_com")){
            controller =  new com.hhhy.crawler.guba_hexun_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("guba_sina_com_cn")){
            controller =  new com.hhhy.crawler.guba_sina_com_cn.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_10jqka_com_cn")){
            controller =  new com.hhhy.crawler.www_10jqka_com_cn.Controller();

            controller.parseBoard();
		}
		if(this.webName.equals("www_55188_com")){
            controller =  new com.hhhy.crawler.www_55188_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_askci_com")){
            controller = new com.hhhy.crawler.www_askci_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("wwww_ce_cn")){
            controller = new com.hhhy.crawler.www_ce_cn.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_china_com_cn")){
            controller = new com.hhhy.crawler.www_china_com_cn.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_chinacenn_com")){
            controller = new com.hhhy.crawler.www_chinacenn_com.Controller();

            controller.parseBoard();
		}
		if(this.webName.equals("www_chinanews_com")){
            controller = new com.hhhy.crawler.www_chinanews_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_cnfol_com")){
            controller = new com.hhhy.crawler.www_cnfol_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_cnstock_com")){
            controller = new com.hhhy.crawler.www_cnstock_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_djtz_net")){
            controller = new com.hhhy.crawler.www_djtz_net.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_ftchinese_com")){
            controller = new com.hhhy.crawler.www_ftchinese_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_qianlong_com")){
            controller = new com.hhhy.crawler.www_qianlong_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_stcn_com")){
            controller = new com.hhhy.crawler.www_stcn_com.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_xde6_net")){
            controller = new com.hhhy.crawler.www_xde6_net.Controller();
            controller.parseBoard();
		}
		if(this.webName.equals("www_zjol_com_cn")){
            controller = new com.hhhy.crawler.www_zjol_com_cn.Controller();
            controller.parseBoard();
		}
        if(this.webName.equals("www_caijing_com_cn")){
            controller = new com.hhhy.crawler.www_caijing_com_cn.Controller();
            controller.parseBoard();
        }
        if(this.webName.equals("www_eeo_com_cn")){
            controller = new com.hhhy.crawler.www_eeo_com_cn.Controller();
            controller.parseBoard();
        }
        if(this.webName.equals("www_jrj_com_cn")){
            controller = new com.hhhy.crawler.www_jrj_com_cn.Controller();
            controller.parseBoard();
        }
        if(this.webName.equals("www_longhoo_net")){
            controller = new com.hhhy.crawler.www_longhoo_net.Controller();
            controller.parseBoard();
        }
        if(this.webName.equals("www_financialnews_com_cn")){
            controller = new com.hhhy.crawler.www_financialnews_com_cn.Controller();
            controller.parseBoard();
        }
        if(this.webName.equals("news_baidu_com")){
            controller = new com.hhhy.crawler.news_baidu_com.Controller();
            controller.parseBoard();
        }
        if(this.webName.equals("search360")){
            controller = new com.hhhy.crawler.search360.Controller();
            controller.parseBoard();
        }
        if(this.webName.equals("chinaso")){
            controller = new com.hhhy.crawler.chinaso.Controller();
            controller.parseBoard();
        }
	}
}
