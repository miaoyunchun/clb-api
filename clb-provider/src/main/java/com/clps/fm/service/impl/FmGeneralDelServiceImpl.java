package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmLgdProcPo;
import com.clps.fm.service.FmGeneralDelService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmGeneralDelServiceImpl extends BaseService implements FmGeneralDelService {
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int deleteOneServicegen(FmLgdProcPo fmlgitm) throws Exception {
		// 记录日志
		log.info("调用删除服务实现");
		fmlgitm.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.deleteByObject("fmgeneraldelMapper.deleteOneServicegen", fmlgitm);
	}


	

}