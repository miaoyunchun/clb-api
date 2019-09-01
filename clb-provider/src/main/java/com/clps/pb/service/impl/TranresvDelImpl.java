package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.TranresvDel;

/**
 * 对删除转账预约的实现
 * 
 * @author Clement.zhu
 * @since 2016年12月28日 16:00
 */
@Component
@Service(version="1.0.0")//分布式服务注解和版本号
public class TranresvDelImpl extends BaseService implements TranresvDel {
	
	/**
     * 删除转账预约
     *  
     * @param map  转账预约号
     * @return int 删除返回信息字段
     * @throws Exception
     */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int tranresvDel(Map<String, Object> map) throws Exception {
		log.info("开始删除转账预约服务");
		return dao.deleteByMap("TranresvDelMapper.delService", map);
	}

}

