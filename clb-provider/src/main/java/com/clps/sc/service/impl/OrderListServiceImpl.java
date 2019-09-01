/**
 * Project Name:clb-provider
 * File Name:OrderServiceImpl.java
 * Package Name:com.clps.sc.service.impl
 * Date:2016年12月29日上午10:40:45
 * Copyright (c) 2016, lonnie@163.com All Rights Reserved.
 *
*/

package com.clps.sc.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.sc.service.OrderListService;

/**
 * ClassName:OrderServiceImpl 
 * Function: TODO ADD FUNCTION. 
 * Reason:	 TODO ADD REASON. 
 * Date:     2016年12月29日 上午10:40:45 
 * @author   lonnie
 * @version  4.6.0   
 * @since    JDK 1.8
 * @see 	 
 */
@Component
@Service(version="1.0.0")//分布式服务注解和版本号
public class OrderListServiceImpl extends BaseService implements OrderListService{
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> OrderInqListService(Map<String, Object> map) throws Exception {
		//记录日志
		log.info("调用多条服务查询实现");
//		//搜索条件默认值处理
//		if(map.get("acct_nbr") == null || map.get("acct_nbr").toString().equals("")){
//			map.put("acct_nbr", "31018880000000000070");// 默认搜索值
//		}
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("orderMapper.orderInqListService_count", map);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("orderMapper.orderInqListService", map);
//		// 循环处理单条数据
//			for (Map<String, Object> one : list) {
//				one.put("new_data", "新数据");
//				one.put("acct_nbr", one.get("acct_nbr") + "-改变的数据");
//			}
//			// 增加独立数据
//			Map<String, Object> data = new HashMap<String, Object>();
//			data.put("data_1", "独立数据1");
//			data.put("data_2", "独立数据2");
//			data.put("data_3", "独立数据3");
			// 处理返回
			Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total);
			// 返回数据
			return reMap;
			}
}

