/**
 * 
 */
package com.clps.fm.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmActItemLstService;

/**
 * TODO: Descriptions here
 * 
 * @author TODO: Aaron
 *
 * 2017-05-07 下午3:46:24
 *
 * @version v1.0
 */
public class FmActItemLstServiceImpl extends BaseService implements FmActItemLstService{
	
	@SuppressWarnings("unused")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> actItemQueryListService(Map<String,Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");
		
		
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("FmActItemLstMapper.actItemQueryListService_count", map);
		
		//TODO 插入 
		map = QueryListUtils.changeInputData(map,total);
		
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("FmActItemLstMapper.actItemQueryListService", map);
		
		String item_key=list.get(list.size() - 1).get("item_key").toString();
    	String buss_type=list.get(list.size() - 1).get("buss_type").toString();
		
		//TODO 最后一个data参数需要改为 QueryListUtils.createPageDataMap(map,data,total);
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
		// 返回数据
		return reMap;
	}
}
