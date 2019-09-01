package com.clps.dp.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.dp.service.dpTransfer;
import com.clps.dp.service.BalanceRedService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.service.BalanceAddService;
import com.clps.gb.service.TxnJourGenService;


/**
 * 分布式服务接口实现
 *
 * @author alice
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class DpTransferServiceImpl extends BaseService implements dpTransfer {
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号
    private BalanceRedService acctBalRedService;
    
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号  
    private BalanceAddService BalanceAddService;
    
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号  
	private CardAcctInqService cardacctinfo;
/*	@Autowired
	private DpCardPo dpcard;
	@Autowired
	private DpAcctPo dpacct;
	@Autowired
	private DpBalancePo dpbalancered;
	@Autowired
	private DpBalancePo dpbalanceadd;*/
	
    Map<String, Object> resultMap = new HashMap<String,Object>();
	
	
	/**
	 * 账户直接转账
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
    public Map<String, Object> dpAcctTransfer(DpBalancePo dpbalancetrans) throws Exception {
		DpBalancePo dpbalanceadd = new DpBalancePo();
		DpBalancePo dpbalancered = new DpBalancePo();
        Map<String, Object> acctRedMap = new HashMap<String,Object>();
        Map<String, Object> acctAddMap = new HashMap<String,Object>();
        //转出账户信息
        
        dpbalancered.setAcct_nbr(dpbalancetrans.getAcct_no_red());
        dpbalancered.setMoney(dpbalancetrans.getMoney_red());
        dpbalancered.setUpdate_user(dpbalancetrans.getUpdate_user());
        dpbalancered.setUpdate_time(DateTimeUtils.changeToDate());
        //转入账户信息
        dpbalanceadd.setAcct_nbr(dpbalancetrans.getAcct_no_add());
        dpbalanceadd.setMoney_add(dpbalancetrans.getMoney_add());
        dpbalanceadd.setUpdate_user(dpbalancetrans.getUpdate_user());
        dpbalanceadd.setUpdate_time(DateTimeUtils.changeToDate());
        //调用账户扣款服务
        acctRedMap=acctBalRedService.acctBalRedService(dpbalancered);
        //调用账户入账服务
        acctAddMap=BalanceAddService.nocardBalAddService(dpbalanceadd);
/*        //查询转出账户余额
        Map<String,Object> acctRed1Map= (Map<String, Object>) dao.selectOneMap("dpBalanceAddMapper.dpAcctInqMapper", acctRedMap);
        //查询转入账户余额
        Map<String,Object> acctAdd1Map= (Map<String, Object>) dao.selectOneMap("dpBalanceAddMapper.dpAcctInqMapper", acctAddMap);*/
        resultMap.put("money_add_bal",acctAddMap.get("balance"));
        resultMap.put("money_red_bal",acctRedMap.get("balance"));
/*
        //查询转出账户信息
        Map<String,Object> acctMap= (Map<String, Object>) dao.selectOneMap("dpBalanceAddMapper.dpAcctInqMapper", acctRedMap);
        if(!acctMap.get("acct_status").equals("0")){
            acctMap.put("RESP-CODE", "0001");
            return resultsMap;
        }
        else {
            if (Double.parseDouble(acctMap.get("acct_curr_bal").toString()) - Double.parseDouble(acctRedMap.get("money_red").toString()) < 0.0 ){
                acctMap.put("RESP-CODE", "0002");
                return resultsMap;
            }
        }*/




        return resultMap;

    }
    
    
    
    
    /**
     * 卡片间转账
     */
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
    public Map<String, Object> dpCardTransfer(DpBalancePo dpbalancetrans) throws Exception {
		DpCardPo dpcard = new DpCardPo();
		DpAcctPo dpacct = new DpAcctPo();
        Map<String, Object> acctMap = new HashMap<String,Object>();
        Map<String, Object> cardMap = new HashMap<String,Object>();
        //转出卡号信息查询
		dpcard.setCard_number(dpbalancetrans.getCard_no1());
		dpcard=(DpCardPo) cardacctinfo.cardNoPinInq(dpcard);
		//dpcard = (DpCardPo) dao.selectOneObject("DpCardOpenMapper.cardInfoInqMappper", dpbalancered);
		if(!dpcard.getCard_status().equals("0")){
            resultMap.put("resp_code", "0001");
			return resultMap;
		}			
		if(!dpcard.getCard_pin().equals(dpbalancetrans.getPsw())){
            resultMap.put("resp_code", "1009");
			return resultMap;
		}
		//账户转账map值获取		
		dpbalancetrans.setAcct_no_red(dpcard.getAssociate_acct_id());
		dpbalancetrans.setMoney_red(dpbalancetrans.getMoney_tfs());
//		acctMap.put("acct_no_red",dpcard.getAssociate_acct_id());
//        acctMap.put("money_red",dpbalancetrans.getMoney_tfs());
        
        //转入卡号账户信息查询
		dpcard.setCard_number(dpbalancetrans.getCard_no2());
		dpcard=(DpCardPo) cardacctinfo.cardNoPinInq(dpcard);
		if(!dpcard.getCard_status().equals("0")){
            resultMap.put("resp_code", "0001");
			return resultMap;
		}			
		dpbalancetrans.setAcct_no_add(dpcard.getAssociate_acct_id());
		dpbalancetrans.setMoney_add(dpbalancetrans.getMoney_tfs());
       /* acctMap.put("acct_no_add",dpcard.getAssociate_acct_id());
        acctMap.put("money_add",dpbalancetrans.getMoney_tfs());*/
        //调用账户转账
        acctMap=dpAcctTransfer(dpbalancetrans);
        
        resultMap.put("bal",acctMap.get("money_red_bal"));


        return resultMap;

    }


}
