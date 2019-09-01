package com.clps.ln.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.ln.pojo.LnApplyPo;
import com.clps.ln.pojo.LnCheckContractPo;
import com.clps.ln.service.LnApplicationService;
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnApplicationServiceImpl extends BaseService implements LnApplicationService{

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int CHKlnApply(LnApplyPo lnapplypo) throws Exception {

		log.info("调用贷款客户申请书审核服务实现");
		lnapplypo.setUpdate_time(DateTimeUtils.nowToSystem());
		lnapplypo.setLncappf_intchk_date(DateTimeUtils.changeToDate());
		return dao.updateByObject("LnApplicationMapper.CHKlnApply", lnapplypo);
	}
	
	


	@Transactional(readOnly = true)//只读事务
	@Override
	public LnApplyPo LnApplyINQService(LnApplyPo lnapplypo) throws Exception {

		log.info("调用贷款客户申请书查询服务实现");
		return (LnApplyPo) dao.selectOneObject("LnApplicationMapper.LnApplyINQService", lnapplypo);
	}

}
