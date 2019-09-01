package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.PbACCTDETInqService;

/**
 * PB-财富账户查询
 * @author zejing.wang
 * @Time：2016年12月15日 下午2:41:40
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") //分布式服务注解和版本号
public class PbACCTDETInqServiceImpl extends BaseService implements PbACCTDETInqService{
	/**
	 * 财富账户查询
	 * 服务名：VBS-PB-ACCTDET-INQ
	 * @param  财富账户查询
	 * @return 财富账户信息
	 * @throws 抛出异常
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true) //只读事务
	@Override
	public Map<String, Object> queryOneService(Map<String, Object> map) throws Exception {
		//记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneObject("queryAccountMapper.queryOneService", map);
	}

}
