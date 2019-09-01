package com.clps.fm.service.impl;



import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fm.pojo.FmItemDtlPo;
import com.clps.fm.service.FmItemdInqService;
@Component
@Transactional
@Service(version="1.0.0")
public class FmItemdInqServiceImpl extends BaseService implements FmItemdInqService{
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public FmItemDtlPo fmQueryOneService(FmItemDtlPo fmitemdtl) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (FmItemDtlPo) dao.selectOneObject("FmItemdInqMapper.fmQueryOneService", fmitemdtl);
	}
	
}