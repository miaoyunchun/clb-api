package com.clps.cp.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.BeanParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.common.vo.JsonRequestVo;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.StringUtils;
import com.clps.cp.pojo.CpProdparm;
import com.clps.cp.pojo.CpSctacctPo;
import com.clps.cp.pojo.CpSctintPo;
import com.clps.cp.service.AcctNbrGenerateService;
import com.clps.cp.service.ProductParamService;
@Component
@Service(version = "1.0.0")
public class AcctNbrGenerateServiceImpl extends BaseService implements AcctNbrGenerateService{
	@Autowired
	private CpProdparm cpProd;
	@Autowired
	private CpSctintPo cpInst;

	@Reference(version = "1.0.0")
	// dubbo的服务注解,内有版本号
	private ProductParamService proService;
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public int editAccountParam(CpSctacctPo cpvo) throws Exception {
		log.info("调用修改服务实现");
		cpvo.setUpdate_time(DateTimeUtils.nowToSystem());
		cpvo.setAccount_current(cpvo.getAccount_next());
		String account_next_change=updateAccountInfo(cpvo);
		cpvo.setAccount_next(account_next_change);
		cpvo.setLast_maint_date(DateTimeUtils.changeToDate());
		return dao.updateByObject("acctNbrGenerateMapper.editAcctParam", cpvo);
	}
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public CpSctacctPo acctNbrGenerate(CpSctacctPo cpvo) throws Exception {
		//根据product_id查询产品参数表数据
		CpProdparm cpProd =new CpProdparm();
		cpProd.setProduct_id(cpvo.getProduct_id());
		cpProd = queryProductParam(cpProd);
		//根据Account_control查询sct账户表数据
		cpvo.setAssociate_org_id(cpProd.getAssociate_org_id());		
		cpvo.setSct_id(cpProd.getAccount_control());
		cpvo=queryAcct(cpvo);
		//根据Interest_control查询sct利率表数据
		cpInst.setSct_id(cpProd.getInterest_control());
		cpInst=QueryCPSCTInterestRateService(cpInst);
//		String account_current=cpvo.getAccount_current();
		cpvo.setUpdate_time(DateTimeUtils.nowToSystem());
		editAccountParam(cpvo);
		return cpvo;
	}
	/**
	 *  修改account_next 的值
	 *  account_next（11：8）+1 //account_next字段的第11位起，长度为8（11-18）位加1
   	 *	account_next（19：1）+1 // account_next字段的第19位起，长度为1（19）位加1
	 * 2016年9月23日 david
	 */
	private String updateAccountInfo(CpSctacctPo cpvo){
		//获取字段的值
		String account_next=cpvo.getAccount_next().toString();
		//account_next第19位加一，不进位
		int nineteen=(Integer.parseInt(account_next.substring(18, 19))+1)%10;
		String s2=String.valueOf(nineteen);
		//account_next前18位加一，进位
		BigDecimal beforeNineteen=new BigDecimal(account_next.substring(0,18)).add(new BigDecimal(1));
		StringBuffer sb=new StringBuffer();
		//字段前的值补零到18位
		String s1=StringUtils.completeString(String.valueOf(beforeNineteen),18,'0',true);
		sb.append(s1).append(s2);
		return sb.toString();
	}
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public CpProdparm queryProductParam(CpProdparm cpProd) throws Exception {
		// 记录日志
				log.info("调用单条查询服务实现");
				return (CpProdparm) dao.selectOneObject("productParamMapper.queryProductParam", cpProd);
	}
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	private CpSctacctPo queryAcct(CpSctacctPo cpvo) throws Exception {
		// 记录日志
		log.info("调用查询服务");
		return (CpSctacctPo) dao.selectOneObject("cp_sct_acctMapper.queryAcct", cpvo);
	}
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	private CpSctintPo QueryCPSCTInterestRateService(CpSctintPo cpInst) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (CpSctintPo) dao.selectOneObject("CPSCTInterestRateMapper.QueryCPSCTInterestRateService", cpInst);
	}
}
