package com.clps.pb.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.pb.service.PbResvdpstLstService;

/**
 * 预约定存列表查询
 * @author zejing.wang
 * @Time：2017年1月9日 下午1:56:41
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbResvdpstLstServiceImpl extends BaseService implements PbResvdpstLstService{

	Map<String,Object> reMap = null;
	@Transactional(readOnly = true) //只读事务
	@Override
	public Map<String, Object> queryListService(Map<String, Object> map) throws Exception {
		//记录日志
		log.info("调用预约定存列表查询服务实现");
		//处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time","create_time");
		//查询总条数
		Long total = (Long) dao.selectOneObject("resvdpstLstMapper.queryListService_count", map);
		//TODO 插入
		map = QueryListUtils.changeInputData(map,total);
		//查询总数据
		List<Map<String,Object>> list = dao.selectListMap("resvdpstLstMapper.queryListService", map);
		if(total != 0){
			//循环处理单条数据
			for(Map<String,Object> one : list){
				one.put("new_data", "新数据");
				one.put("name", one.get("name") + "-改变的数据");
			}
			//增加独立数据
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("screen_counter", map.get("screen_counter"));
			data.put("max_key", map.get("max_key"));
			data.put("min_key", map.get("min_key"));
			data.put("map_direction", map.get("map_direction"));
			data.put("count", map.get("count"));
			//处理返回
			Map<String,Object> reMap = QueryListUtils.changeReturnDate(list, total, data);
			return reMap;
		}
		return reMap;
	}
}
