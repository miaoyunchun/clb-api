package com.clps.dp.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;
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
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.service.BalanceRedService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.dp.service.TransLogService;
import com.clps.gb.service.TxnJourGenService;

/**
 * 分布式服务接口实现
 *
 * @author joker
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号

public  class BalanceRedServiceImpl extends BaseService implements BalanceRedService {
	// 日志对象
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	@Reference(version = "1.0.1")
	// dubbo的服务注解,内有版本号
	private TxnJourGenService GBJour;
	@Reference(version = "1.0.1")
	// dubbo的服务注解,内有版本号
	private CardAcctInqService cardacctinfo;
	@Autowired
	private DpCardPo dpcard;
	@Autowired
	private DpAcctPo dpacct;
    Map<String, Object> resultMap = new HashMap<String,Object>();


	@SuppressWarnings("unchecked")
	public Map<String, Object> nocardBalQueryService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("jdpBalanceMapper.dpBalance2RedMapper", map);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> updateBalQueryService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("jdpBalanceMapper.dpBalance3RedMapper", map);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> havecardBalQueryService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用单条查询服务实现");
		Map<String,Object> map2= (Map<String, Object>) dao.selectOneMap("JdpBalanceMapper.dpBalanceRedMapper", map);
		if (map2.get("card_pin")!=map.get("PIN")){
			map2.put("flag","false");
			return map2;
		}
		Map<String,Object> resultMap=nocardBalQueryService(map2);
		resultMap.put("card_number", map.get("CARD-NO"));
		return resultMap;
	}

	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> insertBalQueryService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用单条查询服务实现");
		Map<String,Object> map2= (Map<String, Object>) dao.selectOneMap("JdpBalanceMapper.dpBalance2RedMapper", map);
		map.put("trans_acct", map2.get("acct_nbr"));
		map.put("trans_code", "1001");
		map.put("trans_desc", "DPOSIT WITH CARD");
		map.put("trans_time", DateTimeUtils.nowToSystem());
		map.put("trans_date", DateTimeUtils.changeToDate());
		map.put("amount_before_trans", map2.get("acct_last_bal"));
		map.put("amount_after_trans", map2.get("acct_curr_bal"));
		int re = dao.insertOneByObject("demoMapper.insertService", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	//账户取款
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> acctBalRedService(DpBalancePo dpbalancered) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用账号取款服务");

		//账户信息查询，查询出对应账户状态和账户余额
		dpacct.setAcct_nbr(dpbalancered.getAcct_nbr());
		dpacct=(DpAcctPo) cardacctinfo.acctInfoInq(dpacct);		
		//Map<String,Object> map3 = (Map<String, Object>) dao.selectOneMap("dpCardAcctInqMapper.acctMsgInqMapper", map);
		if(!dpacct.getAcct_status().equals("0")){
			resultMap.put("resp_code", "0002");
			return resultMap;
		}

		//更新账户余额
		Double acct_last_bal = Double.valueOf(dpacct.getAcct_curr_bal());
		Double acct_curr_bal = acct_last_bal - Double.parseDouble(dpbalancered.getMoney());
		if(acct_curr_bal < 0){
			resultMap.put("resp_code", "0005");
			return resultMap;
		}
		dpacct.setAcct_curr_bal(acct_curr_bal.toString());
		dpacct.setAcct_last_bal(acct_last_bal.toString());
		dpacct.setUpdate_user(dpbalancered.getUpdate_user());
		dpacct.setUpdate_time(DateTimeUtils.changeToDate());
		int a =  dao.updateByObject("acctOpenMapper.dpBalanceUpdMapper", dpacct);
		if (a != 1){
			resultMap.put("resp_code", "0003");
			return resultMap;
		}
		//调用GB服务生成交易流水号
		Map<String, Object> gbmap = new HashMap<String,Object>();
		gbmap.put("create_user", "test");
		gbmap.put("update_user", "test");
		gbmap.put("initial", "");
		gbmap.put("length", "11");
		gbmap=GBJour.txnJourGen(gbmap);
		//System.out.println(">>>>>>>>>>>>>>>gbmap:"+gbmap);
		//写入交易历史表
				Map<String, Object> translogmap=new HashMap<String, Object>();
				 translogmap.put("trans_id",gbmap.get("jour_nbr").toString());
			        translogmap.put("trans_sequence","00000000"+gbmap.get("jour_nbr"));
			        translogmap.put("trans_card", dpbalancered.getAcct_nbr());
			        translogmap.put("trans_acct", dpacct.getAcct_nbr());
			        translogmap.put("trans_code", "1000");
			        translogmap.put("trans_desc", "DPOSIT WITH CARD");
			        translogmap.put("trans_time", DateTimeUtils.changeToTime());
			        translogmap.put("trans_date", DateTimeUtils.changeToDate());
			        translogmap.put("amount_before_trans", dpacct.getAcct_last_bal());
			        translogmap.put("amount_in_trans", dpbalancered.getMoney());
			        translogmap.put("amount_after_trans", dpacct.getAcct_curr_bal());
			        translogmap.put("remark", "DPOSIT WITH CARD");
			        translogmap.put("create_time", DateTimeUtils.nowToSystem());
			        translogmap.put("update_time", DateTimeUtils.nowToSystem());
			        translogmap.put("create_user", dpbalancered.getUpdate_user());
			        translogmap.put("update_user", dpbalancered.getUpdate_user());
					dao.insertOneByMap("dpBalanceAddMapper.addTransLogService", translogmap);
		//System.out.println(">>>>>>>>>>>>>>>>>>>>translogmap:"+translogmap);
		//Map<String,Object> testMap=logInq.transLogQuery(translogmap);
		//System.out.println(">>>>>>>>>>>>>>testMap:"+testMap);
		//返回map
					resultMap.put("resp_code", "0000");
					resultMap.put("acct_nbr",dpbalancered.getAcct_nbr());
					resultMap.put("money",dpbalancered.getMoney());
					resultMap.put("balance",dpacct.getAcct_curr_bal());			
					return resultMap;
	}


	//有卡取款
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public Map<String, Object> havecardBalRedService(DpBalancePo dpbalancered) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用卡号存款服务");

		//卡片信息查询，查出对应账户号和卡片状态以及核对卡片密码	
		dpcard.setCard_number(dpbalancered.getCard_number());
		dpcard=(DpCardPo) cardacctinfo.cardNoPinInq(dpcard);
		//dpcard = (DpCardPo) dao.selectOneObject("DpCardOpenMapper.cardInfoInqMappper", dpbalancered);
		if(!dpcard.getCard_status().equals("0")){
            resultMap.put("resp_code", "0001");
			return resultMap;
		}			
		if(!dpcard.getCard_pin().equals(dpbalancered.getPin())){
            resultMap.put("resp_code", "1009");
			return resultMap;
		}


		//账户信息查询，查询出对应账户状态和账户余额
		
		dpacct.setAcct_nbr(dpcard.getAssociate_acct_id());
		dpacct=(DpAcctPo) cardacctinfo.acctInfoInq(dpacct);
		//dpacct = (DpAcctPo) dao.selectOneObject("acctOpenMapper.acctInfoInq", dpacct);
		if(!dpacct.getAcct_status().equals("0")){
			resultMap.put("resp_code", "0002");
			return resultMap;
		}

		//更新账户余额
		//更新账户余额
		Double acct_last_bal = Double.valueOf(dpacct.getAcct_curr_bal());
		Double acct_curr_bal = acct_last_bal - Double.parseDouble(dpbalancered.getMoney());
		dpacct.setAcct_curr_bal(acct_curr_bal.toString());
		dpacct.setAcct_last_bal(acct_last_bal.toString());
		dpacct.setUpdate_user(dpbalancered.getUpdate_user());
		dpacct.setUpdate_time(DateTimeUtils.changeToDate());
		int a =  dao.updateByObject("acctOpenMapper.dpBalanceUpdMapper", dpacct);
		if (a != 1){
			resultMap.put("resp_code", "0003");
			return resultMap;
		}

		//调用GB服务生成交易流水号
		Map<String, Object> gbmap = new HashMap<String,Object>();
		gbmap.put("create_user", dpbalancered.getUpdate_user());
		gbmap.put("update_user", dpbalancered.getUpdate_user());
		gbmap.put("initial", "");
		gbmap.put("length", "11");
		gbmap=GBJour.txnJourGen(gbmap);
		//System.out.println(">>>>>>>>>>>>>>>gbmap:"+gbmap);
		//写入交易历史表
		Map<String, Object> translogmap=new HashMap<String, Object>();
		 translogmap.put("trans_id",gbmap.get("jour_nbr").toString());
	        translogmap.put("trans_sequence","00000000"+gbmap.get("jour_nbr"));
	        translogmap.put("trans_card", dpbalancered.getCard_number());
	        translogmap.put("trans_acct", dpacct.getAcct_nbr());
	        translogmap.put("trans_code", "1000");
	        translogmap.put("trans_desc", "DPOSIT WITH CARD");
	        translogmap.put("trans_time", DateTimeUtils.changeToTime());
	        translogmap.put("trans_date", DateTimeUtils.changeToDate());
	        translogmap.put("amount_before_trans", dpacct.getAcct_last_bal());
	        translogmap.put("amount_in_trans", dpbalancered.getMoney());
	        translogmap.put("amount_after_trans", dpacct.getAcct_curr_bal());
	        translogmap.put("remark", "DPOSIT WITH CARD");
	        translogmap.put("create_time", DateTimeUtils.nowToSystem());
	        translogmap.put("update_time", DateTimeUtils.nowToSystem());
	        translogmap.put("create_user", dpbalancered.getUpdate_user());
	        translogmap.put("update_user", dpbalancered.getUpdate_user());
			dao.insertOneByMap("dpBalanceAddMapper.addTransLogService", translogmap);
		//Map<String,Object> testMap=logInq.transLogQuery(translogmap);
		//System.out.println(">>>>>>>>>>>>>>testMap:"+testMap);
		resultMap.put("resp_code", "0000");
		resultMap.put("card_number",dpbalancered.getCard_number());
		resultMap.put("pin",dpbalancered.getPin());
		resultMap.put("money",dpbalancered.getMoney());
		resultMap.put("balance",dpacct.getAcct_curr_bal());
		return resultMap;
	}

}    



