package com.clps.dp.service.impl;

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
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.cp.pojo.CpProdparm;
import com.clps.cp.pojo.CpSctacctPo;
import com.clps.cp.service.AcctNbrGenerateService;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.service.AcctOpenService;
import com.clps.dp.service.BalanceAddService;
/**
 * 分布式服务接口实现
 * 
 * @author 
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class AcctOpenServiceImpl extends BaseService implements AcctOpenService{
	
	 // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_DUPLICATE = "0001";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    // The returning map
    Map<String, Object> returnMap = new HashMap<>();
    
	private static final String ZERO = "0";
	@Reference(version = "1.0.0")
	private AcctNbrGenerateService acctNbrService;
	@Autowired
	private CpSctacctPo cpacct;
	
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String,Object> AcctOpenAddService(DpAcctPo dpacct) throws Exception {
		
		//调用cp服务获取acctNbr等数据
		cpacct.setProduct_id(dpacct.getProd_id());
		cpacct=acctNbrService.acctNbrGenerate(cpacct);		
		dpacct.setAcct_nbr(cpacct.getAccount_next());
		if(dpacct.getAcct_curr_bal().equals("")){
			dpacct.setAcct_curr_bal(ZERO);
		}
		//dpacct.setAcct_curr_bal(ZERO);
		dpacct.setRelation_no(dpacct.getCust_nbr());
		dpacct.setLast_press_date(DateTimeUtils.changeToDate());
		dpacct.setAcct_status(ZERO);
		dpacct.setLast_status(ZERO);
		dpacct.setLast_status_date(DateTimeUtils.changeToDate());
		dpacct.setAcct_last_bal(ZERO);
		dpacct.setProd_spec_date(DateTimeUtils.changeToDate());
		dpacct.setProd_end_date("9999-01-01");
		dpacct.setAcct_last_main_date(DateTimeUtils.changeToDate());
		dpacct.setCreate_time(DateTimeUtils.changeToDate());
		dpacct.setUpdate_time(DateTimeUtils.changeToDate());
		dpacct.setPer_tenor_amt(ZERO);
		dpacct.setLast_by(dpacct.getCreate_user());
		dpacct.setAdd_by(dpacct.getCreate_user());
		dpacct.setAggr_bal(ZERO);
		dpacct.setInterest(ZERO);
		dpacct.setTenor(ZERO);
		dpacct.setRate(ZERO);
		
		//添加数据到dpacct
		int re;
		try {
            re = dao.insertOneByObject("acctOpenMapper.insertDpAcct", dpacct);
        } catch (DuplicateKeyException ex) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(dpacct, returnMap);
            returnMap.put("resp_code", ERR_DUPLICATE);
            return returnMap;
        }
        if (re == 1) {
        	returnMap = MapAndObjectUtils.copyPropertiesToMap(dpacct, returnMap);
            returnMap.put("resp_code", ERR_NORMAL);
            return returnMap;
        } else {
            // 没有插入,返回数据为空，返回值为ERR_UNHANDLED_EXCEPTION
            returnMap = new HashMap<>();
            returnMap.put("resp_code", ERR_UNHANDLED_EXCEPTION);
            return returnMap;
		
        }
   
	}
}
