package com.clps.fm.service.impl;



import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fm.pojo.FmLgdProcPo;
import com.clps.fm.service.FmItemINQService;
@Component
@Transactional
@Service(version="1.0.0")
public class FmItemINQServiceImpl extends BaseService implements FmItemINQService{
	@Transactional(readOnly = true)//只读事务
	@Override
	public FmLgdProcPo fmQueryOneService(FmLgdProcPo fmlgitm) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (FmLgdProcPo) dao.selectOneObject("FmItemINQMapper.fmiteminqQueryOneService", fmlgitm);
	}

}