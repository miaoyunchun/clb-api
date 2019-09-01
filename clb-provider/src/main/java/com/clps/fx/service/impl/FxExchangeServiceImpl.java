package com.clps.fx.service.impl;

import java.math.BigDecimal;
import java.security.cert.CertificateNotYetValidException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.clps.cp.service.InternacAcctInqService;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.service.BalanceInqService;
import com.clps.gb.service.TxnJourGenService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.collections.map.StaticBucketMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.pojo.FxAcctExPo;
import com.clps.fx.pojo.FxCashExPo;
import com.clps.fx.pojo.InternacAcct;
import com.clps.fx.pojo.InternacCodePo;
import com.clps.fx.service.FxExchangeService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.dp.service.dpTransfer;

/**
 * 分布式服务接口实现
 *
 * @author snow
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxExchangeServiceImpl extends BaseService implements FxExchangeService{
	//定义常量
    private static  String add_code;//账户转账入账交易代码
    private static  String red_code;//账户转账出账交易代码
    private static  String acct_add;//账户转账入账账户
    private static  String acct_red;//账户转账出账账户
    private static  Double money_red;//账户转账出账金额
    private static  Double money_add;//账户转账入账金额
    private static  Double cust_sell_amt;//客户卖出币种金额
    private static  Double cust_buy_amt;//客户买入金额
    private static  Double acct_rate;//账户兑换汇率
    private static  Double fx_cash_rate;//现金兑换汇率
    
    //交易类型，交易码，币种等
    private static final String PURCHASING = "111";//交易类型为购汇
    private static final String SETTLMENT = "222";//交易类型为购汇
    private static final String ARBITRAGE = "333";//交易类型为套汇
    private static final String CNY = "CNY";//交易币种为人民币
    private static final String ACCT_ADD_CODE333 = "9013";
    private static final String ACCT_RED_CODE333 = "9012";
    private static final String ACCT_ADD_CODE222 = "8011";
    private static final String ACCT_RED_CODE222 = "8010";
    private static final String ACCT_ADD_CODE111 = "9011";
    private static final String ACCT_RED_CODE111 = "9010";
    private static final String CASH_ADD_CODE222 = "8130";
    private static final String CASH_RED_CODE222 = "8130";
    private static final String CASH_ADD_CODE111333 = "8132";
    private static final String CASH_RED_CODE111333 = "8133";
    
    //错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_BALNOTENGHOUT = "0001";
    private static final String ERR_SQL_ERROE = "0003";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
  
    //定义POJO
    @Autowired
    DpAcctPo dpacct;//账户信息po
    @Autowired
    InternacCodePo internaccodecny;//定义人民币内部户代码po
    @Autowired
    InternacAcct internacacctcny;//定义人名币内部户账户po
    @Autowired
	InternacCodePo internaccode;//定义外币内部户代码po
    @Autowired
	InternacAcct internacacct;//定义外币内部互账户po
    @Autowired
    DpBalancePo dpbalancetrans;//账户转账po
    
    //定义返回Map
    Map<String, Object> acctTransMap = new HashMap<String, Object>();//账户转账返回Map
    Map<String, Object> resultsMap = new HashMap<String,Object>();//实现类返回
    
    // 分布式服务方法使用
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private CardAcctInqService acctMsgInq;
    //账户转账服务
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private dpTransfer dpAcctTransfer;
    // 自动生成流水号服务
    @Reference(version = "1.0.0")
    private TxnJourGenService GBJour;
    // 查询内部户账户服务
    @Reference(version = "1.0.0")
    private InternacAcctInqService InternacAcctInq;

    


    //外汇账户兑换
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    public Map<String, Object> acctExchange(FxAcctExPo fxacctex) throws Exception {

        // 记录日志
        log.info("调用账户兑换服务实现");
        cust_sell_amt = Double.valueOf(fxacctex.getSell_amt());
        cust_buy_amt = Double.valueOf(fxacctex.getBuy_amt());
        acct_rate = Double.valueOf(fxacctex.getAcct_rate());

        //判断兑换类型tran_type并账户交易 111=购汇 222=结汇 333=套汇
        //套汇
        if(fxacctex.getTran_type().equals(ARBITRAGE)){
            if(cust_sell_amt>0){
                // 调用账户查询服务,获取返回
            	dpacct.setAcct_nbr(fxacctex.getAcct_sold());
            	dpacct = acctMsgInq.acctInfoInq(dpacct);
                money_red = cust_sell_amt;
                //判断账户余额符合交易金额
                if(Double.valueOf(dpacct.getAcct_curr_bal()) < money_red){
                	resultsMap.put("redp_code", ERR_BALNOTENGHOUT);
                    return resultsMap;
                }
                //计算交易对应人名币金额
                money_add = cust_sell_amt * acct_rate;
            }else
                if (cust_buy_amt>0){
                    // 调用账户查询服务,获取返回
                	dpacct.setAcct_nbr(fxacctex.getAcct_buy());
                	dpacct = acctMsgInq.acctInfoInq(dpacct);
                    //计算交易对应人名币金额
                    money_add = cust_buy_amt;
                    money_red = money_add / acct_rate;
                    //判断账户余额符合交易金额
                    if(Double.valueOf(dpacct.getAcct_curr_bal())< money_red){
                    	resultsMap.put("RESP-CODE", ERR_BALNOTENGHOUT);
                        return resultsMap;
                    }
                }
            //调用账户转账
            acct_add = fxacctex.getAcct_buy();
            add_code = ACCT_ADD_CODE333;
            acct_red = fxacctex.getAcct_sold();
            red_code = ACCT_RED_CODE333;


        }else
        //结购汇
        if (fxacctex.getTran_type().equals(PURCHASING) || fxacctex.getTran_type().equals(SETTLMENT)){
            // 调用账户查询服务,获取返回
            dpacct.setAcct_nbr(fxacctex.getAcct_sold());
            dpacct = acctMsgInq.acctInfoInq(dpacct);
            Double acct_curr_bal = Double.valueOf(dpacct.getAcct_curr_bal());
            if(cust_sell_amt > 0){
                //计算交易出账对应人民币金额
                money_red = cust_sell_amt;
                //判断账户余额符合交易金额
                if(acct_curr_bal < money_red){
                	resultsMap.put("resp_code", ERR_BALNOTENGHOUT);
                    return resultsMap;
                }
                //计算交易出账对应人民币金额
                 //结汇
                if (fxacctex.getBuy_ccy().equals("CNY")){
                    money_add = money_red * acct_rate;
                    add_code = ACCT_ADD_CODE222;
                    red_code = ACCT_RED_CODE222;
                }else  //购汇
                    {
                        money_add = money_red / acct_rate;
                        add_code = ACCT_ADD_CODE111;
                        red_code = ACCT_RED_CODE111;
                }
            }else
            if (cust_buy_amt > 0){
                //判断交易类型
                //结汇
                if (fxacctex.getBuy_ccy().equals(CNY)){
                    money_add = cust_buy_amt;
                    money_red = money_add / acct_rate;
                    if(acct_curr_bal < money_red){
                    	resultsMap.put("resp_code", ERR_BALNOTENGHOUT);
                        return resultsMap;
                    }
                    add_code = ACCT_ADD_CODE222;
                    red_code = ACCT_RED_CODE222;
                }else  //购汇
                {
                    money_add = cust_buy_amt;
                    money_red = money_add * acct_rate;
                    if(acct_curr_bal < money_red){
                    	resultsMap.put("resp_code", ERR_BALNOTENGHOUT);
                        return resultsMap;
                    }
                    add_code = ACCT_ADD_CODE111;
                    red_code = ACCT_RED_CODE111;
                }
            }
            //调用账户转账
            acct_add = fxacctex.getAcct_buy();
            acct_red = fxacctex.getAcct_sold();
        }
        
        //账户转账
        dpbalancetrans.setAcct_no_add(acct_add);
        dpbalancetrans.setMoney_add(money_add.toString());
        dpbalancetrans.setAdd_code(add_code);
        dpbalancetrans.setAcct_no_red(acct_red);
        dpbalancetrans.setMoney_red(money_red.toString());
        dpbalancetrans.setAdd_code(add_code);
        dpbalancetrans.setUpdate_user(fxacctex.getUpdate_user());
        dpbalancetrans.setUpdate_time(DateTimeUtils.changeToDate());
        acctTransMap = dpAcctTransfer.dpAcctTransfer(dpbalancetrans);

        //调用GB服务生成交易流水号
        Map<String, Object> gbmap = new HashMap<String,Object>();
        gbmap.put("create_user", "fxacex");
        gbmap.put("update_user", "fxacex");
        gbmap.put("initial", "0001");
        gbmap.put("length", "19");
        gbmap=GBJour.txnJourGen(gbmap);


        //帐户交易表数据插入
        fxacctex.setTran_no(gbmap.get("jour_nbr").toString());
        fxacctex.setBuy_rate(fxacctex.getAcct_rate());
        fxacctex.setSell_rate(fxacctex.getAcct_rate());
        fxacctex.setTran_ways("001");
        int a =  dao.insertOneByObject("FxExchangeMapper.FxAcctExchange", fxacctex);
        if (a != 1){
        	resultsMap.put("resp_code",ERR_SQL_ERROE);
            return resultsMap;
        }
        resultsMap = MapAndObjectUtils.copyPropertiesToMap(fxacctex, resultsMap);
        BigDecimal red   =   new   BigDecimal(money_red);
        double   money_red1   =   red.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal add   =   new   BigDecimal(money_add);
        double   money_add1   =   add.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        resultsMap.put("sell_amt",money_red1);
        resultsMap.put("buy_amt",money_add1);
        resultsMap.put("acct_buy_bal",acctTransMap.get("money_add_bal"));
        resultsMap.put("acct_sold_bal",acctTransMap.get("money_red_bal"));
        resultsMap.put("resp_code", ERR_NORMAL);
        return resultsMap;
    }



    //外汇现金兑换
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    public Map<String, Object> cashExchange(FxCashExPo fxcashex) throws Exception {
        //人民币内部户账户查询
        internaccodecny.setCcy(CNY);
        internacacctcny=InternacAcctInq.InternacAcctInq(internaccodecny);
        cust_sell_amt = Double.valueOf(fxcashex.getCust_sell_amt());
        cust_buy_amt = Double.valueOf(fxcashex.getBuy_amt());
        fx_cash_rate = Double.valueOf(fxcashex.getFx_cash_rate());
        //判断交易类型
        if (fxcashex.getTran_type().equals(PURCHASING)){
            //售汇内部户查询
            internaccode.setCcy(fxcashex.getCust_sell_ccy());
            internacacct=InternacAcctInq.InternacAcctInq(internaccode);
            //计算交易金额
            if(cust_sell_amt > 0){
                money_add = cust_sell_amt;
                money_red = money_add * fx_cash_rate;
            }else
                if (cust_buy_amt > 0){
                    money_red = cust_buy_amt;
                    money_add = money_red / fx_cash_rate;
                }
            acct_add = internacacct.getAcct_nbr();//外币内部户
            add_code = CASH_ADD_CODE222;
            acct_red = internacacctcny.getAcct_nbr();//人民币内部户
            red_code = CASH_RED_CODE222;
        }else//结汇
            if (fxcashex.getTran_type().equals(SETTLMENT)){
            	//结汇内部户查询
            	internaccode.setCcy(fxcashex.getBuy_ccy());
            	internacacct=InternacAcctInq.InternacAcctInq(internaccode);
            	//计算交易金额
                if(cust_sell_amt>0){
                    money_add = cust_sell_amt;
                    money_red = money_add / fx_cash_rate;
                }else
                    if (cust_buy_amt>0){
                        money_red = cust_buy_amt;
                        money_add = money_red * fx_cash_rate;
                    }
                //账户转账数据
                acct_add = internacacctcny.getAcct_nbr();//人民币内部户
                add_code = CASH_ADD_CODE111333;
                acct_red = internacacct.getAcct_nbr();//外币内部户
                red_code = CASH_RED_CODE111333;
            }else//套汇
                if(fxcashex.getTran_type().equals(ARBITRAGE)){

                	internaccode.setCcy(fxcashex.getCust_sell_ccy());
                	internacacct=InternacAcctInq.InternacAcctInq(internaccode);
                	InternacCodePo internaccodebuy = new InternacCodePo();
                	InternacAcct internacacctbuy = new InternacAcct();
                	internaccodebuy.setCcy(fxcashex.getBuy_ccy());
                	internacacctbuy=InternacAcctInq.InternacAcctInq(internaccodebuy);
                    if (cust_sell_amt>0){
                        money_add = cust_sell_amt;
                        money_red = money_add * fx_cash_rate;
                    }else
                        if (cust_buy_amt > 0){
                            money_red = cust_buy_amt;
                            money_add = money_red / fx_cash_rate;
                        }
                    acct_add = internacacct.getAcct_nbr();//卖出外币内部户
                    add_code = CASH_ADD_CODE111333;
                    acct_red = internacacctbuy.getAcct_nbr();//买入外币内部户
                    red_code = CASH_RED_CODE111333;
                }
        //调用账户转账功能
        dpbalancetrans.setAcct_no_add(acct_add);
        dpbalancetrans.setMoney_add(money_add.toString());
        dpbalancetrans.setAdd_code(add_code);
        dpbalancetrans.setAcct_no_red(acct_red);
        dpbalancetrans.setMoney_red(money_red.toString());
        dpbalancetrans.setAdd_code(add_code);        
        dpbalancetrans.setUpdate_user(fxcashex.getUpdate_user());
        dpbalancetrans.setUpdate_time(DateTimeUtils.changeToDate());
        acctTransMap = dpAcctTransfer.dpAcctTransfer(dpbalancetrans);
        //调用GB服务生成交易流水号
        Map<String, Object> gbmap = new HashMap<String,Object>();
        gbmap.put("create_user", "fxacex");
        gbmap.put("update_user", "fxacex");
        gbmap.put("initial", "0001");
        gbmap.put("length", "19");
        gbmap=GBJour.txnJourGen(gbmap);

        //现金交易表数据插入
        fxcashex.setTran_no(gbmap.get("jour_nbr").toString());
        fxcashex.setBuy_rate(fxcashex.getFx_cash_rate());
        fxcashex.setSelling_rate(fxcashex.getFx_cash_rate());
        int a =  dao.insertOneByObject("FxExchangeMapper.FxCashExchange", fxcashex);
        if (a != 1){
        	resultsMap.put("resp_code", ERR_SQL_ERROE);
            return resultsMap;
        }
        resultsMap = MapAndObjectUtils.copyPropertiesToMap(fxcashex, resultsMap);
        BigDecimal red   =   new   BigDecimal(money_red);
        double   money_red1   =   red.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal add   =   new   BigDecimal(money_add);
        double   money_add1   =   add.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        resultsMap.put("cust_sell_amt",money_add1);
        resultsMap.put("buy_amt",money_red1);
        resultsMap.put("resp_code", ERR_NORMAL);
        return resultsMap;
    }


    //外汇储蓄零头兑换
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    public Map<String, Object> fraExchange(FxAcctExPo fxacctex) throws Exception {
    	//人民币内部户账户查询
    	internaccodecny.setCcy(CNY);
    	internacacctcny=InternacAcctInq.InternacAcctInq(internaccodecny);
    	//兑换账户币种内部户查询
    	internaccode.setCcy(fxacctex.getSell_ccy());
    	internacacct=InternacAcctInq.InternacAcctInq(internaccode);
        //账户零头兑换至银行内部户
        money_add= Double.valueOf(fxacctex.getSell_amt());
        money_red=money_add * Double.valueOf(fxacctex.getAcct_rate());
        DpBalancePo dpbalancetrans = new DpBalancePo();
        dpbalancetrans.setAcct_no_add(internacacct.getAcct_nbr());
        dpbalancetrans.setAcct_no_red(internacacctcny.getAcct_nbr());
        dpbalancetrans.setMoney_add(money_add.toString());
        dpbalancetrans.setMoney_red(money_red.toString());
        dpbalancetrans.setAdd_code(ACCT_ADD_CODE333);
        dpbalancetrans.setRed_code(ACCT_RED_CODE333);
        dpbalancetrans.setUpdate_user(fxacctex.getUpdate_user());
        dpbalancetrans.setUpdate_time(DateTimeUtils.changeToDate());
        acctTransMap = dpAcctTransfer.dpAcctTransfer(dpbalancetrans);

        //调用GB服务生成交易流水号
        Map<String, Object> gbmap = new HashMap<String,Object>();
        gbmap.put("create_user", "fxacex");
        gbmap.put("update_user", "fxacex");
        gbmap.put("initial", "0001");
        gbmap.put("length", "19");
        gbmap=GBJour.txnJourGen(gbmap);

        //帐户交易表数据插入
        fxacctex.setTran_no(gbmap.get("jour_nbr").toString());
        fxacctex.setBuy_rate(fxacctex.getAcct_rate());
        fxacctex.setSell_rate(fxacctex.getAcct_rate());
        fxacctex.setTran_ways("001");
        int a =  dao.insertOneByObject("FxExchangeMapper.FxAcctExchange", fxacctex);
        if (a != 1){
        	resultsMap.put("resp_code", ERR_SQL_ERROE);
            return resultsMap;
        }
        resultsMap = MapAndObjectUtils.copyPropertiesToMap(fxacctex, resultsMap);
        BigDecimal red   =   new   BigDecimal(money_red);
        double   money_red1   =   red.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        BigDecimal add   =   new   BigDecimal(money_add);
        double   money_add1   =   add.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
        resultsMap.put("sell_amt",money_red1);
        resultsMap.put("buy_amt",money_add1);
        resultsMap.put("acct_buy_bal",acctTransMap.get("money_add_bal"));
        resultsMap.put("acct_sold_bal",acctTransMap.get("money_red_bal"));
        resultsMap.put("resp_code", ERR_NORMAL);
        return resultsMap;

    }
    
}
