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
import com.clps.core.common.service.BaseService;
import com.clps.dp.service.TransLogService;

/**
 * 分布式服务接口实现
 * 
 * @author leo
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class TransLogInqServiceImpl extends BaseService implements TransLogService{

	
	// 日志对象
	//private Logger log = LoggerFactory.getLogger(getClass().getName());
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> transLogQuery(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用交易历史查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("dpTransLog.dpTransLogInq", map);
	}
	
	
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> transLogList(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用交易历史查询服务实现");
		// 搜索条件默认值处理
		//if (map.get("sex") == null || map.get("sex").toString().equals("")) {
		//	map.put("sex", "1");// 默认搜索值
		//}
		if(map.get("begintime") == null || map.get("begintime").equals("")){
			map.put("begintime", "0001-01-01");
		}
		if(map.get("endtime") == null || map.get("endtime").equals("")){
			map.put("endtime", "9999-01-01");
		}
		map.put("trans_date", map.get("begintime").toString()+" ~ "+map.get("endtime").toString());
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time","trans_date");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("dpTransLog.dpTransLogCount", map);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("dpTransLog.dpTransLogList", map);
		// 循环处理单条数据
		//for (Map<String, Object> one : list) {
		//	one.put("new_data", "新数据");
		//	one.put("name", one.get("name") + "-改变的数据");
		//}
		// 增加独立数据
//		Map<String, Object> data = new HashMap<String, Object>();
//		data.put("data_1", "独立数据1");
//		data.put("data_2", "独立数据2");
//		data.put("data_3", "独立数据3");
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, null);
		// 返回数据
		return reMap;
	}




}
