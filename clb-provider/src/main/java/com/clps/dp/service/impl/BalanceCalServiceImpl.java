package com.clps.dp.service.impl;

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
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.service.BalanceCalService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class BalanceCalServiceImpl extends BaseService implements BalanceCalService {
	//个人人民币账户余额汇总
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> balanceCalService(DpAcctPo dpacct) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		dpacct.setCcy("cny");
		Map<String, Object> reMap=(Map<String, Object>) dao.selectOneObject("acctOpenMapper.BalanceCalService", dpacct);
		Map<String, Object> resultMap=new HashMap<String,Object>();
		resultMap.put("cust_nbr", reMap.get("cust_nbr"));
		resultMap.put("balance", reMap.get("sum"));
		return resultMap;
	}
}
