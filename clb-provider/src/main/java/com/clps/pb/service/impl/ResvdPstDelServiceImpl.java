package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.pb.service.PbResvdPstDelService;

/**
 * @author Neil Ding
 * @Time：2016年12月16日 上午9:21:19
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class ResvdPstDelServiceImpl extends BaseService implements PbResvdPstDelService{
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int deleteOneService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用删除服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.deleteByMap("ResvdPstDelMapper.deleteOneService", map);
	}
}
