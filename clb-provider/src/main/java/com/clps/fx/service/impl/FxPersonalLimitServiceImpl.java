/**
 * 
 */
package com.clps.fx.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.service.BalanceAddService;
import com.clps.fx.service.FxPersonalLimitSevice;
import com.clps.fx.pojo.FxPersonalAcctOppPo;
import com.clps.fx.pojo.FxPersonalLimitPo;
import com.clps.fx.pojo.FxRatePo;
import com.clps.fx.service.RateInqService;

/**
 * TODO: Descriptions here
 * 
 * @author TODO: leo.wang
 *
 * 2017-05-07 下午4:30:42
 *
 * @version v1.0
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxPersonalLimitServiceImpl extends BaseService implements FxPersonalLimitSevice{
	
	// 错误码定义
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_BALNOTENOUGH = "0001";
    private static final String ERR_DPADDFILED = "0002";
    private static final String ERR_DPREDFILED = "1002";
    private static final String ERR_FXUPDFILED = "0003";
    private static final String ERR_LIMITUPDFILED = "0004";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    private static final String ERR_DUPLICATE = "1000";

    // The returning map
    Map<String, Object> returnMap = new HashMap<>();
    

	
	@Reference(version = "1.0.0")
	//dubbo的服务注解,内有版本号
	private RateInqService RateInq;
/*	@Autowired
	private FxRatePo fxrate;*/
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public FxPersonalLimitPo perLimitInq(FxPersonalLimitPo fxperlimit) throws Exception {
		
		// 记录日志
	    log.info("调用单条查询服务实现");
		return (FxPersonalLimitPo) dao.selectOneObject("personalLimit.limitInq", fxperlimit);
	}
	
	 @SuppressWarnings({ "unchecked", "unused" })
	 @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	 @Override
		public Map<String, Object> perLimitAdd(FxPersonalLimitPo fxperlimit) throws Exception {
			
			// 记录日志
		    log.info("调用单条添加服务实现");
		    fxperlimit.setCcy("USD");
		    fxperlimit.setBalance_limt("50000");
			 int re;
		        
		        try {
		            re = dao.insertOneByObject("personalLimit.limitAdd", fxperlimit);
		        } catch (DuplicateKeyException ex) {
		            returnMap = MapAndObjectUtils.copyPropertiesToMap(fxperlimit, returnMap);
		            returnMap.put("resp_code", ERR_DUPLICATE);
		            return returnMap;
		        }
		        if (re == 1) {
		        	returnMap = MapAndObjectUtils.copyPropertiesToMap(fxperlimit, returnMap);
		            returnMap.put("resp_code", ERR_NORMAL);
		            return returnMap;
		        } else {
		            // 没有插入,返回数据为空，返回值为ERR_UNHANDLED_EXCEPTION
		            returnMap = new HashMap<>();
		            returnMap.put("resp_code", ERR_UNHANDLED_EXCEPTION);
		            return returnMap;
				
		        }
		}
	 
	 
	 @SuppressWarnings({ "unchecked", "unused" })
	 @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	 @Override
		public Map<String, Object> perLimitUpd(FxPersonalLimitPo fxperlimit) throws Exception {
		 	FxRatePo fxrate = new FxRatePo();
			// 记录日志
		    log.info("调用单条添加服务实现");
		    FxPersonalLimitPo fxperlimitinq = new FxPersonalLimitPo();
		    fxperlimitinq = perLimitInq(fxperlimit);
		    Double tran_amt = Double.valueOf(fxperlimit.getTran_amt());
		    Double balance_limit = Double.valueOf(fxperlimitinq.getBalance_limt());
		    if("USD" == fxperlimit.getCcy()){
		    	balance_limit = balance_limit - tran_amt;
		    }else{
		    	fxrate.setFx_sell_ccy("USD");
		    	fxrate.setFx_buy_ccy(fxperlimit.getCcy());
		    	Map<String, Object> reteMap = RateInq.RateInq(fxrate);
		    	Double fx_acct_rate = Double.valueOf(reteMap.get("fx_acct_rate").toString());
		    	balance_limit = balance_limit - tran_amt * fx_acct_rate;
		    }
		    fxperlimit.setBalance_limt(balance_limit.toString());
		    
		    int a = dao.updateByObject("personalLimit.limitUpd", fxperlimit);
		    if(a != 1){
		    	returnMap.put("resp_code", ERR_LIMITUPDFILED);
            	return returnMap;
		    }else{
		    	returnMap.put("resp_code", ERR_NORMAL);
		    	returnMap = MapAndObjectUtils.copyPropertiesToMap(fxperlimit, returnMap);
    			return returnMap;
		    }
		}

}
