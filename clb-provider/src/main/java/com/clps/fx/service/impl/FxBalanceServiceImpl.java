package com.clps.fx.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.service.BalanceAddService;
import com.clps.dp.service.BalanceRedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.clps.fx.pojo.FxAcctPo;
import com.clps.fx.pojo.FxBalanceExPo;
import com.clps.fx.pojo.FxTradlimitPo;
import com.clps.fx.service.FxBalanceService;
import com.clps.fx.service.TradeLmtService;

import java.util.HashMap;
import java.util.Map;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxBalanceServiceImpl extends BaseService implements FxBalanceService{
	// 错误码定义
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_BALNOTENOUGH = "0001";
    private static final String ERR_DPADDFILED = "0002";
    private static final String ERR_DPREDFILED = "1002";
    private static final String ERR_FXUPDFILED = "0003";
    private static final String ERR_LIMITUPDFILED = "0004";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
/*	@Autowired
	private DpBalancePo dpbalanceadd;*/
	
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());

	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号
	private BalanceAddService nocardBalAddService;

	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号
	private BalanceRedService acctBalRedService;
	
	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号
	private TradeLmtService tradLimit;
	//定义返回map
	Map<String, Object> resultMap=new HashMap<String,Object>();
	


    //外汇账户存款
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		public Map<String, Object> acctFxBalAddService(FxBalanceExPo fxbalanceex) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
    		DpBalancePo dpbalanceadd = new DpBalancePo();
			log.info("外汇活期账户存款服务实现");
            dpbalanceadd.setAcct_nbr(fxbalanceex.getAcct_nbr());
            dpbalanceadd.setMoney_add(fxbalanceex.getMoney_add());
            dpbalanceadd.setUpdate_user(fxbalanceex.getUpdate_user());
            //leo update 2017/04/25
            //Map<String, Object> dpacctMap =nocardBalAddService.nocardBalAddService(map);
            Map<String, Object> dpacctMap =nocardBalAddService.nocardBalAddService(dpbalanceadd);
            if(dpacctMap.get("resp_code").equals("0000")){
            	int addresult=dao.updateByObject("dpFxBalanceMapper.FxBalanceAddMapper", fxbalanceex);
    			if(addresult>0)
    			{
    				// 更新外汇敞口限额
    				FxTradlimitPo fxtradlimit = new FxTradlimitPo();
    				FxTradlimitPo fxtradlimitinq = new FxTradlimitPo();
    				fxtradlimit.setOrg(fxbalanceex.getOrg());
    				fxtradlimit.setCcy(fxbalanceex.getCcy());
    				fxtradlimitinq = tradLimit.queryOrgCcy(fxtradlimit);
    				fxtradlimit.setBal_ccy(fxtradlimitinq.getBal_ccy());
    				fxtradlimit.setTrade_amt_sum(fxtradlimitinq.getTrade_amt_sum());
    				Double trade_amt_limit = Double.valueOf(fxtradlimitinq.getTrade_amt_limit()) + Double.valueOf(fxbalanceex.getMoney_add());
    				fxtradlimit.setTrade_amt_limit(trade_amt_limit.toString());
    				fxtradlimit.setUpdate_user(fxbalanceex.getUpdate_user());
    				fxtradlimit.setUpdate_time(DateTimeUtils.changeToDate());
    				int limit = tradLimit.editTradeLmt(fxtradlimit);
    				//int limit=dao.updateByObject("dpFxBalanceMapper.dpUpdateTradeAmtLimit1", fxbalanceex);
    				if(limit<=0)
    				{
    					resultMap.put("resp_code", ERR_LIMITUPDFILED);
                    	return resultMap;
    				}else{
    					resultMap.put("resp_code", ERR_NORMAL);
    	    			resultMap.put("acct",fxbalanceex.getAcct_nbr());
    	    			resultMap.put("acct_bal", dpacctMap.get("balance"));
    	    			return resultMap;
    					
    				}
    			}else{    				
    				resultMap.put("resp_code", ERR_FXUPDFILED);
                	return resultMap;
    			}
            }
            else{
            	resultMap.put("resp_code", ERR_DPADDFILED);
            	return resultMap;
            } 
			
		}
    
    
    //外汇账户取款
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		public Map<String, Object> acctFxBalRedService(FxBalanceExPo fxbalanceex) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("外汇活期账户取款服务实现");
			DpBalancePo dpbalancered = new DpBalancePo();
            dpbalancered.setAcct_nbr(fxbalanceex.getAcct_nbr());
            dpbalancered.setMoney(fxbalanceex.getMoney_red());
            dpbalancered.setUpdate_user(fxbalanceex.getUpdate_user());
			//map.put("acct_nbr",map.get("acct"));
			// leo update 2017/04/25
			//Map<String, Object> dpacctMap =acctBalRedService.acctBalRedService(map);
			Map<String, Object> dpacctMap =acctBalRedService.acctBalRedService(dpbalancered);
			if(dpacctMap.get("resp_code").equals("0000")){
				int redresult=dao.updateByObject("dpFxBalanceMapper.FxBalanceRedMapper", fxbalanceex);
				if(redresult>0)
    			{
    				// 更新外汇敞口限额
    				FxTradlimitPo fxtradlimit = new FxTradlimitPo();
    				FxTradlimitPo fxtradlimitinq = new FxTradlimitPo();
    				fxtradlimit.setOrg(fxbalanceex.getOrg());
    				fxtradlimit.setCcy(fxbalanceex.getCcy());
    				fxtradlimitinq = tradLimit.queryOrgCcy(fxtradlimit);
    				fxtradlimit.setBal_ccy(fxtradlimitinq.getBal_ccy());
    				fxtradlimit.setTrade_amt_sum(fxtradlimitinq.getTrade_amt_sum());
    				Double trade_amt_limit = Double.valueOf(fxtradlimitinq.getTrade_amt_limit()) - Double.valueOf(fxbalanceex.getMoney_red());
    				fxtradlimit.setTrade_amt_limit(trade_amt_limit.toString());
    				fxtradlimit.setUpdate_user(fxbalanceex.getUpdate_user());
    				fxtradlimit.setUpdate_time(DateTimeUtils.changeToDate());
    				int limit = tradLimit.editTradeLmt(fxtradlimit);
    				//int limit=dao.updateByObject("dpFxBalanceMapper.dpUpdateTradeAmtLimit1", fxbalanceex);
    				if(limit<=0)
    				{
    					resultMap.put("resp_code", ERR_LIMITUPDFILED);
                    	return resultMap;
    				}else{
    					resultMap.put("resp_code", ERR_NORMAL);
    	    			resultMap.put("acct",fxbalanceex.getAcct_nbr());
    	    			resultMap.put("acct_bal", dpacctMap.get("balance"));
    	    			return resultMap;
    					
    				}
    			}else{    				
    				resultMap.put("resp_code", ERR_FXUPDFILED);
                	return resultMap;
    			}
			}else if(dpacctMap.get(dpacctMap).equals("0005")){
				resultMap.put("resp_code", ERR_BALNOTENOUGH);
            	return resultMap;
			}else{
				resultMap.put("resp_code", ERR_DPREDFILED);
            	return resultMap;
			}
			
		}
			
}
