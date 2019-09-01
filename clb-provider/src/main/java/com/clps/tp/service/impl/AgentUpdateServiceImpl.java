package com.clps.tp.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.tp.service.AgentUpdateService;

/**
 * 对公委托协议签约更新接口实现
 *  
 *  
 * @author Clement.zhu
 * @since 2016年12月9日 10:00
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class AgentUpdateServiceImpl extends BaseService implements AgentUpdateService{

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int agntUpdService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用更新服务实现");
		return dao.updateByMap("AgentUpdateMapper.agntUpdService", map);
	}
}

