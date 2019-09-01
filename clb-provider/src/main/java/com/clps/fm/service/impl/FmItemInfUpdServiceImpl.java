package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmInfoPo;
import com.clps.fm.service.FmItemInfUpdService;


@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmItemInfUpdServiceImpl extends BaseService implements FmItemInfUpdService{
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editFMItemInfoService(FmInfoPo fminfo) throws Exception {
		// TODO Auto-generated method stub
		log.info("调用修改服务实现");
		fminfo.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("FMItemInfUpdMapper.editFmItemInfService", fminfo);
	}
	

}
