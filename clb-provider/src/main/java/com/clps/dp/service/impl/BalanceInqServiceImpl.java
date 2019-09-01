package com.clps.dp.service.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.clps.gb.service.TxnJourGenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.cp.pojo.CpProdparm;
import com.clps.cp.service.ProductParamService;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.service.BalanceInqService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.rm.pojo.RmcustPo;
import com.clps.rm.service.PersonalAccountService;



/**
 * 分布式服务接口实现
 * 
 * @author leo
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class BalanceInqServiceImpl extends BaseService implements BalanceInqService{

	// 日志对象
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	
	@Reference(version = "1.0.0")
	// dubbo的服务注解,内有版本号
	private PersonalAccountService personalAccountInq;
	
	@Reference(version = "1.0.0")
	// dubbo的服务注解,内有版本号
	CardAcctInqService cardacctInfoInq;
	
	@Reference(version = "1.0.0")
	// dubbo的服务注解,内有版本号
	ProductParamService queryProductParam;
	
	/*@Autowired 2017-05-12 代码修改
	private DpAcctPo dpacct;
	@Autowired
	private CpProdparm cpProd;*/

	

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> acctBalQueryService(DpCardPo dpcard) throws Exception {
		//定义pojo 2017-05-12 代码修改
		DpAcctPo dpacct =  new DpAcctPo();
		CpProdparm cpProd = new CpProdparm();
		
		// TODO Auto-generated method stub
		// 记录日志
		log.info("DP余额查询服务实现>>>>>>>>>>>>>>>>>>>>>>>>>>");
		Map<String, Object> resultMap = new HashMap<String,Object>();
		//查询卡号信息
		log.info("DP卡片信息查询服务实现>>>>>>>>>>>>>>>>>>>>>>>>>>start");
		dpcard = cardacctInfoInq.cardNoPinInq(dpcard);
		//dpcard = (DpCardPo) dao.selectOneObject("DpCardOpenMapper.cardInfoInqMappper", dpcard);
		log.info("DP卡片信息查询服务实现>>>>>>>>>>>>>>>>>>>>>>>>>>end");
		//查询账户信息
		log.info("DP账户信息查询服务实现>>>>>>>>>>>>>>>>>>>>>>>>>>start");
		dpacct.setAcct_nbr(dpcard.getAssociate_acct_id());		
		dpacct = cardacctInfoInq.acctInfoInq(dpacct);
		log.info("DP账户信息查询服务实现>>>>>>>>>>>>>>>>>>>>>>>>>>end");
		//dpacct= (DpAcctPo) dao.selectOneObject("acctOpenMapper.acctInfoInq", dpacct);
		//查询产品名称
		log.info("CP产品信息查询服务实现>>>>>>>>>>>>>>>>>>>>>>>>>>start");
		cpProd.setProduct_id(dpacct.getProd_id());
		cpProd = queryProductParam.queryProductParam(cpProd);
		log.info("CP产品信息查询服务实现>>>>>>>>>>>>>>>>>>>>>>>>>>end");
		//查询客户姓名
		resultMap.put("card_number",dpcard.getCard_number());
		resultMap.put("current_balance",dpacct.getAcct_curr_bal());
		resultMap.put("product_id",dpacct.getProd_id());
		resultMap.put("curr_tenor",dpacct.getTenor());
		resultMap.put("per_amt",dpacct.getPer_tenor_amt());
		resultMap.put("curr_interest",dpacct.getInterest());
		resultMap.put("product_name",dpacct.getProd_id());
		resultMap.put("customer_type",dpacct.getCust_nbr().substring(0,1));
		resultMap.put("id_type",dpacct.getCust_nbr().substring(0, 1));
		resultMap.put("id_no",dpacct.getCust_nbr().substring(1, 19));
		//查询客户姓名
		RmcustPo rmcustPo = new RmcustPo();
		rmcustPo.setCust_number(dpacct.getCust_nbr());
		//RmcustPo rmcustPo2 = new RmcustPo();
		rmcustPo = personalAccountInq.personalAccountInq(rmcustPo);
		resultMap.put("name",rmcustPo.getCust_name());

		//if(acctInfoMap.get("cust_nbr").toString().substring(0,1).equals("c")){

		//}





		return resultMap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> acct2BalQueryService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("dpBalanceInqMapper.dpBalance3InqMapper", map);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> cardBalQueryService(DpCardPo dpcard) throws Exception {
		//2017-05-12 代码修改
		DpAcctPo dpacct =  new DpAcctPo();
		CpProdparm cpProd = new CpProdparm();
		// TODO Auto-generated method stub
		// 记录日志
		Map<String, Object> resultMap2 = new HashMap<String,Object>();
		log.info("调用卡号查询服务实现");
		DpCardPo dpcardreturn =  (DpCardPo) dao.selectOneObject("DpCardOpenMapper.cardInfoInqMappper", dpcard);
		if (!dpcardreturn.getCard_pin().equals(dpcard.getCard_pin())){
			resultMap2.put("flag","false");
			return resultMap2;
		}
		dpacct.setAcct_nbr(dpcardreturn.getAssociate_acct_id());
		dpacct = (DpAcctPo) dao.selectOneObject("acctOpenMapper.acctInfoInq", dpacct);
		//Map<String,Object> resultMap=acct2BalQueryService(map2);
		//resultMap.replace(acct_curr_bal, balance);
		resultMap2.put("card_number", dpcardreturn.getCard_number());
		resultMap2.put("card_pin", dpcardreturn.getCard_pin());
		resultMap2.put("balance", dpacct.getAcct_curr_bal());
		resultMap2.put("acct_nbr", dpacct.getAcct_nbr());
		resultMap2.put("flag","true");
		return resultMap2;
		
	}
}
