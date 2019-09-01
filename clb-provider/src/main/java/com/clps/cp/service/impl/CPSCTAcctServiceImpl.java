package com.clps.cp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.cp.pojo.CpSctacctPo;
import com.clps.cp.service.CPSCTAcctService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CPSCTAcctServiceImpl extends BaseService implements CPSCTAcctService {
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public CpSctacctPo queryAcct(CpSctacctPo cpvo) throws Exception {
		// 记录日志
		log.info("调用查询服务");
		return (CpSctacctPo) dao.selectOneObject("cp_sct_acctMapper.queryAcct", cpvo);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editAcct(CpSctacctPo cpvo) throws Exception {
		log.info("调用修改服务");
		cpvo.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("cp_sct_acctMapper.editAcct", cpvo);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public CpSctacctPo insertAcct(CpSctacctPo cpvo) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		cpvo.setCreate_time(DateTimeUtils.nowToSystem());
		cpvo.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("cp_sct_acctMapper.insertAcct", cpvo);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return cpvo;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

}

