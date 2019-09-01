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
import com.clps.demo.service.DemoService;
import com.clps.ab.service.PosfeeService;


/**
 * 分布式服务接口实现
 * 
 * @author Alice 
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PosfeeServiceImpl extends BaseService implements PosfeeService{
	
	//收单费用信息添加服务
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public Map<String, Object> PosfeeAddService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用添加服务实现");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("PosfeeMapper.PosfeeAddMapper", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	//收单费用信息修改服务
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public int PosfeeUpdService(Map<String, Object> map) throws Exception {
		log.info("调用修改服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("PosfeeMapper.PosfeeUpdMapper", map);
	}
	
	//收单费用信息查询服务
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public Map<String, Object> PosfeeInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("PosfeeMapper.PosfeeInqMapper", map);
	}
	

}


