/** 
 * Project Name:clb-provider 
 * File Name:LnBadLoanServiceImpl.java 
 * Package Name:com.clps.ln.service.impl 
 * Date:2016年11月3日下午3:42:57 
 * Copyright (c) 2016, chenzhou1025@126.com All Rights Reserved. 
 * 
*/  
  
package com.clps.ln.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.ln.pojo.LnContractPo;
import com.clps.ln.pojo.LnWoroffPo;
import com.clps.ln.service.LnBadLoanService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/** 
 * ClassName:LnBadLoanServiceImpl <br/> 
 * Function: TODO ADD FUNCTION. <br/> 
 * Reason:   TODO ADD REASON. <br/> 
 * Date:     2016年11月3日 下午3:42:57 <br/> 
 * @author   Jamie.Chen 
 * @version   
 * @since    JDK 1.8
 * @see       
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnBadLoanServiceImpl extends BaseService implements LnBadLoanService{

	/**
	 * 呆账认定
	 * 更改合同形态(lncntrct_cntrct_shape)
	 */
	@Override
    public Map<String,Object> lnBadLoanIde(LnContractPo lnContractPo) throws Exception {
		// 记录日志
		log.info("LN - 呆账认定");
		int re = dao.updateByObject("lnBadLoanMapper.lnBadLoanIde", lnContractPo);
		Map<String, Object> returnMap = new HashMap<>();
		if (re == 1) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnContractPo, returnMap);
            returnMap.put("isSuccessful", "true");
			return returnMap;
		} else {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnContractPo, returnMap);
            returnMap.put("isSuccessful", "false");
			return returnMap;
		}
	}

	/**
	 * 呆账核销
	 * 更改合同状态(lncntrct_cntrct_status)
	 * @param lnContractPo
	 */
	@Override
	public Map<String, Object> lnBadLoanOff(LnContractPo lnContractPo) throws Exception {
		// 记录日志
		log.info("LN - 呆账核销");
		int re = dao.updateByObject("lnBadLoanMapper.lnBadLoanOff", lnContractPo);
		Map<String, Object> resultMap = new HashMap<>();
		if (re == 1) {
			// 更改了一条数据,返回更改数据
            resultMap = MapAndObjectUtils.copyPropertiesToMap(lnContractPo, resultMap);
            resultMap.put("isSuccessful", "true");
			return resultMap;
		} else {
            resultMap = MapAndObjectUtils.copyPropertiesToMap(lnContractPo, resultMap);
            resultMap.put("isSuccessful", "false");
            return resultMap;
		}
	}

	/**
	 * 呆账核销
	 * 把输入接口的信息insert到贷款核销表(ln_woroff)中
	 */
	@Override
    public Map<String,Object> lnWorOff(LnWoroffPo lnWoroffPo) throws Exception {
		int re = dao.insertOneByObject("lnBadLoanMapper.lnWorOff", lnWoroffPo);
        Map<String, Object> returnMap = new HashMap<>();
		if (re == 1) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnWoroffPo, returnMap);
            returnMap.put("isSuccessful", "true");
            return returnMap;
		} else {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnWoroffPo, returnMap);
            returnMap.put("isSuccessful", "false");
            return returnMap;
		}
	}

	/**
	 * 呆账查询
	 * 查询ln_woroff表
	 */
	@SuppressWarnings("unchecked")
	@Override
    public LnWoroffPo lnBadLoanInq(LnWoroffPo lnWoroffPo) throws Exception {
		log.info("LN - 呆账查询");
        return (LnWoroffPo) dao.selectOneObject("lnBadLoanMapper.lnBadLoanInq", lnWoroffPo);
	}

	/**
	 * 呆账回收 
	 * 更新ln_woroff表
	 */
	@Override
    public Map<String, Object> updLnWorOff(LnWoroffPo lnWoroffPo) throws Exception {
		// 记录日志
		log.info("LN - 呆账回收 - 更新贷款账户表");
		int re = dao.updateByObject("lnBadLoanMapper.updLnWorOff", lnWoroffPo);
        Map<String, Object> returnMap = new HashMap<>();
		if (re == 1) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnWoroffPo, returnMap);
            returnMap.put("isSuccessful", "true");
            return returnMap;
		} else {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnWoroffPo, returnMap);
            returnMap.put("isSuccessful", "false");
            return returnMap;
		}
	}

	/**
	 * 呆账回收完成 余额还清
	 */
	@Override
    public Map<String, Object> lnBadLoanClean(LnContractPo lnContractPo) throws Exception {
		// 记录日志
		log.info("LN - 呆账回收完成 余额还清");
		int re = dao.updateByObject("lnBadLoanMapper.lnBadLoanClean", lnContractPo);
        Map<String, Object> returnMap = new HashMap<>();
		if (re == 1) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnContractPo, returnMap);
            returnMap.put("isSuccessful", "true");
            return returnMap;
		} else {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(lnContractPo, returnMap);
            returnMap.put("isSuccessful", "false");
            return returnMap;
		}
	}
}
  