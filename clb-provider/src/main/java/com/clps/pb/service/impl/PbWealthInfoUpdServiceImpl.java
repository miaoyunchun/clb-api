/**
 * Project Name:clb-provider
 * File Name:PBSUpdateServiceImpl.java
 * Package Name:com.clps.pb.service.impl
 * Date:2016年12月16日上午9:49:03
 * Copyright (c) 2016, christ@163.com All Rights Reserved.
 *
*/

package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.PbWealthInfoUpdService;
import com.esotericsoftware.minlog.Log;

/**
 * ClassName:PBSUpdateServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年12月16日 上午9:49:03 <br/>
 * @author   christ.guan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbWealthInfoUpdServiceImpl extends BaseService implements PbWealthInfoUpdService{
	/**
	 * PB -  财富信息的修改.
	 * 服务名:VBS-PB-ACCTDET-UPD
	 * 
	 * @param 财富信息的修改.
	 * @return 状态码。
	 * @throws 抛出异常
	 * @see com.clps.pb.service.PbWealthInfoUpdService#updatePBSCust(java.util.Map)
	 */
	//只读，可扩展，回滚到异常以前
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Exception.class)

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.pb.service.PBSUpdateService#updatePBSCust(java.util.Map)
	 */
	@Override
	public int updatePBSCust(Map<String, Object> map) throws Exception {
		Log.info("调用财富信息修改");
		// TODO Auto-generated method stub
		return dao.updateByMap("PbWealthInformationUpdMapper.updatePBSCust", map);
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.pb.service.PbWealthInfoUpdService#selectPBSCust(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> selectPBSCust(Map<String, Object> map) throws Exception {
		
		// TODO Auto-generated method stub
		//对财富信息是否存在的查询
		return (Map<String, Object>) dao.selectOneMap("PbWealthInformationUpdMapper.selectPBSCust", map);
	}


}

