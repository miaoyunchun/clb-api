package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.DepositDelService;

/**
 * 对财富优惠定存撤销接口实现
 * 
 * @author Clement.zhu
 * @since 2016年12月15日 10:00
 */
@Component
@Service(version="1.0.0")//分布式服务注解和版本号
public class DepositDelServiceImpl extends BaseService implements DepositDelService{
	 /**
     * 财富优惠定存撤销 
     * @param map 客户号信息字段
     * @return int 删除返回信息字段
     * @throws Exception
     */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int depositDelService(Map<String,Object> map) throws Exception {
		log.info("开始进行财富优惠定存撤销");
		return dao.deleteByMap("depositDelMapper.depositdel", map);
	}
}

