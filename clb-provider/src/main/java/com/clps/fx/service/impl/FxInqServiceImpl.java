package com.clps.fx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.pojo.FxAcctExPo;
import com.clps.fx.service.FxInqService;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxInqServiceImpl extends BaseService implements FxInqService{
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());

		@SuppressWarnings("unchecked")
		@Transactional(readOnly = true)//只读事务
		@Override
		public Map<String, Object> acctExchListService(Map<String, Object> map) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("外汇账户交易历史列表查询服务实现");
			// 处理日期范围
			if(map.get("start_date") == null || map.get("start_date").equals("")){
				map.put("start_date", "0001-01-01");
			}
			if(map.get("end_date") == null || map.get("end_date").equals("")){
				map.put("end_date", "9999-01-01");
			}
			map.put("tran_date", map.get("start_date").toString()+" ~ "+map.get("end_date").toString());
			// 处理日期范围
			QueryListUtils.changeTimeSearch(map, "update_time", "create_time","tran_date");
			Long total = (Long) dao.selectOneObject("dpFxExchMapper.dpExchCount", map);
			List<Map<String, Object>> list = dao.selectListMap("dpFxExchMapper.dpFxInqMapper", map);
			// 处理返回
			Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, null);
			// 返回数据
			return reMap;
		}
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = true)//只读事务
		@Override
		public Map<String, Object> acctExchInqService(FxAcctExPo fxacctex) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用外汇账户交易历史详细查询服务实现");
			return (Map<String, Object>) dao.selectOneObject("dpFxExchMapper.dpExchInq", fxacctex);
		}
			
}
