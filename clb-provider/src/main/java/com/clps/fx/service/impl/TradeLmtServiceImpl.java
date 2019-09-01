package com.clps.fx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.pojo.FxTradlimitPo;
import com.clps.fx.pojo.FxUnWindPo;
import com.clps.fx.service.TradeLmtService;

/**
 * 分布式服务接口实现
 * 
 * @author wenjie.li
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class TradeLmtServiceImpl extends BaseService implements TradeLmtService {
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public FxTradlimitPo queryOrgCcy(FxTradlimitPo fxtradlimit) throws Exception {
		// 记录日志
		log.info("调用外汇敞口限额查询服务实现");
		return (FxTradlimitPo) dao.selectOneObject("tradelmtMapper.queryOrgCcy", fxtradlimit);
	}

	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> queryTradeLmt(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用外汇敞口限额列表查询服务实现");
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("tradelmtMapper.queryTradeLmt_count", map);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("tradelmtMapper.queryTradeLmt", map);
		// 循环处理单条数据
		//for (Map<String, Object> one : list) {
		//	one.put("new_data", "新数据");
		//	one.put("org", one.get("org") + "-改变的数据");
		//}
		// 增加独立数据
		//Map<String, Object> data = new HashMap<String, Object>();
		//data.put("data_1", "独立数据1");
		//data.put("data_2", "独立数据2");
		//data.put("data_3", "独立数据3");
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total,null);
		// 返回数据
		return reMap;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editTradeLmt(FxTradlimitPo fxtradlimit) throws Exception {
		log.info("外汇敞口限额维护测试实现");
		fxtradlimit.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("tradelmtMapper.editTradeLmt", fxtradlimit);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public FxTradlimitPo insertTradeLmt(FxTradlimitPo fxtradlimit) throws Exception {
		// 记录日志
		log.info("新建外汇敞口限额服务实现");
		int re = dao.insertOneByObject("tradelmtMapper.insertTradeLmt", fxtradlimit);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return fxtradlimit;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int sumTradeLmt(FxUnWindPo fxunwind) throws Exception {
		log.info("外汇敞口限额维护测试实现");
		FxTradlimitPo fxtradlimit = new FxTradlimitPo();
		fxtradlimit.setOrg(fxunwind.getOrg());
		fxtradlimit.setCcy(fxunwind.getCcy());
		fxtradlimit= queryOrgCcy(fxtradlimit);
		Double trade_amt_sum =Double.valueOf(fxunwind.getTrade_amt());
		Double limit_amt_sum = trade_amt_sum + Double.valueOf(fxtradlimit.getTrade_amt_sum());
		fxtradlimit.setTrade_amt_sum(limit_amt_sum.toString());
		fxtradlimit.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("tradelmtMapper.editTradeLmt", fxtradlimit);
	}

}
