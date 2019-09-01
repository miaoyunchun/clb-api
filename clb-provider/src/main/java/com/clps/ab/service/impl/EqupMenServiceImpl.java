package com.clps.ab.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.service.BalanceInqService;
import com.clps.gb.service.TxnJourGenService;
import com.clps.ab.service.EqupMenService;

/**
 * 分布式服务接口实现
 * 
 * @author WEI
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.1") // 分布式服务注解和版本号

public class EqupMenServiceImpl extends BaseService implements EqupMenService{
	
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());
		@Reference(version = "1.0.0")
		// dubbo的服务注解,内有版本号
		private TxnJourGenService GBJour;
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		@Override
		public Map<String, Object> insertService(Map<String, Object> map) throws Exception {
			// 记录日志
			log.info("调用插入服务实现");
			Map<String, Object> gbmap = new HashMap<String,Object>();
	        gbmap.put("create_user", map.get("create_user"));
	        gbmap.put("update_user", map.get("update_user"));
	        gbmap.put("initial", map.get("mcc_code"));
	        gbmap.put("length", "15");
	        gbmap=GBJour.txnJourGen(gbmap);
			map.put("create_time", DateTimeUtils.nowToSystem());
			map.put("update_time", DateTimeUtils.nowToSystem());
			map.put("pos_no",gbmap.get("jour_nbr").toString());
			int re = dao.insertOneByObject("EqupMenMapper.insertService", map);
			if (re == 1) {
				map.clear();
				map.put("pos_no",gbmap.get("jour_nbr").toString());
				// 插入了一条数据,返回插入数据
				return map;
			} else {
				// 没有插入,返回空
				return null;
			}
		}
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		@Override
		public int editOneService(Map<String, Object> map) throws Exception {
			log.info("调用更新服务实现");
			map.put("update_time", DateTimeUtils.nowToSystem());
			return dao.updateByMap("EqupMenMapper.editOneService", map);
		}
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = true)//只读事务
		@Override
		public Map<String, Object> equpQueryOneService(Map<String, Object> map) throws Exception {
			// 记录日志
			log.info("调用单条查询服务实现");
			return (Map<String, Object>) dao.selectOneMap("EqupMenMapper.equpQueryOneService", map);
		}
		//列表查询
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = true)//只读事务
		@Override
		public Map<String, Object> equpQueryListService(Map<String, Object> map) throws Exception {
			// 记录日志
			log.info("调用多条查询服务实现");
			// 搜索条件默认值处理
			if (map.get("sex") == null || map.get("sex").toString().equals("")) {
				map.put("sex", "1");// 默认搜索值
			}
			// 处理日期范围
			QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
			// 查询总条数
			Long total = (Long) dao.selectOneObject("EqupMenMapper.equpQueryListService_count", map);
			map = QueryListUtils.changeInputData(map,total);
			// 查询总数据
			List<Map<String, Object>> list = dao.selectListMap("EqupMenMapper.equpQueryListService", map);
			// 处理返回
			Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
			reMap.put("merch_no", map.get("merch_no"));
			reMap.put("mcc_code", map.get("mcc_code"));
			reMap.put("equip_tpye", map.get("equip_tpye"));
			reMap.put("belong_org", map.get("belong_org"));
			reMap.put("access_net", map.get("access_net"));
			// 返回数据
			return reMap;
		}
}
