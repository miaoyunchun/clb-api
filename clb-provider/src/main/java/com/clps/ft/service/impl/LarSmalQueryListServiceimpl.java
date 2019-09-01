package com.clps.ft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ft.service.LarSmaQueryListService;

/** 
 * 分布式服务接口实现
 * @Description:TODO
 * @author andy.wang
 * @date 2016年12月26日 下午2:25:22
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LarSmalQueryListServiceimpl extends BaseService implements LarSmaQueryListService {
	@Transactional(readOnly = true) // 只读事务
	@Override
	public Map<String, Object> LarSmalQueryListService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");

		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("larsmaMapper.larsmaQueryListService_count", map);
		if (total > 0) {
			// //TODO 插入
			// 查询总数据
			List<Map<String, Object>> list = dao.selectListMap("larsmaMapper.larsmaQueryListService", map);
			// 循环处理单条数据
			for (Map<String, Object> one : list) {
				one.put("txn_jour", one.get("txn_jour"));
				one.put("deduc_bank_number", one.get("deduc_bank_number"));
				one.put("recv_bank_number", one.get("recv_bank_number"));
				one.put("trans_amt", one.get("trans_amt"));
			}
			// 增加独立数据
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("mulpage_ind", map.get("mulpage_ind"));
			data.put("st_recacct", map.get("st_recacct"));
			data.put("txn_code", map.get("txn_code"));
			data.put("txn_date", map.get("txn_date"));
			// 处理返回

			/*
			 * //TODO 最后一个data参数需要改为
			 * QueryListUtils.createPageDataMap(map,data,total);
			 *  Map<String,Object> reMap = QueryListUtils.changeReturnDate(list, total,QueryListUtils.createPageDataMap(map,data,total));
			 */
			// 处理返回
			Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, data);
			// 返回数据
			return reMap;
		}else{
			map.put("0002", "ERR_NOTHING_FOUND");
			return map;
		}
	}
}
