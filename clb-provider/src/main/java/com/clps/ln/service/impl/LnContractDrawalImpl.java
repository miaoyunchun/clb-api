package com.clps.ln.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.ln.pojo.LnDrdwPo;
import com.clps.ln.pojo.LnRepaymentPlanPo;
import com.clps.ln.service.LnContractDrawalService;
/**
 * * 合同放款
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnContractDrawalImpl extends BaseService implements LnContractDrawalService {
	/**
	 * 合同放款管理 
	 * @ClassName: LnContractDrawal
	 * @Description: 合同放款
	 * @author Cara.fan
	 * @date 2016年11月11日
	 */
	@Override
	public Map<String, Object> lnContractDrawal(Map<String, Object> map) throws Exception {
    	//lnAcctUpdMap 账户更新的数据
        Map<String, Object> lnAcctUpdMap=new HashMap<String, Object>();
        lnAcctUpdMap.put("update_time", DateTimeUtils.nowToSystem());
        lnAcctUpdMap.put("update_user", map.get("update_user"));
        lnAcctUpdMap.put("loan_acct_id", map.get("ws_loan_acct_id"));
        lnAcctUpdMap.put("loan_principal", map.get("ws_loan_principal"));
        lnAcctUpdMap.put("loan_remain_amt", map.get("ws_loan_remain_amt"));
        lnAcctUpdMap.put("loan_amt_toltal", map.get("ws_loan_amt_toltal"));
        int re1 = dao.updateByMap("lnContractDrawalMapper.updateLnAcct", lnAcctUpdMap);
     // 记录日志
        log.info("LN - 合同放款");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("lnContractDrawalMapper.insertLnContractDrawal", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	@Override
	public LnDrdwPo lnContractDrawalPo(LnDrdwPo lnDrpo) throws Exception {
		//lnAcctUpdMap 账户更新的数据
        Map<String, Object> lnAcctUpdMap=new HashMap<String, Object>();
        lnAcctUpdMap.put("update_time", DateTimeUtils.nowToSystem());
        lnAcctUpdMap.put("update_user", lnDrpo.getUpdate_user());
        lnAcctUpdMap.put("loan_acct_id", lnDrpo.getWs_loan_acct_id());
        lnAcctUpdMap.put("loan_principal", lnDrpo.getWs_loan_principal());
        lnAcctUpdMap.put("loan_remain_amt", lnDrpo.getWs_loan_remain_amt());
        lnAcctUpdMap.put("loan_amt_toltal", lnDrpo.getWs_loan_amt_toltal());
        int re1 = dao.updateByMap("lnContractDrawalMapper.updateLnAcct", lnAcctUpdMap);
        // 记录日志
        log.info("LN - 合同放款");
        lnDrpo.setCreate_time(DateTimeUtils.nowToSystem());
        lnDrpo.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("lnContractDrawalMapper.insertLnContractDrawalPo", lnDrpo);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return lnDrpo;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.clps.ln.service.LnContractDrawalService#insertRepayLoan(com.clps.ln.pojo.LnRepaymentPlanPo)
	 */
	@Override
	public int insertRepayLoan(LnRepaymentPlanPo repayPlan) throws Exception {
		// 记录日志
				log.info("LN - 还款计划添加");
				repayPlan.setCreate_time(DateTimeUtils.nowToSystem());
				repayPlan.setUpdate_time(DateTimeUtils.nowToSystem());
				int re = dao.insertOneByObject("lnContractDrawalMapper.insertLnRepayPlan", repayPlan);
				if (re == 1) {
					// 插入了一条数据,返回插入数据
					return re;
				} else {
					// 没有插入,返回空
					return 0;
				}
	}
}














