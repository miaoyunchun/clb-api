package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.ResvdpstAddService;

/**
 * 对预约开立定存单的实现
 * 
 * @author Clement.zhu
 * @since 2016年12月15日 16:00
 */
@Component
@Service(version="1.0.0")//分布式服务注解和版本号
public class ResvdpstAddServiceImpl extends BaseService implements ResvdpstAddService{

	/**
     * 预约开立定存单
     *
     * @param map
     * @return int
     * @throws Exception
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int resvdpstAddService(Map<String, Object> map) throws Exception {
		log.info("预约开立定存单");
		
		return dao.insertOneByMap("ResvdpstAddMapper.insertresvdpst",map);
	}

	/**
     * 查询转账账户
     *
     * @param map
     * @return map
     * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读服务
	@Override
	public Map<String, Object> resvdpstInqService(Map<String, Object> map) throws Exception {
		log.info("查询转出账户");
		return (Map<String, Object>) dao.selectOneMap("ResvdpstAddMapper.AccountAdd",map);
	}
}

