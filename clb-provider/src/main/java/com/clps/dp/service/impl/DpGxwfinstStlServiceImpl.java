package com.clps.dp.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.clps.gb.service.TxnJourGenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.cp.pojo.CpProdparm;
import com.clps.cp.pojo.CpSctintPo;
import com.clps.cp.service.CPSCTInterestRateService;
import com.clps.cp.service.ProductParamService;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.pojo.DpGxregflfPo;
import com.clps.dp.service.BalanceAddService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.dp.service.GxwfinstStl;

/**
 * 分布式服务接口实现
 *
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class DpGxwfinstStlServiceImpl extends BaseService implements GxwfinstStl{
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号 自动生成号码服务
    private TxnJourGenService GBJour;
    
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号  卡片账户信息查询
    private CardAcctInqService cardacctinfo;
    
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号 产品信息查询
    private ProductParamService queryProductParam;
    
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号 利率表查询
    private CPSCTInterestRateService QueryCPSCTInterestRateService;
    
    @Reference(version = "1.0.0")
    // dubbo的服务注解,内有版本号  账户存款
    private BalanceAddService nocardBalAddService;
    
    //定义返回map
    Map<String, Object> resultMap = new HashMap<String,Object>();
    
    //定义pojo
/*    @Autowired
    private DpCardPo dpcard;
    @Autowired
    private DpAcctPo dpacct;
    @Autowired
    private CpProdparm cpprodparm;
    @Autowired
    private CpSctintPo cpsctintPo;
    @Autowired
    private DpBalancePo dpbalanceadd;*/
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
    public Map<String, Object> acctGxwfinstStl(DpGxregflfPo gxregflf) throws Exception {
        // TODO Auto-generated method stub
        // 记录日志
		//定义pojo
		DpCardPo dpcard =  new DpCardPo();
		DpAcctPo dpacct = new DpAcctPo();
		CpProdparm cpprodparm = new CpProdparm();
		CpSctintPo cpsctintPo = new CpSctintPo();
		DpBalancePo dpbalanceadd = new DpBalancePo();
        log.info("调用单条查询服务实现");
        
        //查询卡片信息
        dpcard.setCard_number(gxregflf.getCard_nbr());
        dpcard= cardacctinfo.cardNoPinInq(dpcard);
        //查询账户信息
        dpacct.setAcct_nbr(dpcard.getAssociate_acct_id());
        dpacct= cardacctinfo.acctInfoInq(dpacct);
        //查询产品信息
        cpprodparm.setProduct_id(dpacct.getProd_id());
        cpprodparm=queryProductParam.queryProductParam(cpprodparm);
        log.info(">>>test");
        log.info(cpprodparm.toString());
        log.info(cpsctintPo.toString());
        log.info(">>>test-end");
        //查询利率信息
        cpsctintPo.setSct_id(cpprodparm.getInterest_control());
        cpsctintPo=QueryCPSCTInterestRateService.QueryCPSCTInterestRateService(cpsctintPo);
        //计算日利
         //double interest_daily = (Double) interestInfoMap.get("interest_rate") / 360;
         double interest_daily = Double.valueOf(cpsctintPo.getInterest_rate()) / 360;
//        BigDecimal b   =   new   BigDecimal(interest_daily);
//        double   f1   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();

        //计算利息
        //BigDecimal b   =   new   BigDecimal((Double) acctInfoMap.get("aggr_bal") * interest_daily);
        BigDecimal b   =   new   BigDecimal(Double.valueOf(dpacct.getAggr_bal()) * interest_daily);
        double   interest   =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        //本金+利息
        //double totalAmt = (Double) acctInfoMap.get("acct_curr_bal") + interest;
        double totalAmt = Double.valueOf(dpacct.getAcct_curr_bal()) + interest;
        //更新账户信息
        dpbalanceadd.setAcct_nbr(dpacct.getAcct_nbr());
        dpbalanceadd.setMoney_add(String.valueOf(interest));
        dpbalanceadd.setCreate_user(gxregflf.getCreate_user());
        dpbalanceadd.setUpdate_user(gxregflf.getUpdate_user());
        nocardBalAddService.nocardBalAddService(dpbalanceadd);
        
/*        resultMap.put("acct_bal",acctInfoMap.get("acct_curr_bal"));
        acctInfoMap.put("acct_last_bal",acctInfoMap.get("acct_curr_bal"));
        acctInfoMap.put("acct_curr_bal",totalAmt);
        int a =  dao.updateByMap("dpBalanceRedMapper.dpBalanceUpdMapper", acctInfoMap);
        if (a != 1){
            resultMap.put("RESP-CODE", "0003");
            return resultMap;
        }*/
        //调用GB服务生成交易流水号
        Map<String, Object> gbmap = new HashMap<String,Object>();
        gbmap.put("create_user", "test");
        gbmap.put("update_user", "test");
        gbmap.put("initial", "");
        gbmap.put("length", "11");
        gbmap=GBJour.txnJourGen(gbmap);
        //写入交易历史表
        Map<String, Object> translogmap=new HashMap<String, Object>();
        resultMap.put("trans_id",gbmap.get("jour_nbr").toString());
        resultMap.put("trans_sequence","00000000"+gbmap.get("jour_nbr"));
        resultMap.put("trans_card", dpcard.getCard_number());
        resultMap.put("trans_acct", dpacct.getAcct_nbr());
        resultMap.put("trans_code", "5001");
        resultMap.put("trans_desc", "ACCT REVORKE INTEST SETTLE");
        resultMap.put("trans_time", DateTimeUtils.changeToTime());
        resultMap.put("trans_date", DateTimeUtils.changeToDate());
        resultMap.put("amount_before_trans", dpacct.getAcct_curr_bal());
        resultMap.put("amount_in_trans", interest);
        resultMap.put("amount_after_trans", totalAmt);
        resultMap.put("remark", "ACCT REVORKE INTEST SETTLE");
        resultMap.put("create_time", DateTimeUtils.nowToSystem());
        resultMap.put("update_time", DateTimeUtils.nowToSystem());
        resultMap.put("create_user", gbmap.get("create_user"));
        resultMap.put("update_user", gbmap.get("update_user"));
//        dao.insertOneByMap("dpBalanceRedMapper.addTransLogService", translogmap);
        //输出数据
        resultMap.put("card_nbr",dpcard.getCard_number());
        resultMap.put("acct_nbr",dpacct.getAcct_nbr());
        resultMap.put("acct_ccy",dpacct.getCcy());
        resultMap.put("acct_ints_amt",interest);
        resultMap.put("acct_total_bal",totalAmt);
        resultMap.put("acct_bal",dpacct.getAcct_curr_bal());

        return resultMap;
    }
	/**
	 * 交易历史写入
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
    public Map<String, Object> transLogAdd(Map<String, Object> map) throws Exception {

        dao.insertOneByMap("dpBalanceRedMapper.addTransLogService", map);
        return map;

    }

}
