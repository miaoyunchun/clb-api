package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmItemDtlPo;
import com.clps.fm.service.FmItemdJurService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmItemdJurServiceImpl extends BaseService implements FmItemdJurService {
	
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public FmItemDtlPo insertService(FmItemDtlPo fmitemdtl) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		fmitemdtl.setTxn_time(DateTimeUtils.changeToTime());
		fmitemdtl.setTxn_date_t(DateTimeUtils.changeToDate());
		fmitemdtl.setCreate_time(DateTimeUtils.nowToSystem());
		fmitemdtl.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("FmItemdJurMapper.insertServiceItemdJur", fmitemdtl);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return fmitemdtl;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
}
