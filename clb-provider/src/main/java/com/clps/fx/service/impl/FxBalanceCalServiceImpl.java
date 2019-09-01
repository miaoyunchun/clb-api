package com.clps.fx.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fx.service.FxBalanceCalService;

/**
 * @author feilong.song
 * @Time：2017年1月13日 下午4:26:19
 * @version 1.0
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxBalanceCalServiceImpl extends BaseService implements FxBalanceCalService{

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public Map<String, Object> balanceCalService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("balanceCalMapper.balanceCalService", map);
	}

}
