package com.clps.cp.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.cp.pojo.CpSctcardPo;
import com.clps.cp.service.CPSCTCardService;

/**
 * @author snow
 * 2016年9月28日
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CPSCTCardServiceImpl extends BaseService implements CPSCTCardService{

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editCPSCTCardService(CpSctcardPo cpCard) throws Exception {
		log.info("调用修改服务实现");
		cpCard.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("CPSCTCardMapper.editCPSCTCardService", cpCard);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public CpSctcardPo insertCPSCTCardService(CpSctcardPo cpCard) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		cpCard.setCreate_time(DateTimeUtils.nowToSystem());
		cpCard.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("CPSCTCardMapper.insertCPSCTCardService", cpCard);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return cpCard;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	
	@Transactional(readOnly = true)//只读事务
	public CpSctcardPo QueryCPSCTCardService(CpSctcardPo cpCard) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (CpSctcardPo) dao.selectOneObject("CPSCTCardMapper.QueryCPSCTCardService", cpCard);
	}
	

}
