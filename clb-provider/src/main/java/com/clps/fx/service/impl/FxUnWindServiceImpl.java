/**
 * 
 */
package com.clps.fx.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.cp.service.InternacAcctInqService;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.service.CardAcctInqService;
import com.clps.dp.service.dpTransfer;
import com.clps.fx.pojo.FxAcctExPo;
import com.clps.fx.pojo.FxRatePo;
import com.clps.fx.pojo.FxUnWindPo;
import com.clps.fx.pojo.InternacAcct;
import com.clps.fx.pojo.InternacCodePo;
import com.clps.fx.service.FxUnWindService;
import com.clps.fx.service.RateInqService;
import com.clps.fx.service.TradeLmtService;
import com.clps.gb.service.TxnJourGenService;

/**
 * TODO: Descriptions here
 * 
 * @author TODO: leo.wang
 *
 * 2017-05-06 下午2:40:00
 *
 * @version v1.0
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxUnWindServiceImpl extends BaseService implements FxUnWindService{
	
	private static final String CNY = "CNY";//交易币种为人民币
	private static  Double trade_amt;//交易金额
	private static  Double trade_price;//成交汇率
	private static  Double trade_amt_cny;//折算人民币金额
	private static  Double fee;//费用
    private static  Double total_rmb_cny;//
    //账户转账字段定义
    private static  String add_code;//账户转账入账交易代码
    private static  String red_code;//账户转账出账交易代码
    private static  String acct_add;//账户转账入账账户
    private static  String acct_red;//账户转账出账账户
    private static  Double money_red;//账户转账出账金额
    private static  Double money_add;//账户转账入账金额

    //错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_CCYERR = "0002";
    private static final String ERR_BALNOTENGHOUT = "0001";
    private static final String ERR_SQL_ERROE = "0003";
    private static final String ERR_LIMITUPD_ERROE = "0004";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    private static final String ACCT_ADD_CODE444 = "9020";
    private static final String ACCT_RED_CODE444 = "9021";
    private static final String ACCT_ADD_CODE555 = "9031";
    private static final String ACCT_RED_CODE555 = "9030";
    //map
    Map<String, Object> resultsMap = new HashMap<String,Object>();//实现类返回
    Map<String, Object> acctTransMap = new HashMap<String, Object>();//账户转账返回Map
    
    // 分布式服务方法使用
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private RateInqService RateInq;//单条汇率查询
    // 自动生成流水号服务
    @Reference(version = "1.0.0")
    private TxnJourGenService GBJour;
    // 查询内部户账户服务
    @Reference(version = "1.0.0")
    private InternacAcctInqService InternacAcctInq;
    //账户转账服务
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private dpTransfer dpAcctTransfer;
    //敞口币种限额汇总
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private TradeLmtService sumLimit;
    


    //定义POJO
    @Autowired
    private FxRatePo fxrate;//汇率查询
    @Autowired
    private FxAcctExPo fxacctex;//外汇账户兑换
    @Autowired
    private InternacCodePo internaccodecny;//定义人民币内部户代码po
    @Autowired
    private InternacAcct internacacctcny;//定义人名币内部户账户po
    @Autowired
    DpBalancePo dpbalancetrans;//账户转账po
    
    /**
     * 外汇平盘申请 VBS.FX.UNWIND  .APL
     */
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    public Map<String, Object> unwindAplly(FxUnWindPo fxunwind) throws Exception {
    	//汇率查询
    	if(fxunwind.getTran_type().equals("444")){
    		fxrate.setFx_sell_ccy(fxunwind.getCcy());
    		fxrate.setFx_buy_ccy(CNY);
    	}else if(fxunwind.getTran_type().equals("555")){
    		fxrate.setFx_sell_ccy(CNY);
    		fxrate.setFx_buy_ccy(fxunwind.getCcy());
    	}
    	Map<String, Object> reteMap = RateInq.RateInq(fxrate);
    	
    	//调用GB服务生成交易流水号
        Map<String, Object> gbmap = new HashMap<String,Object>();
        gbmap.put("create_user", "fxacex");
        gbmap.put("update_user", "fxacex");
        gbmap.put("initial", "1001");
        gbmap.put("length", "19");
        gbmap=GBJour.txnJourGen(gbmap);
        
        //
    	trade_price = Double.valueOf(reteMap.get("fx_acct_rate").toString());
    	trade_amt_cny = trade_price * Double.valueOf(fxunwind.getTrade_amt());
    	fee = trade_amt_cny * 0.0005;
    	fxunwind.setTrade_amt_cny(trade_amt_cny.toString());
    	fxunwind.setTrade_price(trade_price.toString());
    	fxunwind.setFee(fee.toString());
    	fxunwind.setTrade_date(DateTimeUtils.changeToDate());
    	fxunwind.setTrade_time(DateTimeUtils.changeToTime());
    	fxunwind.setCertifi_nbr(gbmap.get("jour_nbr").toString());
    	int a =  dao.insertOneByObject("FxunwindMapper.FxunwindApply", fxunwind);
        if (a != 1){
        	resultsMap.put("resp_code",ERR_SQL_ERROE);
            return resultsMap;
        }
        resultsMap = MapAndObjectUtils.copyPropertiesToMap(fxunwind, resultsMap);
        resultsMap.put("resp_code", ERR_NORMAL);
        return resultsMap;

    }
    
    /**
     * 外汇平盘申请 VBS.FX.UNWIND  .ADD
     */
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    public Map<String, Object> unwindAdd(FxUnWindPo fxunwind) throws Exception {
    	if(CNY == fxunwind.getCcy()){
    		resultsMap.put("resp_code", ERR_CCYERR);
            return resultsMap;
    	}
    	trade_amt = Double.valueOf(fxunwind.getTrade_amt());
    	trade_price = Double.valueOf(fxunwind.getTrade_price());
    	trade_amt_cny = Double.valueOf(fxunwind.getTrade_amt_cny());
    	fee = Double.valueOf(fxunwind.getFee());
    	Double trade_amt_sum =  0 - trade_amt;
    	//人民币内部户账户查询
    	//人民币内部户账户查询
        internaccodecny.setCcy(CNY);
        internacacctcny=InternacAcctInq.InternacAcctInq(internaccodecny);
    	//
    	if(fxunwind.getTran_type().equals("444")){
    		money_add = trade_amt;
    		money_red = trade_amt_cny + fee;
    		total_rmb_cny = money_red;
    		//账户转账调用传参
    		acct_add = fxunwind.getBank_acct();
    		acct_red = internacacctcny.getAcct_nbr();
    		add_code = ACCT_ADD_CODE444;
    		red_code = ACCT_RED_CODE444;
    		//写入外汇账户交易历史传参
    		fxacctex.setBuy_ccy(fxunwind.getCcy());
    		fxacctex.setSell_ccy(CNY);
    		fxacctex.setAcct_sold(acct_red);
    		fxacctex.setAcct_buy(acct_add);
    		fxacctex.setSell_amt(money_add.toString());
    		fxacctex.setBuy_amt(money_red.toString());
    		fxacctex.setTran_code(ACCT_ADD_CODE444);
    	}else if(fxunwind.getTran_type().equals("555")){
    		money_add = trade_amt_cny - fee;
    		money_red = trade_amt;
    		total_rmb_cny = money_add;
    		//账户转账调用传参
    		acct_add = internacacctcny.getAcct_nbr();
    		acct_red = fxunwind.getBank_acct();
    		add_code = ACCT_ADD_CODE555;
    		red_code = ACCT_RED_CODE555;
    		//写入外汇账户交易历史传参
    		fxacctex.setBuy_ccy(CNY);
    		fxacctex.setSell_ccy(fxunwind.getCcy());
    		fxacctex.setAcct_sold(acct_red);
    		fxacctex.setAcct_buy(acct_add);
    		fxacctex.setSell_amt(money_red.toString());
    		fxacctex.setBuy_amt(money_add.toString());
    		fxacctex.setTran_code(ACCT_RED_CODE555);
    	}
    	//调用账户转账
        dpbalancetrans.setAcct_no_add(acct_add);
        dpbalancetrans.setMoney_add(money_add.toString());
        dpbalancetrans.setAdd_code(add_code);
        dpbalancetrans.setAcct_no_red(acct_red);
        dpbalancetrans.setMoney_red(money_red.toString());
        dpbalancetrans.setAdd_code(add_code);        
        dpbalancetrans.setUpdate_user(fxunwind.getUpdate_user());
        dpbalancetrans.setUpdate_time(DateTimeUtils.changeToDate());
        acctTransMap = dpAcctTransfer.dpAcctTransfer(dpbalancetrans);
   		//调用VBS.FX.TRADELMT.SUM 敞口币种限额汇总
		fxunwind.setTrade_amt(total_rmb_cny.toString());
		int b = sumLimit.sumTradeLmt(fxunwind);
		if (b != 1){
        	resultsMap.put("resp_code",ERR_LIMITUPD_ERROE);
            return resultsMap;
        }
    	
    	//调用GB服务生成交易流水号
        Map<String, Object> gbmap = new HashMap<String,Object>();
        gbmap.put("create_user", "fxacex");
        gbmap.put("update_user", "fxacex");
        gbmap.put("initial", "1001");
        gbmap.put("length", "19");
        gbmap=GBJour.txnJourGen(gbmap);
        
        //写入平盘交易文件
        fxunwind.setJour_nbr(gbmap.get("jour_nbr").toString());
        fxunwind.setTotal_rmb_amt(total_rmb_cny.toString());
    	fxunwind.setTrade_date(DateTimeUtils.changeToDate());
    	fxunwind.setTrade_time(DateTimeUtils.changeToTime());
    	int a =  dao.insertOneByObject("FxunwindMapper.FxunwindAdd", fxunwind);
        if (a != 1){
        	resultsMap.put("resp_code",ERR_SQL_ERROE);
            return resultsMap;
        }
        
        //写入账户交易
        fxacctex.setOrg(fxunwind.getOrg());
        fxacctex.setTran_no(gbmap.get("jour_nbr").toString());
        fxacctex.setTran_type(fxunwind.getTran_type());
        fxacctex.setTran_ways("001");
        fxacctex.setBuy_rate(trade_price.toString());
        fxacctex.setSell_rate(trade_price.toString());
        fxacctex.setConment(fxunwind.getRemark());
        fxacctex.setRate_type("00");
        fxacctex.setCreate_user(fxunwind.getCreate_user());
        fxacctex.setUpdate_user(fxunwind.getUpdate_user());
        fxacctex.setCreate_time(DateTimeUtils.changeToDate());
        fxacctex.setUpdate_time(DateTimeUtils.changeToDate());
        fxacctex.setTran_date(DateTimeUtils.changeToDate());
        fxacctex.setTran_time(DateTimeUtils.changeToTime());
        fxacctex.setProd_code("P0060001");
        int ab =  dao.insertOneByObject("FxExchangeMapper.FxAcctExchange", fxacctex);
        if (ab != 1){
        	resultsMap.put("resp_code",ERR_SQL_ERROE);
            return resultsMap;
        }
        resultsMap = MapAndObjectUtils.copyPropertiesToMap(fxunwind, resultsMap);
        resultsMap.put("resp_code", ERR_NORMAL);
        return resultsMap;

    }
}
