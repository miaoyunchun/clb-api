/**
 * Project Name:clb-provider
 * File Name:PbTranresvChgServiceImpl.java
 * Package Name:com.clps.pb.service.impl
 * Date:2016年12月28日下午11:36:28
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
import com.clps.pb.service.PbTranresvChgUpdService;
import com.esotericsoftware.minlog.Log;

/**
 * ClassName:PbTranresvChgServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2016年12月28日 下午11:36:28 <br/>
 * @author   christ.guan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbTranresvChgUpdServiceImpl extends BaseService implements PbTranresvChgUpdService{
	/**
	 * PB -  修改转账预约.
	 * 服务名:VBS-PB-TRANRESV-CHG
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
	 * @see com.clps.pb.service.PbTranresvChgService#PbTranresvChgUpdate(java.util.Map)
	 */
	@Override
	public int PbTranresvChgUpdate(Map<String, Object> map) throws Exception {
		Log.info("调用转账预约信息修改");
		// TODO Auto-generated method stub
		return dao.updateByMap("PbTranresvChgUpdServiceMapper.PbTranresvChgUpdate", map);
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.pb.service.PbTranresvChgUpdService#lnContInq1(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> lnContInq1(Map<String, Object> map) throws Exception {
		
		// TODO Auto-generated method stub
		return (Map<String, Object>) dao.selectOneMap("PbTranresvChgUpdServiceMapper.lnContInq1", map);
	}

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.pb.service.PbTranresvChgUpdService#lnContInq2(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> lnContInq2(Map<String, Object> map) throws Exception {
		
		// TODO Auto-generated method stub
		return (Map<String, Object>) dao.selectOneMap("PbTranresvChgUpdServiceMapper.lnContInq2", map);
	}

	/**
	 * TODO 根据转账预约号（rsv_id），查询出pbfresrv_trans_stat.
	 * @see com.clps.pb.service.PbTranresvChgUpdService#lnContInq3(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> lnContInq3(Map<String, Object> map) throws Exception {
		
		// TODO Auto-generated method stub
		return (Map<String, Object>) dao.selectOneMap("PbTranresvChgUpdServiceMapper.lnContInq3", map);
	}

	/**
	 * TODO 查询是否存在（可选）.
	 * @see com.clps.pb.service.PbTranresvChgUpdService#PbTranresvChgSelect(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> PbTranresvChgSelect(Map<String, Object> map) throws Exception {
		
		// TODO Auto-generated method stub
		return (Map<String, Object>) dao.selectOneMap("PbTranresvChgUpdServiceMapper.PbTranresvChgSelect", map);
	}

}

