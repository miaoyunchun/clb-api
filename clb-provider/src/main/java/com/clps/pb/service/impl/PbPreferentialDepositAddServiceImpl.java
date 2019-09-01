/**
 * Project Name:clb-provider
 * File Name:PBSInsertServiceImpl.java
 * Package Name:com.clps.pb.service.impl
 * Date:2016年12月16日下午12:17:49
 * Copyright (c) 2016, christ@163.com All Rights Reserved.
 *
*/

package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.PbPreferentialDepositAddService;

/**
 * ClassName:PBSInsertServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年12月16日 下午12:17:49 <br/>
 * @author   christ.guan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbPreferentialDepositAddServiceImpl extends BaseService implements PbPreferentialDepositAddService{

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.pb.service.PbPreferentialDepositAddService#insertPBSCust(java.util.Map)
	 */
	@Override
	public int insertPBSCust(Map<String, Object> map) throws Exception {
		//记录日志
		log.info("调用单条注册服务实现");
		return dao.insertOneByMap("PbPreferentialDepositAddMapper.insertPBSCust", map);
	}



	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.pb.service.PbPreferentialDepositAddService#selectUnion(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> selectUnion(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		log.info("查询pb_dprd表");
		return (Map<String, Object>)dao.selectOneObject("PbPreferentialDepositAddMapper.selectUnion", map);
	}



	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.pb.service.PbPreferentialDepositAddService#PblnContractInq(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> PblnContractInq(Map<String, Object> map) throws Exception {
		
		log.info("调用VBS.DP.ACCOUNT2.INQ服务实现");
		return (Map<String, Object>)dao.selectOneObject("PbPreferentialDepositAddMapper.PblnContractInq", map);
	}

}

