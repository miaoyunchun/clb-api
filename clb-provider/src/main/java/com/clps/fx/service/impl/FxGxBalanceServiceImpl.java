package com.clps.fx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.common.util.JsonResponseUtils;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.service.BalanceAddService;
import com.clps.dp.service.BalanceInqService;
import com.clps.dp.service.BalanceRedService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.fx.pojo.FxPersonalAcctOppPo;
import com.clps.fx.pojo.FxPersonalBalPo;
import com.clps.fx.pojo.FxRatePo;
import com.clps.fx.service.FxGxBalanceService;
import com.clps.fx.service.FxPersonalAcct;
import com.clps.fx.service.RateInqService;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxGxBalanceServiceImpl extends BaseService implements FxGxBalanceService{
	
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_PIN = "1000";
    private static final String ERR_FXACCT = "1001";
    private static final String ERR_BALNOTENOUGH = "0001";
    private static final String ERR_DPADDFILED = "0002";
    private static final String ERR_DPREDFILED = "1002";
    private static final String ERR_FXUPDFILED = "0003";
    private static final String ERR_LIMITUPDFILED = "0004";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    
	//定义返回map
    Map<String, Object> resultsMap = new HashMap<String,Object>();//实现类返回
    
	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号  卡片信息查询
	private BalanceInqService cardBalQueryService;
	@Autowired
	private DpCardPo dpcard;
	
	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号  外汇个人账户对应表查询
	private FxPersonalAcct perAcctOppInq;
	@Autowired
	private FxPersonalAcctOppPo fxperacctopp;
	
	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号  外汇个人账户对应表查询
	private BalanceAddService nocardBalAddService;
	
	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号  外汇个人账户对应表查询
	private BalanceRedService accrbalred;
	@Autowired
	private DpBalancePo dpbalanceadd;
	@Autowired
	private DpBalancePo dpbalancered;
	
	
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());
		/**
		 * 个人外汇活期存款服务实现  VBS.FX.GXBALANC.ADD
		 * @param fxperbal
		 */
		@SuppressWarnings({ "unchecked", "unused" })
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		@Override
		public Map<String, Object> acctFxBalAddService(FxPersonalBalPo fxperbal) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("个人外汇活期存款服务实现");
			
			//检查密码是否正确
			dpcard.setCard_number(fxperbal.getCard_nbr());
			dpcard.setCard_pin(fxperbal.getCard_psw());
			Map<String,Object> cardMap = cardBalQueryService.cardBalQueryService(dpcard);
			if(! cardMap.get("flag").equals("true")){
		    	resultsMap.put("resp_code", ERR_PIN);
	        	return resultsMap;
			}
			
			//查询个人账户对应表
			fxperacctopp.setCard_nbr(fxperbal.getCard_nbr());
			fxperacctopp.setCcy(fxperbal.getAcct_ccy());
			fxperacctopp = perAcctOppInq.perAcctOppInq(fxperacctopp);
			if(null == fxperacctopp){
		    	resultsMap.put("resp_code", ERR_FXACCT);
	        	return resultsMap;
			}
			
			//调用账户存款
			dpbalanceadd.setAcct_nbr(fxperacctopp.getAcct_nbr());
			dpbalanceadd.setMoney_add(fxperbal.getBalance_add());
			dpbalanceadd.setUpdate_user(fxperbal.getUpdate_user());
			Map<String, Object> balanceadd = nocardBalAddService.nocardBalAddService(dpbalanceadd);
			
			if(null == balanceadd){
				resultsMap.put("resp_code", ERR_DPADDFILED);
	        	return resultsMap;
			}else{
				resultsMap.put("resp_code", ERR_NORMAL);
				resultsMap.put("card_nbr", fxperbal.getCard_nbr());
				resultsMap.put("acct_ccy", fxperbal.getAcct_ccy());
				resultsMap.put("acct_bal", balanceadd.get("balance"));
	        	return resultsMap;
			}
			
		}
		
		/**
		 * 个人外汇活期取款服务实现  VBS.FX.GXBALANC.RED
		 * @param fxperbal
		 */
		@SuppressWarnings({ "unchecked", "unused" })
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		@Override
		public Map<String, Object> acctFxBalRedService(FxPersonalBalPo fxperbal) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("个人外汇活期取款服务实现");
			//检查密码是否正确
			dpcard.setCard_number(fxperbal.getCard_nbr());
			dpcard.setCard_pin(fxperbal.getCard_psw());
			Map<String,Object> cardMap = cardBalQueryService.cardBalQueryService(dpcard);
			if(! cardMap.get("flag").equals("true")){
		    	resultsMap.put("resp_code", ERR_PIN);
	        	return resultsMap;
			}
			
			//查询个人账户对应表
			fxperacctopp.setCard_nbr(fxperbal.getCard_nbr());
			fxperacctopp.setCcy(fxperbal.getAcct_ccy());
			fxperacctopp = perAcctOppInq.perAcctOppInq(fxperacctopp);
			if(null == fxperacctopp){
		    	resultsMap.put("resp_code", ERR_FXACCT);
	        	return resultsMap;
			}
			//调用账户存款
			dpbalancered.setAcct_nbr(fxperacctopp.getAcct_nbr());
			dpbalancered.setMoney(fxperbal.getBalance_add());
			dpbalancered.setUpdate_user(fxperbal.getUpdate_user());
			Map<String, Object> dpred = accrbalred.acctBalRedService(dpbalancered);
			
			if(null == dpred){
				resultsMap.put("resp_code", ERR_DPREDFILED);
	        	return resultsMap;
			}else{
				resultsMap.put("resp_code", ERR_NORMAL);
				resultsMap.put("card_nbr", fxperbal.getCard_nbr());
				resultsMap.put("acct_ccy", fxperbal.getAcct_ccy());
				resultsMap.put("acct_bal", dpred.get("balance"));
	        	return resultsMap;
			}

		}
			
}
