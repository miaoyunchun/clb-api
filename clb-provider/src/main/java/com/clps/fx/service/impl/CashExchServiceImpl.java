package com.clps.fx.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.pojo.FxCashExPo;
import com.clps.fx.service.CashExchService;

/**
 * 分布式服务接口实现
 * 
 * @author snow
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CashExchServiceImpl extends BaseService implements CashExchService{

	
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> QueryCashExchListService(Map<String, Object> map) throws Exception {
	
		// 记录日志
		log.info("调用多条查询服务实现");
        
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time","start_date","end_date");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("CashExchMapper.QueryCashExchListService_count", map);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("CashExchMapper.QueryCashExchListService", map);
		
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, null);
		// 返回数据
		return reMap;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> QueryCashExchOneService(FxCashExPo fxcashex) throws Exception {
		
		// 记录日志
	    log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneObject("CashExchMapper.QueryCashExchOneService", fxcashex);
	}

}
