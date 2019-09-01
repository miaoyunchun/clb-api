/**
 * Project Name:clb-provider
 * File Name:PriveServiceImpl.java
 * Package Name:com.clps.sc.service.impl
 * Date:2016年12月9日下午12:01:29
 * Copyright (c) 2016, All Rights Reserved.
 *
*/

package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.sc.service.PriceMaintService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 证券价格信息更新
 *
 * @author Ruby.Zhao
 * @since 2016年12月9日 下午2:00:58
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PriceMaintServiceImpl extends BaseService implements PriceMaintService{

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int priceUpdService(Map<String, Object> map) throws Exception {
		log.info("证券价格信息更新");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("priceMaintMapper.priceUpdService", map);
	}

	

}

