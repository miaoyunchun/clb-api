package com.clps.ci.service.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.clps.ci.service.CredPlanService;
/**
 * 分布式服务接口实现
 * 客户管理服务
 * @author Vee
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CredPlanServiceImpl extends BaseService implements CredPlanService {
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	
	//信用计划信息查询
	public Map<String, Object> CredPlanInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		map.put("plan_num", map.get("plan_numb"));
		return (Map<String, Object>) dao.selectOneMap("CiCredPlanMapper.CredPlanInqMapper", map);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	//信用计划信息添加
	public Map<String, Object> CredPlanAddService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		//Map<String,Object> map2= (Map<String, Object>) dao.selectOneMap("CiCredPlanMapper.CredPlanAddMapper", map);
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		map.put("plan_num", map.get("num"));
		map.put("plan_org", map.get("org"));
		Object temp=01;
		map.put("plan_seq", temp);
		Object temp1='R';
		map.put("plan_type", temp1);
		Object temp2=0;
		map.put("plan_prin", temp2);
		map.put("plan_int", temp2);
		map.put("plan_fee", temp2);
		map.put("plan_beg_bal", temp2);
		map.put("plan_ctd_db", temp2);
		map.put("plan_ctd_cr", temp2);
		map.put("plan_acrd_int", temp2);
		map.put("plan_beg_int", temp2);
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置8位日期格式
		Object temp3=df.format(new Date());
		map.put("plan_date",temp3);
		map.put("plan_bal", temp2);//temp2为输入接口中的 Bill_date
		int re = dao.insertOneByObject("CiCredPlanMapper.CredPlanAddMapper", map);
		
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

}
