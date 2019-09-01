package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmActEtr2Po;
import com.clps.fm.pojo.FmActEtrPo;
import com.clps.fm.service.FmDelService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmDelServiceImpl extends BaseService implements FmDelService {
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int deleteOneService(FmActEtrPo fmactetr) throws Exception {
		// 记录日志
		log.info("调用删除服务实现");
		fmactetr.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.deleteByObject("fmdelMapper.deleteOneService", fmactetr);
	}


	@Override
	public int deleteOneService1(FmActEtr2Po fmactetr2)throws Exception {
		log.info("调用删除服务实现");
		fmactetr2.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.deleteByObject("fmdelMapper.deleteOneService1", fmactetr2);
	}

	

}
