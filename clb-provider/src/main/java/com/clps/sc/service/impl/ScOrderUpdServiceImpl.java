package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.sc.service.ScOrderUpdService;
import org.mortbay.log.Log;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * SC-订单维护
 * @author zejing.wang
 * @Time：2016年12月30日 上午11:07:53
 * @version 1.0
 */
@Component
@Service(version="1.0.0") //分布式服务注解和版本号
public class ScOrderUpdServiceImpl extends BaseService implements ScOrderUpdService{

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int orderUpdService(Map<String, Object> map) throws Exception {
		Log.info("调用订单维护服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("ScOrderUpdMapper.updateOrder", map);
	}

}
