package com.clps.ln.service.impl;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.pojo.LnAcctPo;
import com.clps.ln.service.LnAcctApplyService;

@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnAcctApplyServiceImpl extends BaseService implements LnAcctApplyService{

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> QueryAcctService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用贷款账户查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("lnacctapplyMapper.QueryAcctService", map);

	}
	@Override
	public LnAcctPo QueryAcctServiceByPo(LnAcctPo lnacctpo) throws Exception {
		// 记录日志
		log.info("调用贷款账户查询服务实现");
		return (LnAcctPo) dao.selectOneObject("lnacctapplyMapper.QueryAcctServiceByPo", lnacctpo);
	}

	/**
	 * 呆账回收 - 更新贷款账户表
	 */
	@Override
	public String updLnAcctAmt(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("LN - 呆账回收 - 更新贷款账户表");
		int re = dao.updateByMap("lnacctapplyMapper.updLnAcctAmt", map);
		if (re == 1) {
			// 更改了一条数据,返回更改数据
			return "success";
		} else {
			// 没有更改,返回空
			return "error";
		}
	}

	@Override
	public Map<String, Object> QueryAcctListService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("LN - 贷款客户号查其下所有账号和产品号查询");

		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("lnacctapplyMapper.queryLnAcctListByCustomerNo_count", map);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("lnacctapplyMapper.queryLnAcctListByCustomerNo", map);

		// 返回map
		Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, null);
		if (total != 0) {
			resultMap.put("customer_number", list.get(list.size() - 1).get("customer_number"));
		} else {
			resultMap.put("customer_number", "");
		}
		return resultMap;
	}
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public LnAcctPo QueryAcctServiceByCustomerNumber(LnAcctPo lnacctpo) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
			log.info("调用贷款账户查询服务实现");
			return (LnAcctPo) dao.selectOneObject("lnacctapplyMapper.QueryAcctServiceByCustmoerNumber", lnacctpo);
	}




}
