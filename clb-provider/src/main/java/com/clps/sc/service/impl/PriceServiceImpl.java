/**
 * Project Name:clb-provider
 * File Name:PriceServiceimpl.java
 * Package Name:com.clps.sc.service.impl
 * Date:2016年12月10日上午11:40:45
 * Copyright (c) 2016, lonnis@163.com All Rights Reserved.
 *
*/

package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.sc.service.PriceService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * ClassName:PriceServiceimpl 
 * Function: TODO ADD FUNCTION. 
 * Reason:	 TODO ADD REASON. 
 * Date:     2016年12月10日 上午11:40:45 
 * @author   lonnis
 * @version  4.6.0   
 * @since    JDK 1.7
 * @see 	 
 */
@Component
@Service(version="1.0.0")//分布式服务注解和版本号
public class PriceServiceImpl extends BaseService implements PriceService{
	@SuppressWarnings("unchecked")
	@Transactional(readOnly=true)//只读事物
	@Override
	public Map<String, Object> priceInqOneService(Map<String, Object> map) throws Exception {
		//日志记录
		log.info("单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("priceMapper.priceInqOneService", map);
	}


	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> priceInqListService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("priceMapper.priceInqListService_count", map);

		map = QueryListUtils.changeInputData(map, total);

		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("priceMapper.priceInqListService", map);
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
		// 返回数据
		return reMap;
	}
}

