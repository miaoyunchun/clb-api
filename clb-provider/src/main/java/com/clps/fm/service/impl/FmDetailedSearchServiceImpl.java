package com.clps.fm.service.impl;

import java.util.HashMap;
import java.util.List;
//import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
//import com.clps.core.sys.util.DateTimeUtils;
//import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service. FmDetailedSearchService;
/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmDetailedSearchServiceImpl extends BaseService implements  FmDetailedSearchService {
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> FmDetailedSearchService1(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");
		// 搜索条件默认值处理
		if (map.get("item_jour") == null || map.get("item_jour").toString().equals("")) {
			map.put("item_jour", "1");// 默认搜索值
		}
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("FmDetailedSearchMapper.FmDetailedSearchService_count", map);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("FmDetailedSearch.FmDetailedSearchService", map);
		// 循环处理单条数据
		for (Map<String, Object> one : list) {
			one.put("new_data", "新数据");
			one.put("item_jour", one.get("item_jour") + "-改变的数据");
		}
		// 增加独立数据
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data_1", "独立数据1");
		data.put("data_2", "独立数据2");
		data.put("data_3", "独立数据3");
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, data);
		// 返回数据
		return reMap;
	}
}