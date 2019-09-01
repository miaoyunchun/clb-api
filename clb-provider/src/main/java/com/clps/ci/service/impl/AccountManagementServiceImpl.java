package com.clps.ci.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.clps.ci.service.AccountManagementService;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Map;


/**
 * 分布式服务接口实现
 * 
 * @author Sunday
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class AccountManagementServiceImpl extends BaseService implements AccountManagementService {
	
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	/* (non-Javadoc)
	 * @see com.clps.ci.service.AccountManagementService#AccountManagementAddService(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	
	@Override
	//添加信息服务
	public Map<String, Object> AccountManagementAddService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
         log.info("调用插入服务实现");      
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		map.put("acct_org", "000");
		map.put("acct_num", map.get("numb"));
		map.put("acct_prod", "00000000");
		map.put("acct_cust_num", map.get("Cust_numb"));
		map.put("acct_status", "a");
		map.put("acct_billing_cycle", map.get("Bill_date"));
		map.put("acct_curr_bal", "0");
		map.put("acct_crlim", map.get("Limit"));
		map.put("acct_otb",map.get("Limit"));	
		//把map.get("Limit")转换成double型，乘以0.5,保存在result中，put到acct_cash_otb字段
		Object temp=map.get("Limit");
		double temp1=Double.parseDouble(temp.toString()); 
		double temp2=temp1*0.5; 
		Double result=temp2;
		map.put("acct_cash_otb",result);
		map.put("acct_memo_db", "0");
		map.put("acct_memo_cr", "0");
		map.put("acct_due", "0");
		map.put("acct_due_date", "00010101");
		map.put("acct_block_code", "z");
		map.put("acct_block_code_date", "00010101");
		map.put("acct_last_stmt_date", "00010101");
		map.put("acct_next_stmt_date", DateNext());//调用函数DateNext获取当前日期+1个月
		map.put("acct_last_retail_date", "00010101");
		map.put("acct_last_cash_date", "00010101");
		map.put("acct_last_pymt_date", "00010101");
		map.put("acct_last_int_accru_date", "00010101");
		map.put("acct_create_date", DateNow());//调用函数DateNext获取当前日期
		map.put("acct_close_date", "00010101");
		map.put("acct_last_maint_date", DateNow());//调用函数DateNext获取当前日期
		map.put("acct_currency_code", "000");
		map.put("acct_currency_flag", "L");
		map.put("acct_dual_org", "000");
		map.put("acct_dual_currency_code", "000");
		map.put("create_time",  DateTimeUtils.nowToSystem());
		map.put("create_user", map.get("create_user"));
		map.put("update_time", DateTimeUtils.nowToSystem());
		map.put("update_user", map.get("update_user"));
	
		int re = dao.insertOneByObject("dpAccountManagementMapper.AccountManagementAddService", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	
	}//获得当前日期YYYY-MM-DD
	public Object DateNow(){
		Calendar c = Calendar.getInstance();
		int year1 = c.get(Calendar.YEAR); 
		int month1 = c.get(Calendar.MONTH)+1; 
		int date1 = c.get(Calendar.DATE);  
		String timeNow=null;//当前日期
		if(month1>10)
			 timeNow=Integer.toString(year1)+Integer.toString(month1)+Integer.toString(date1);
			else
			 timeNow=Integer.toString(year1)+"0"+Integer.toString(month1)+Integer.toString(date1);
		return timeNow;	
	}//获得下一个月的日期YYYY-MM-DD
	public Object DateNext(){
		Calendar c = Calendar.getInstance();
		int year1 = c.get(Calendar.YEAR); 
		int month1 = c.get(Calendar.MONTH)+1; 
		int date1 = c.get(Calendar.DATE);
		String timeNext=null;//下个月的当前日期
		if(month1==12){
			year1+=1;
			month1=1;
		}
		else
		{
			month1+=1;
		}
		if(month1>10)
		 timeNext=Integer.toString(year1)+Integer.toString(month1)+Integer.toString(date1);
		else
		 timeNext=Integer.toString(year1)+"0"+Integer.toString(month1)+Integer.toString(date1);
		return timeNext;
	}
		

	@Override
	public int AccountManagementUpdateService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		log.info("调用修改服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		
		return dao.updateByMap("dpAccountManagementMapper.AccountManagementUpdateMapper", map);
	
	}

	@Override
	public Map<String, Object> AccountManagementacct_numberService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("dpAccountManagementMapper.AccountManagementacct_numberMapper", map);
	}

	@Override
	
	public Map<String, Object> AccountManagementcust_noService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("dpAccountManagementMapper.AccountManagementcust_noMapper", map);
		
}
}







