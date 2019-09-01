package com.clps.ab.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.demo.service.DemoService;
import com.clps.ab.service.ManAutService;
import com.clps.ab.service.PostXnService;


/**
 * 分布式服务接口实现
 * 
 * @author joker 
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.1") // 分布式服务注解和版本号

public class PostXnServiceImpl extends BaseService implements PostXnService{
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
		private ManAutService service;// 信用卡发卡行授权服务接口（空接口，对应的业务还没完成）
		
		@SuppressWarnings("unchecked")
		public Map<String, Object> PostxnAutService(Map<String, Object> map) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用单条查询服务实现");

			String si;  //接收map的卡号前6位
			si = (String)map.get("card_no");
			//判断是否为本本收单行
		    if(si=="666666"){
			 String s; //接收map的Expiry_date值
			 s = (String)map.get("Expiry_date");
			 map.put("rxpiry_date", "20"+s.substring(0,1)+"-"+s.substring(2,3)+"-"+"01");
		 	 map.put("create_time", df.format(new Date()));
			 map.put("update_time", df.format(new Date()));
			 Map<String,Object> reslutMap = service.PostService(map);
			 return reslutMap;
			}
		    
		    else{
			 int ws_rc_code;//0~99的随机数
			 Random r=new Random();
			 ws_rc_code = r.nextInt(100);
			 //分类判别随机的取值区间的操作
			 if(ws_rc_code>=0 && ws_rc_code<75){
				map.put("resp_code", "000");
				map.put("resp_msg","AUTHORIZE SUCCESSFUL");
			 }
			 else if(ws_rc_code>=75 && ws_rc_code<80){
				map.put("resp_code", "001");
				map.put("resp_msg","AUTHORIZE FAIL,CARD NOT ACTIVE");
			 }
			 else if(ws_rc_code>=80 && ws_rc_code<85){
				map.put("resp_code", "002");
				map.put("resp_msg","AUTHORIZE FAIL, CVV2 NOT CORRECT");
			 }
			 else if(ws_rc_code>=85 && ws_rc_code<90){
				map.put("resp_code", "003");
				map.put("resp_msg","AUTHORIZE FAIL, EXPIRY DATE NOT CORECT");
			 }
			 else if(ws_rc_code>=90 && ws_rc_code<95){
				map.put("resp_code", "004");
				map.put("resp_msg","AUTHORIZE FAIL, NO AVALIABLE LIMIT");
			 }
			 else if(ws_rc_code>=95 && ws_rc_code<98){
				map.put("resp_code", "005");
				map.put("resp_msg","AUTHORIZE FAIL, NO AVALIABLE CASH LIMIT");
			 }
			 else if(ws_rc_code>=98 && ws_rc_code<=99){
				map.put("resp_code", "006");
				map.put("resp_msg","UNKNOWN TRANSACTION CODE");
			 }
			//赋值作输出接口
			map.put("Timestamp", df.format(new Date()));
			map.put("auth_code", r.nextInt(899999)+100000);
			map.put("Tran_ref_num",df.format(new Date())+map.get("tran_code")+map.get("auth_code"));
			return map;
		   }
		
	  }
}
