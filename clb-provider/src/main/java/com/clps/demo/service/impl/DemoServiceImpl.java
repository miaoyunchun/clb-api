package com.clps.demo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.common.pojo.PagePo;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.demo.pojo.DemoPo;
import com.clps.demo.service.DemoService;
import com.clps.fm.pojo.FmInfoPo;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */

@Service(version = "1.0.0") // 分布式服务注解和版本号
public class DemoServiceImpl extends BaseService implements DemoService {
	@Autowired
	private FmInfoPo fmpo;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> demoQueryOneService(DemoPo demo) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		log.info(fmpo.getBuss_type());
		return (Map<String, Object>) dao.selectOneObject("demoMapper.demoQueryOneService", demo);
	}

	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> demoQueryListService(Map<String,Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");
		
		// 搜索条件默认值处理
		if (map.get("sex") == null || map.get("sex").toString().equals("")) {
			map.put("sex", "1");// 默认搜索值
		}
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("demoMapper.demoQueryListService_count", map);
		
		//TODO 插入 
		map = QueryListUtils.changeInputData(map,total);
		
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("demoMapper.demoQueryListService", map);
		// 循环处理单条数据
		for (Map<String, Object> one : list) {
			one.put("new_data", "新数据");
			one.put("name", one.get("name") + "-改变的数据");
		}
		// 增加独立数据
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data_1", "独立数据1");
		data.put("data_2", "独立数据2");
		data.put("data_3", "独立数据3");
		// 处理返回
		
		//TODO 最后一个data参数需要改为 QueryListUtils.createPageDataMap(map,data,total);
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,data,total));
		// 返回数据
		return reMap;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int deleteOneService(DemoPo demo) throws Exception {
		// 记录日志
		log.info("调用删除服务实现");
//		map.put("update_time", DateTimeUtils.nowToSystem());
		demo.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.deleteByObject("demoMapper.deleteOneService", demo);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editOneService(DemoPo demo) throws Exception {
		log.info("调用修改服务实现");
//		map.put("update_time", DateTimeUtils.nowToSystem());
		demo.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("demoMapper.editOneService", demo);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public DemoPo insertService(DemoPo demo) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
//		map.put("create_time", DateTimeUtils.nowToSystem());
//		map.put("update_time", DateTimeUtils.nowToSystem());
		demo.setCreate_user(DateTimeUtils.nowToSystem());
		demo.setUpdate_user(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("demoMapper.insertService", demo);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return demo;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

}
