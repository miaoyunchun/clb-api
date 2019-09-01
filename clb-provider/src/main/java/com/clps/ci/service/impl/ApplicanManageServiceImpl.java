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
import com.clps.dp.service.BalanceInqService;
import com.clps.demo.service.DemoService;
import com.clps.ci.service.ApplicanManageService;





/**
 * 分布式服务接口实现
 * 申请书管理服务
 * @author alice
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.1") // 分布式服务注解和版本号
public class ApplicanManageServiceImpl extends BaseService implements ApplicanManageService{
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	//申请书信息添加
	public Map<String, Object> ApplicanAddService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("ApplicanManageMapper.ApplicanAddMapper", map);
		
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
    //申请书信息修改
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> ApplicanUpdService(Map<String, Object> map) throws Exception {
		log.info("调用修改服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re= dao.updateByMap("ApplicanManageMapper.ApplicanUpdMapper", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> ApplicanInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("ApplicanManageMapper.ApplicanInqMapper", map);
	}	
	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> ApplicanIn1Service(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("ApplicanManageMapper.ApplicanIn1Mapper", map);
	}
}

	

	
	
	
	
	
	