package com.clps.ab.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ab.service.PosfeeManagementService;

/**
* PosfeeManagement
* 
* @author Vee
*/
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PosfeeManagementServiceImpl extends BaseService implements PosfeeManagementService {
		
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> PosfeeManagementInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用信用卡收单费用信息查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("PosfeeManagementMapper.PosfeeManagementInqService", map);
	}
		
		
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> PosfeeManagementAddService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用信用卡收单费用信息添加服务实现");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("PosfeeManagementMapper.PosfeeManagementAddService", map);
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
	public int PosfeeManagementUpdService(Map<String, Object> map) throws Exception {
		log.info("调用信用卡收单费用信息维护服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("PosfeeManagementMapper.PosfeeManagementUpdService", map);
	}
}
	
