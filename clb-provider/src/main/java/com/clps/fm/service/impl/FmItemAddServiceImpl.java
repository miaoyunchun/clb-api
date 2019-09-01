package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmLgdProcPo;
import com.clps.fm.service.FmItemAddService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmItemAddServiceImpl extends BaseService implements FmItemAddService {
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public FmLgdProcPo insertServiceItemAdd(FmLgdProcPo fmlgitm) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		fmlgitm.setOp_time_stamp(DateTimeUtils.nowToSystem());
		fmlgitm.setCreate_time(DateTimeUtils.nowToSystem());
		fmlgitm.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("FmItemAddMapper.insertServiceItemAdd", fmlgitm);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return fmlgitm;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

}
