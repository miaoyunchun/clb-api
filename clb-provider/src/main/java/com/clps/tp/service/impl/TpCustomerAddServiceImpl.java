package com.clps.tp.service.impl;

import java.util.Map;

import org.mortbay.log.Log;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.tp.service.TpCustomerAddService;

/**
 * @author Neil Ding
 * @Time：2016年12月9日 下午6:34:34
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class TpCustomerAddServiceImpl extends BaseService implements TpCustomerAddService{

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> cusInsertService(Map<String, Object> map) throws Exception {
		//记录日志
		Log.info("调用客户信息采集实现");
		
		if(map.get("agent_cre_date")==null){
			String date_now = DateTimeUtils.nowToSystem();
			map.put("agent_cre_date",DateTimeUtils.changeToDate(date_now));
		}
		
		int re = dao.insertOneByObject("customerMapper.cusInsertService", map);
		if(re == 1){
			//插入了一条数据，返回插入数据
			return map;
		}else{
			//没有插入，返回空
			return null;
		}
	}
}






