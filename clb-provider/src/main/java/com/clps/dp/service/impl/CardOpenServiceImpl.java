package com.clps.dp.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.service.BalanceInqService;
import com.clps.dp.service.CardOpenService;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.cp.service.CPSCTCardService;
/**
 * 分布式服务接口实现
 * 
 * @author Vee
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class CardOpenServiceImpl extends BaseService implements CardOpenService{
	// 日志对象
		private static final String ERR_NORMAL = "0000";
		private static final String ERR_DUPLICATE = "0001";
		private static final String ERR_UNHANDLED_EXCEPTION = "9999";
		private Logger log = LoggerFactory.getLogger(getClass().getName());
		
		private CPSCTCardService service1;
		// The returning map
	    Map<String, Object> returnMap = new HashMap<>();

		@SuppressWarnings("unchecked")
		public Map<String, Object> CardOpenAddService(DpCardPo dpcard) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			//map = service1.insertCPSCTCardService(map);
			log.info("调用插入服务实现");
			//添加数据到dpcard
			int re;
			try {
			 re = dao.insertOneByObject("DpCardOpenMapper.DpCardOpen2Mapper", dpcard);
			
			} catch (Exception e) {
	            returnMap = MapAndObjectUtils.copyPropertiesToMap(dpcard, returnMap);
	            returnMap.put("resp_code", ERR_UNHANDLED_EXCEPTION);
	            return returnMap;
			}	
			if (re == 1) {
	        	returnMap = MapAndObjectUtils.copyPropertiesToMap(dpcard, returnMap);
	            returnMap.put("resp_code", ERR_NORMAL);
	            return returnMap;
	        } else {
	            // 没有插入,返回数据为空，返回值为ERR_UNHANDLED_EXCEPTION
	            returnMap = new HashMap<>();
	            returnMap.put("resp_code", ERR_UNHANDLED_EXCEPTION);
	            return returnMap;
			
	        
		}

	}}
