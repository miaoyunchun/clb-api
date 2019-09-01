/**
 * @author Lacus
 * @Time：2016年12月15日 上午11:21:49
 * @version 1.0  
 */
package com.clps.sc.service.impl;

import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.common.util.JsonResponseUtils;
import com.clps.core.common.vo.JsonRequestVo;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.sc.service.ProdfService;
@Component
@Service(version = "1.0.0")		//分布式服务注解
public class ProdfServiceImpl extends BaseService implements ProdfService{

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public Map<String, Object> prodfAdd(Map<String, Object> map)
			throws Exception {
		log.info("调用插入服务实现");
		
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		
			int re = dao.insertOneByObject("prodfMapper.prodfAdd", map);
			if(re==1){
				return map;
			}else{
				return null;
			}
	}

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED,rollbackFor=Exception.class)
	@Override
	public Map<String, Object> priceAdd(Map<String, Object> map)
			throws Exception {
		log.info("调用插入服务实现");
		
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		
			int re = dao.insertOneByObject("prodfMapper.priceAdd", map);
			if(re==1){
				return map;
			}else{
				return null;
			}
	}
}
