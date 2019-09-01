package com.clps.cp.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.cp.pojo.CpProdparm;
import com.clps.cp.service.ProductParamService;
/**
 * @author david
 * 2016年9月18日
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class _ProductParamServiceImpl extends BaseService implements ProductParamService{

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public CpProdparm queryProductParam(CpProdparm cpProd) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (CpProdparm) dao.selectOneObject("productParamMapper.queryProductParam", cpProd);
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editProductParam(CpProdparm cpProd) throws Exception {
		log.info("调用修改服务实现");
		cpProd.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("productParamMapper.editProductParam", cpProd);
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public CpProdparm insertProductParam(CpProdparm cpProd) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		cpProd.setCreate_time( DateTimeUtils.nowToSystem());
		cpProd.setUpdate_time( DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("productParamMapper.insertProductParam", cpProd);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return cpProd;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

}
