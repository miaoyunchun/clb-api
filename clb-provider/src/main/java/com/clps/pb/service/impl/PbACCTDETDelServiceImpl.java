package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.pb.service.PbACCTDETDelService;

/**
 * PB-财富账户删除
 * @author zejing.wang
 * @Time：2016年12月15日 下午2:06:14
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbACCTDETDelServiceImpl extends BaseService implements PbACCTDETDelService{
	/**
	 * 财富账户删除
	 * 服务名：VBS-PB-ACCTDET-DEL
	 * @param 财富账户删除
	 * @return 是否删除
	 * @throws 抛出异常
	 * */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int deleteOneService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用删除服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.deleteByMap("delAccountMapper.deleteOneService",map);
	}

	/**
	 * 年费退回账户查询
	 * @param 年费退回账户查询
	 * @return 年费账户信息
	 * @throws 抛出异常
	 * */
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true) //只读事务
	@Override
	public Map<String, Object> selectAcctService(Map<String, Object> map) throws Exception {
		//记录日志
		log.info("单条年费退回账户查询实现");
		return (Map<String, Object>) dao.selectOneObject("delAccountMapper.selectDpAcctService", map);
	}

}
