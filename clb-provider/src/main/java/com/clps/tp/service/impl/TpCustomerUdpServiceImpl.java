package com.clps.tp.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.tp.service.TpCustomerUdpService;

/**
 * @author feilong.song
 * @Time：2016年12月9日 下午4:56:13
 * @version 1.0
 */


/**
 * 分布式服务接口实现
 * 
 * @author feilong.song
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class TpCustomerUdpServiceImpl extends BaseService implements TpCustomerUdpService{

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public int updateService(Map<String, Object> map) throws Exception {
		log.info("调用更新服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("tpupdMapper.updateService", map);
		
	}
	
	
	

}
