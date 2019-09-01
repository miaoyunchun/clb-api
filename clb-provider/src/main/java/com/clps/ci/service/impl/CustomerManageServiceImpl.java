package com.clps.ci.service.impl;
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
import com.clps.ci.service.CustomerManageService;
/**
 * 分布式服务接口实现
 * 客户管理服务
 * @author Wangwl
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CustomerManageServiceImpl extends BaseService implements CustomerManageService {
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	
	//信用卡客户信息查询（通过客户ID）
	public Map<String, Object> customerInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		map.put("cust_id_number", map.get("id_number"));
		return (Map<String, Object>) dao.selectOneMap("CiCustomerManageMapper.customerInqMapper", map);
	}
	
	@SuppressWarnings("unchecked")	
	//信用卡客户信息查询（通过客户卡号）
	public Map<String, Object> customerInq2Service(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
	
		return (Map<String, Object>) dao.selectOneMap("CiCustomerManageMapper.customerInq2Mapper", map);
	}
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	
	//通过客户卡号查询出客户ID
	public Map<String, Object> customerIn1Service(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		map.put("card_numb", map.get("numb"));
		Map<String,Object> map2 =(Map<String, Object>) dao.selectOneMap("CiCustomerManageMapper.customerIn1Mapper", map);
		Map<String,Object> resultMap=customerInq2Service(map2);
		return resultMap;
	}
	
	//信用卡客户信息修改

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	
	public Map<String, Object> customerUpdService(Map<String, Object> map) throws Exception {
		log.info("调用修改服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
	   //因前端需要，改变输入接口名字
		map.put("cust_name",map.get("name"));
		map.put("cust_english_name",map.get("english_name"));
		map.put("cust_nationality",map.get("nationality"));
		map.put("cust_birth_date",map.get("birth_date"));
		map.put("cust_gender",map.get("gender"));
		map.put("cust_marital_status",map.get("marital_status"));
		map.put("cust_id_type",map.get("id_type"));
		map.put("cust_id_number",map.get("id_number"));
		map.put("cust_annual_salary",map.get("annual_salary"));
		map.put("cust_mobile",map.get("mobile"));
		map.put("cust_email",map.get("email"));
		map.put("cust_bill_type",map.get("bill_type"));
		map.put("cust_bill_addr",map.get("bill_addr"));
		map.put("cust_bill_date",map.get("bill_date"));
		map.put("cust_app_date",map.get("app_date"));
		map.put("cust_live_country",map.get("live_country"));
		map.put("cust_live_province",map.get("live_province"));
		map.put("cust_live_city",map.get("live_city"));
		map.put("cust_live_district",map.get("live_district"));
		map.put("cust_live_zip_code",map.get("live_zip_code"));
		map.put("cust_live_address",map.get("live_address"));
		map.put("cust_live_years",map.get("live_years"));
		map.put("cust_company_name",map.get("company_name"));
		map.put("cust_company_country",map.get("company_country"));
		map.put("cust_company_province",map.get("company_province"));
		map.put("cust_company_city",map.get("company_city"));
		map.put("cust_company_district",map.get("company_district"));
		map.put("cust_company_zip_code",map.get("company_zip_code"));
		map.put("cust_company_address",map.get("company_address"));
		map.put("cust_company_serve_year",map.get("company_serve_year"));
		map.put("cust_qurpwd",map.get("qurpwd"));
		map.put("cust_qurpwd_wrgcnt",map.get("qurpwd_wrgcnt"));
		map.put("cust_qurpwd_wrgdate",map.get("qurpwd_wrgdate"));
		map.put("cust_qurpwd_wrgtime",map.get("qurpwd_wrgtime"));
		map.put("cust_qurpwd_last_date",map.get("qurpwd_last_date"));

		int re=dao.updateByMap("CiCustomerManageMapper.customerUpdMapper", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	//信用卡客户信息添加
	public Map<String, Object> customerAddService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		  //因前端需要，改变输入接口名字
				map.put("cust_name",map.get("name"));
				map.put("cust_english_name",map.get("english_name"));
				map.put("cust_nationality",map.get("nationality"));
				map.put("cust_birth_date",map.get("birth_date"));
				map.put("cust_gender",map.get("gender"));
				map.put("cust_marital_status",map.get("marital_status"));
				map.put("cust_id_type",map.get("id_type"));
				map.put("cust_id_number",map.get("id_number"));
				map.put("cust_annual_salary",map.get("annual_salary"));
				map.put("cust_mobile",map.get("mobile"));
				map.put("cust_email",map.get("email"));
				map.put("cust_bill_type",map.get("bill_type"));
				map.put("cust_bill_addr",map.get("bill_addr"));
				map.put("cust_bill_date",map.get("bill_date"));
				map.put("cust_app_date",map.get("app_date"));
				map.put("cust_live_country",map.get("live_country"));
				map.put("cust_live_province",map.get("live_province"));
				map.put("cust_live_city",map.get("live_city"));
				map.put("cust_live_district",map.get("live_district"));
				map.put("cust_live_zip_code",map.get("live_zip_code"));
				map.put("cust_live_address",map.get("live_address"));
				map.put("cust_live_years",map.get("live_years"));
				map.put("cust_company_name",map.get("company_name"));
				map.put("cust_company_country",map.get("company_country"));
				map.put("cust_company_province",map.get("company_province"));
				map.put("cust_company_city",map.get("company_city"));
				map.put("cust_company_district",map.get("company_district"));
				map.put("cust_company_zip_code",map.get("company_zip_code"));
				map.put("cust_company_address",map.get("company_address"));
				map.put("cust_company_serve_year",map.get("company_serve_year"));
				map.put("cust_qurpwd",map.get("qurpwd"));
				map.put("cust_qurpwd_wrgcnt",map.get("qurpwd_wrgcnt"));
				map.put("cust_qurpwd_wrgdate",map.get("qurpwd_wrgdate"));
				map.put("cust_qurpwd_wrgtime",map.get("qurpwd_wrgtime"));
				map.put("cust_qurpwd_last_date",map.get("qurpwd_last_date"));
		int re = dao.insertOneByObject("CiCustomerManageMapper.customerAddMapper", map);	
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

}

