/**
 * Project Name:clb-provider
 * File Name:TPUNITInqServiceImpl.java
 * Package Name:com.clps.tp.service.impl
 * Date:2016年12月9日下午5:10:33
 * Copyright (c) 2016, christ@163.com All Rights Reserved.
 *
*/

package com.clps.tp.service.impl;



import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.tp.service.TPUNITInqService;
import com.esotericsoftware.minlog.Log;

/**
 * ClassName:TPUNITInqServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO REASON:对公委托签约信息de查询 <br/>
 * Date:     2016年12月9日 下午5:10:33 <br/>
 * @author   christ.guan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Component
@Service(version="1.0.0")//分布式服务注解和版本号
public class TPUNITInqServiceImpl extends BaseService implements TPUNITInqService{


	/**
	 * TP -  对公委托协议签约信息内容查询.
	 * 服务名:VBS-TP-UNIT-INQ
	 * 
	 * @param 公共委托签约信息
	 * @return 返回签约信息
	 * @throws 抛出异常
	 * @see com.clps.tp.service.TPUNITInqService#selectUnitInfoService(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	//只读，可扩展，回滚到异常以前
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Exception.class)

	/**
	 * TODO 简单描述该方法的实现功能（可选）.
	 * @see com.clps.tp.service.TPUNITInqService#selectOneService(java.util.Map)
	 */
	@Override
	public Map<String, Object> selectOneService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		Log.info("调用查询对公委托协议签约信息");//查询时只需要输入营业执照号
		// TODO Auto-generated method stub
		return (Map<String, Object>)dao.selectOneObject("queryForPublicMapper.selectOneService", map);
	}

}

