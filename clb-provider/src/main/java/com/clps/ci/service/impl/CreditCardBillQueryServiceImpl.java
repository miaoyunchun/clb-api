package com.clps.ci.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ci.service.CreditCardBillQueryService;

/**
 * 分布式服务接口实现
 * 
 * @author Wangwl
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CreditCardBillQueryServiceImpl extends BaseService implements CreditCardBillQueryService {
	// 未出账单查询实现
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true) // 只读事务
	@Override
	public Map<String, Object> postTranInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");

		map.put("otran_org", map.get("org"));
		map.put("otran_acct_num", map.get("acct_numb"));
		map.put("otran_seq", map.get("seq"));
		// 查询总条数
		Long total = (Long) dao.selectOneObject("CreditCardBillQueryMapper.postTranInqService_count", map);
		// 查询总数据
		Map<String, Object> map1 = (Map<String, Object>) dao.selectOneMap("CreditCardBillQueryMapper.postTranInqService", map);
		//将数据改成输出接口要求的名称，以list方式输出
		Map<String, Object> one = new HashMap<String, Object>();
		one.put("otran_seq", map1.get("otran_seq"));
		one.put("otran_effdate", map1.get("otran_eff_date"));
		one.put("otran_postdate", map1.get("otran_post_date"));
		one.put("otran_amount", map1.get("otran_amount"));
		one.put("otran_tc", map1.get("otran_tc"));
		one.put("otran_desc", map1.get("otran_desc"));
	
		List<Map<String, Object>> list= new ArrayList<Map<String, Object>>();
		// 增加数据
		list.add(one);		

		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, null);
		// 返回数据
		return reMap;
	}

	// 账单查询实现
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true) // 只读事务
	@Override
	public Map<String, Object> billStmtInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		//处理输入的数据
		map.put("stmt_org", map.get("stmt_org"));
		map.put("stmt_acct_num", map.get("stmt_acct_num"));
		map.put("stmt_date", map.get("stmt_date"));
		Object s1 =001;
		Object s2 =002;
		map.put("stmt_data_type",s1);
		map.put("stmt_seq", s1);
		Map<String, Object> map1 = (Map<String, Object>) dao.selectOneMap("CreditCardBillQueryMapper.billStmtInq1Service", map);
		map.put("stmt_data_type",s2);
		Map<String, Object> map2 = (Map<String, Object>) dao.selectOneMap("CreditCardBillQueryMapper.billStmtInq2Service", map);
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("card_num", map1.get("stmt_acct_num"));
		// 得出billing_cycle的值
		String billDate = (map1.get("stmt_date")).toString();
		// 将String型转换成int进行计算
		String bYear = billDate.substring(0, 4);
		int bYear1 = Integer.parseInt(bYear);
		// 将String型转换成int进行计算
		String bMonth = billDate.substring(4, 6);
		int bMonth1 = Integer.parseInt(bMonth);
		String cycle = (map1.get("stmt_da_billing_cycle")).toString();
		int cycle1 = Integer.parseInt(cycle);
		String billDate1 = null;
		switch (bMonth1) {
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
			billDate1 = bYear + "/0" + (bMonth1 - 1) + "/" + (cycle1 + 1) + "-" + bYear + "/" + bMonth + "/" + cycle;
			break;
		case 1:
			billDate1 = (bYear1 - 1) + "/" + 12 + "/" + (cycle1 + 1) + "-" + bYear + "/" + bMonth + "/" + cycle;
			break;
		case 11:
		case 12:
			billDate1 = bYear + "/" + (bMonth1 - 1) + "/" + (cycle1 + 1) + "-" + bYear + "/" + bMonth + "/" + cycle;
			break;
		case 10:
			billDate1 = bYear + "/0" + (bMonth1 - 1) + "/" + (cycle1 + 1) + "-" + bYear + "/" + bMonth + "/" + cycle;
			break;

		}
		resultMap.put("billing_cycle", billDate1);
		// stmt_da_due_date中8位日期变成10位
		String date1 = (map1.get("stmt_da_due_date")).toString();
		String year = date1.substring(0, 4);
		String month = date1.substring(4, 6);
		String day = date1.substring(6, 8);
		String date2 = year + "/" + month + "/" + day;
		// 新的日期格式put到Due_date中
		resultMap.put("due_date", date2);
		//新的输出字段
		resultMap.put("due", map1.get("stmt_da_due"));
		resultMap.put("crlim", map1.get("stmt_da_crlim"));
		resultMap.put("cash_otb", map1.get("stmt_da_cash_otb"));
		resultMap.put("bal", map1.get("stmt_da_bal"));
		resultMap.put("beg_bal", map2.get("stmt_dp_beg_bal"));
		resultMap.put("ctd_db", map2.get("stmt_dp_ctd_db"));
		resultMap.put("ctd_cr", map2.get("stmt_dp_ctd_cr"));
		return resultMap;
	}

	// 已出账单查询实现
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true) // 只读事务
	@Override
	public Map<String, Object> billTranInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");
		// 处理日期范围
		// QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		//处理输入的数据
		map.put("stmt_org", map.get("org"));
		map.put("stmt_acct_num", map.get("acct_num"));
		map.put("stmt_date", map.get("date"));
		map.put("stmt_data_type", map.get("date_type"));
		map.put("stmt_seq", map.get("seq_t"));
		// 查询总条数
		Long total = (Long) dao.selectOneObject("CreditCardBillQueryMapper.billTranInqService_count", map);
		// 查询总数据
		Map<String, Object> map1 = (Map<String, Object>) dao.selectOneMap("CreditCardBillQueryMapper.billTranInqService", map);
		//将数据改成输出接口要求的名称，以list方式输出
		Map<String, Object> one = new HashMap<String, Object>();
		one.put("dt_eff_date", map1.get("stmt_dt_eff_date"));
		one.put("dt_post_date", map1.get("stmt_dt_post_date"));
		one.put("dt_amount", map1.get("stmt_dt_amount"));
		one.put("dt_tc", map1.get("stmt_dt_tc"));
		one.put("dt_desc", map1.get("stmt_dt_desc"));
		one.put("seq", map1.get("stmt_seq"));
		//List<Map<String, Object>> list = dao.selectListMap("CreditCardBillQueryMapper.billTranInqService", map);
		List<Map<String, Object>> list2= new ArrayList<Map<String, Object>>();
		// 增加数据
		list2.add(one);		
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list2, total, null);
		// 返回数据
		return reMap;
	}

}
