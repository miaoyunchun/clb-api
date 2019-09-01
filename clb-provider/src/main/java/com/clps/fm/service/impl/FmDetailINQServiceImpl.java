package com.clps.fm.service.impl;



import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fm.pojo.FmTxnDtlPo;
import com.clps.fm.service.FmDetailINQService;
@Component
@Transactional
@Service(version="1.0.0")
public class FmDetailINQServiceImpl extends BaseService implements FmDetailINQService{
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public FmTxnDtlPo fmQueryOneService(FmTxnDtlPo fmtxndtl) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (FmTxnDtlPo) dao.selectOneObject("FmDetailINQMapper.fmdetailinqQueryOneService", fmtxndtl);
	}
	
}