package com.clps.dp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.service.RateUpdateService;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Service(version = "1.0.1") // 分布式服务注解和版本号

public class RateUpdateServiceImpl extends BaseService implements RateUpdateService{
	// 日志对象
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String,Object> acctRateUPDService(DpAcctPo dpacct) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("个人账户利率更新服务实现");
		int upd=dao.updateByObject("acctOpenMapper.RateUpdateMapper",dpacct);
		if(upd>0)
		{
			return  (Map<String,Object>)dao.selectOneObject("acctOpenMapper.ResultMapper", dpacct);
		}
		else
			return null ;
	}
	
}
