package com.clps.dp.service.impl;
import java.util.ArrayList;
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
import com.clps.core.sys.util.QueryListUtils;
import com.clps.cp.service.ProductParamService;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.service.BalanceAddService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.dp.service.TransLogService;
import com.clps.gb.service.TxnJourGenService;
import com.clps.fm.pojo.FmTxnAcctPo;
import com.clps.fm.service.FmTransAccountingService;

/**
 * 分布式服务接口实现
 * 
 * @author alice
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public  class BalanceAddServiceImpl extends BaseService implements BalanceAddService {
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());
		@Reference(version = "1.0.0")
		// dubbo的服务注解,内有版本号
		private TxnJourGenService GBJour;
		
		@Reference(version = "1.0.0")
		 //dubbo的服务注解,内有版本号
		private FmTransAccountingService TransAccounting;
		@Autowired
		private DpCardPo dpcard;
		@Autowired
		private DpAcctPo dpacct;
		Map<String, Object> resultMap = new HashMap<String,Object>();
		@Autowired
		private CardAcctInqService cardacctinfo;
		//有卡存款
		@SuppressWarnings({ })
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		@Override
		public Map<String, Object> havecardBalAddService(DpBalancePo dpbalanceadd) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用卡号存款服务");
			
			//卡片信息查询，查出对应账户号和卡片状态
			dpcard.setCard_number(dpbalanceadd.getCard_number());
			dpcard=(DpCardPo) cardacctinfo.cardNoPinInq(dpcard);
			//dpcard = (DpCardPo) dao.selectOneObject("DpCardOpenMapper.cardInfoInqMappper", dpbalanceadd);
			if(!dpcard.getCard_status().equals("0")){
				resultMap.put("resp_code", "0001");
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
			Double acct_last_bal = Double.valueOf(dpacct.getAcct_curr_bal());
			Double acct_curr_bal = Double.parseDouble(dpbalanceadd.getMoney_add())+ acct_last_bal;
			dpacct.setAcct_curr_bal(acct_curr_bal.toString());
			dpacct.setAcct_last_bal(acct_last_bal.toString());
			dpacct.setUpdate_user(dpbalanceadd.getUpdate_user());
			dpacct.setUpdate_time(DateTimeUtils.changeToDate());
			int a =  dao.updateByObject("acctOpenMapper.dpBalanceUpdMapper", dpacct);
			if (a != 1){
				resultMap.put("resp_code", "0003");
				return resultMap;
			}

			/*//查询账户信息
			Map<String,Object> acctMap= (Map<String, Object>) dao.selectOneMap("dpBalanceAddMapper.dpAcctInqMapper", map2);
			acctMap.put("resp_code", "0000");*/

			//调用交易记账服务
			/*FmTxnAcctPo fmtxnact = new FmTxnAcctPo();
			fmtxnact.setTran_id("1000");
			fmtxnact.setCond_seq("0002");
			fmtxnact.setTran_amt(dpbalanceadd.getMoney_add());
			fmtxnact.setAcct_prod(dpacct.getProd_id());
			fmtxnact=TransAccounting.TransAccounting(fmtxnact);*/
			
			//原map调用记账服务实现过程
			Map<String, Object> fmMap = new HashMap<String, Object>();
			fmMap.put("tran_id","1000");
			fmMap.put("cond_seq","0002");
			fmMap.put("tran_desp","DEMAND DEPOSIT-CASH DEPOSIT");
			//定义接口中List
			List<Map<String, Object>> fmMapList = new ArrayList<Map<String, Object>>();
			//List第一条数据
			Map<String, Object> fmList1Map = new HashMap<String, Object>();
			fmList1Map.put("sub_cond","01");
			fmList1Map.put("acct_prod", dpacct.getProd_id());
			fmList1Map.put("acct_org",dpacct.getAssociate_org_id());
			fmList1Map.put("amt_pionter","01");
			fmList1Map.put("tran_amt",dpbalanceadd.getMoney_add());
			fmMapList.add(fmList1Map);
			//List第二条数据
			Map<String, Object> fmList2Map = new HashMap<String, Object>();
			fmList2Map.put("sub_cond","02");
			fmList2Map.put("acct_prod", dpacct.getProd_id());
			fmList2Map.put("acct_org",dpacct.getAssociate_org_id());
			fmList2Map.put("amt_pionter","01");
			fmList2Map.put("tran_amt",dpbalanceadd.getMoney_add());
			fmMapList.add(fmList2Map);
			//List3-10数据
			for(int i=1;i<9;i++){
				Map<String, Object> fmList3Map = new HashMap<String, Object>();
				fmList3Map.put("sub_cond","");
				fmList3Map.put("acct_prod","");
				fmList3Map.put("acct_org","");
				fmList3Map.put("amt_pionter","");
				fmList3Map.put("tran_amt","");
				fmMapList.add(fmList3Map);
			}
            fmMap.put("list",fmMapList);
			fmMap.put("txn_jour","");
			fmMap.put("doc_type","");
			fmMap.put("doc_nbr","");
			fmMap.put("txn_descrip","");
			fmMap.put("opr_nbr","");
			fmMap.put("rchr_nbr","");
			fmMap.put("txn_instn","");
			fmMap.put("val_date","");
			fmMap.put("txn_fxr","");
			fmMap.put("desrp_cod","");
			fmMap.put("txn_seq_nbr","");
			fmMap.put("txn_unit","");
			fmMap.put("txn_type","");
			fmMap=TransAccounting.TransAccounting(fmMap);



			//调用GB服务生成交易流水号
			Map<String, Object> gbmap = new HashMap<String,Object>();
	        gbmap.put("create_user", dpbalanceadd.getUpdate_user());
	        gbmap.put("update_user", dpbalanceadd.getUpdate_user());
	        gbmap.put("initial", "");
	        gbmap.put("length", "11");
	        gbmap=GBJour.txnJourGen(gbmap);
	        
			//写入交易历史表
	        Map<String, Object> translogmap=new HashMap<String, Object>();
	        translogmap.put("trans_id",gbmap.get("jour_nbr").toString());
	        translogmap.put("trans_sequence","00000000"+gbmap.get("jour_nbr"));
	        translogmap.put("trans_card", dpbalanceadd.getCard_number());
	        translogmap.put("trans_acct", dpacct.getAcct_nbr());
	        translogmap.put("trans_code", "1000");
	        translogmap.put("trans_desc", "DPOSIT WITH CARD");
	        translogmap.put("trans_time", DateTimeUtils.changeToTime());
	        translogmap.put("trans_date", DateTimeUtils.changeToDate());
	        translogmap.put("amount_before_trans", dpacct.getAcct_last_bal());
	        translogmap.put("amount_in_trans", dpbalanceadd.getMoney_add());
	        translogmap.put("amount_after_trans", dpacct.getAcct_curr_bal());
	        translogmap.put("remark", "DPOSIT WITH CARD");
	        translogmap.put("create_time", DateTimeUtils.nowToSystem());
	        translogmap.put("update_time", DateTimeUtils.nowToSystem());
	        translogmap.put("create_user", dpbalanceadd.getUpdate_user());
	        translogmap.put("update_user", dpbalanceadd.getUpdate_user());
			dao.insertOneByMap("dpBalanceAddMapper.addTransLogService", translogmap);
			//System.out.println(">>>>>>>>>>>>>>>>>>>>translogmap:"+translogmap);
			//Map<String,Object> testMap=logInq.transLogQuery(translogmap);
			//System.out.println(">>>>>>>>>>>>>>testMap:"+testMap);
			resultMap.put("card_number", dpcard.getCard_number());
			resultMap.put("money_add", dpbalanceadd.getMoney_add());
			resultMap.put("card_balance", dpacct.getAcct_curr_bal());
			resultMap.put("resp_code", "0000");
			return resultMap;
		}
		
		
		@SuppressWarnings({ "unchecked", "unused" })
		@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		@Override
		public Map<String, Object> nocardBalAddService(DpBalancePo dpbalanceadd) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用账户存款服务");
			//DpAcctPo dpacct = new DpAcctPo();
			//账户信息查询，查询出对应账户状态和账户余额
			//账户信息查询，查询出对应账户状态和账户余额
			dpacct.setAcct_nbr(dpbalanceadd.getAcct_nbr());
			dpacct= cardacctinfo.acctInfoInq(dpacct);		
			//Map<String,Object> map3 = (Map<String, Object>) dao.selectOneMap("dpCardAcctInqMapper.acctMsgInqMapper", map);
			if(!dpacct.getAcct_status().equals("0")){
				resultMap.put("resp_code", "0002");
				return resultMap;
			}


			//更新账户余额
			Double acct_last_bal = Double.valueOf(dpacct.getAcct_curr_bal());
			Double acct_curr_bal = acct_last_bal + Double.parseDouble(dpbalanceadd.getMoney_add());
			dpacct.setAcct_curr_bal(acct_curr_bal.toString());
			dpacct.setAcct_last_bal(acct_last_bal.toString());
			dpacct.setUpdate_user(dpbalanceadd.getUpdate_user());
			dpacct.setUpdate_time(DateTimeUtils.changeToDate());
			int a =  dao.updateByObject("acctOpenMapper.dpBalanceUpdMapper", dpacct);
			if (a != 1){
				resultMap.put("resp_code", "0003");
				return resultMap;
			}

			//查询账户信息
			/*Map<String,Object> acctMap= (Map<String, Object>) dao.selectOneMap("dpBalanceAddMapper.dpAcctInqMapper", map);
			acctMap.put("resp_code", "0000");*/


			//调用GB服务生成交易流水号
			Map<String, Object> gbmap = new HashMap<String,Object>();
			gbmap.put("create_user", "test");
			gbmap.put("update_user", "test");
			gbmap.put("initial", "");
			gbmap.put("length", "11");
			gbmap=GBJour.txnJourGen(gbmap);
			//写入交易历史表
			Map<String, Object> translogmap=new HashMap<String, Object>();
			 translogmap.put("trans_id",gbmap.get("jour_nbr").toString());
		        translogmap.put("trans_sequence","00000000"+gbmap.get("jour_nbr"));
		        translogmap.put("trans_card", dpbalanceadd.getAcct_nbr());
		        translogmap.put("trans_acct", dpacct.getAcct_nbr());
		        translogmap.put("trans_code", "1000");
		        translogmap.put("trans_desc", "DPOSIT WITH CARD");
		        translogmap.put("trans_time", DateTimeUtils.changeToTime());
		        translogmap.put("trans_date", DateTimeUtils.changeToDate());
		        translogmap.put("amount_before_trans", dpacct.getAcct_last_bal());
		        translogmap.put("amount_in_trans", dpbalanceadd.getMoney_add());
		        translogmap.put("amount_after_trans", dpacct.getAcct_curr_bal());
		        translogmap.put("remark", "DPOSIT WITH CARD");
		        translogmap.put("create_time", DateTimeUtils.nowToSystem());
		        translogmap.put("update_time", DateTimeUtils.nowToSystem());
		        translogmap.put("create_user", dpbalanceadd.getUpdate_user());
		        translogmap.put("update_user", dpbalanceadd.getUpdate_user());
				dao.insertOneByMap("dpBalanceAddMapper.addTransLogService", translogmap);
				resultMap.put("resp_code", "0000");
				resultMap.put("acct_nbr",dpbalanceadd.getAcct_nbr());
				resultMap.put("money_add",dpbalanceadd.getMoney());
				resultMap.put("balance",dpacct.getAcct_curr_bal());	
			return resultMap;
		}
		
}    
