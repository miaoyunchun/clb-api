package com.clps.tp.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.tp.service.TpUNITAddService;

/**
 * TP-添加对公委托协议签约信息
 * @author zejing.wang
 * @Time：2016年12月9日 下午4:46:28
 * @version 1.0
 */
@Component
@Service(version="1.0.0") //分布式服务注解和版本号
public class TpUNITAddServiceImpl extends BaseService implements TpUNITAddService{

	/**
	 * 添加对公委托协议签约信息
	 * 服务名：VBS-TP-UNIT-ADD
	 * @param 对公委托协议签约信息添加
	 * @return 对公委托协议签约信息
	 * @throws 抛出异常
	 * */
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> insertUNITService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用添加对公委托协议签约信息服务");
		int re = dao.insertOneByObject("addForPublicMapper.insertUNITService", map);
		if(re == 1){
			//插入了一条数据，返回该数据
			return map;
		}else{
			//没有插入，返回空
			return null;
		}
		
	}

//	/**
//	 * 查询dp_dpcard表中的数据信息
//	 * */
//	@SuppressWarnings("unchecked")
//	@Override
//	public Map<String, Object> selectDpOneService(Map<String, Object> map) throws Exception {
//		// 记录日志
//		log.info("调用dp_dpcard表中的数据");
//		return (Map<String, Object>) dao.selectOneMap("addForPublicMapper.selectDpOneService", map);
//	}

}
