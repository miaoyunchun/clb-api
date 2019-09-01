package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmActEtrPo;
import com.clps.fm.service.FmUPDService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmUPDServiceImpl extends BaseService implements FmUPDService {
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public FmActEtrPo editOneService(FmActEtrPo fmactetr) throws Exception {
		
		log.info("调用修改服务实现");
		fmactetr.setUpdate_time(DateTimeUtils.nowToSystem());

		int re = dao.updateByObject("FmUPDMapper.editOneService1", fmactetr);
		
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return fmactetr;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

}
