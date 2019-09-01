package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fm.pojo.FmAcctDtlPo;
import com.clps.fm.service.FmAcctDetailInqService;

@Component
@Transactional
@Service(version="1.0.1")
public class FmAcctDetailInqServiceImpl extends BaseService implements FmAcctDetailInqService {
	@Override
	public FmAcctDtlPo acctDetailQueryService(FmAcctDtlPo fmactdtl) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		
//		map.put("tran_jour1",map.get("tran_jour").toString().substring(0,19));
//		map.put("tran_seq",map.get("tran_jour").toString().substring(19));
//		map.put("tran_jour",map.get("tran_jour1"));
		return (FmAcctDtlPo) dao.selectOneObject("FmAcctDetailInqMapper.fmacctQueryOneService", fmactdtl);
		
	}
	

}
