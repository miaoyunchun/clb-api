package com.clps.ln.service.impl;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.ln.pojo.LnCustAppPo;
import com.clps.ln.service.LnCustomerApplyService;

@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class LnCustomerApplyServiceImpl extends BaseService implements LnCustomerApplyService{
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editLnCusService(LnCustAppPo lncustapppo) throws Exception {
		log.info("调用更新贷款客户信息服务实现");
		//map.put("update_time", DateTimeUtils.nowToSystem());
		
		return dao.updateByObject("lncustomerapplyMapper.editLnCusService", lncustapppo);
	}
	
	@Transactional(readOnly = true)//只读事务
	@Override
	public  LnCustAppPo QueryCusService(LnCustAppPo lncustapppo) throws Exception {
		// 记录日志
		log.info("贷款客户信息查询服务实现");
		return  (LnCustAppPo) dao.selectOneObject("lncustomerapplyMapper.QueryCusService", lncustapppo);
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> insertService(Map<String, Object> map) throws Exception {
		
		log.info("调用贷款客户申请服务实现");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("lncustomerapplyMapper.addLnCusService", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
		return null;
	}
	
	

}
}