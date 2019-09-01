package com.clps.cp.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.cp.pojo.CpSctintPo;
import com.clps.cp.service.CPSCTInterestRateService;

/**
 * 分布式服务接口实现
 * 
 * @author snow
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CPSCTInterestRateServiceImpl extends BaseService implements CPSCTInterestRateService{
	

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public int editCPSCTInterestRateService(CpSctintPo cpInst) throws Exception {
		log.info("调用修改服务实现");
		cpInst.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("CPSCTInterestRateMapper.editCPSCTInterestRateService", cpInst);
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback

	public CpSctintPo insertCPSCTInterestRateService(CpSctintPo cpInst) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		cpInst.setCreate_time(DateTimeUtils.nowToSystem());
		cpInst.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("CPSCTInterestRateMapper.insertCPSCTInterestRateService", cpInst);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return cpInst;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	
	@Transactional(readOnly = true)//只读事务
	public CpSctintPo QueryCPSCTInterestRateService(CpSctintPo cpInst) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (CpSctintPo) dao.selectOneObject("CPSCTInterestRateMapper.QueryCPSCTInterestRateService", cpInst);
	}

}
