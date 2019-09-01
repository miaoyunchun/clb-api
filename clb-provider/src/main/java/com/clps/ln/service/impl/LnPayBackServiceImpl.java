package com.clps.ln.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.service.BalanceAddService;
import com.clps.dp.service.BalanceRedService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.gb.service.TxnJourGenService;
import com.clps.ln.pojo.LnAcctPo;
import com.clps.ln.service.LnAcctApplyService;
import com.clps.ln.service.LnContractMsgService;
import com.clps.ln.service.LnPayBackService;
import com.clps.ln.service.LnTransTableService;

/**
 * Project Name: clb-master
 * Package Name: com.clps.ln.service.impl
 * Description: TODO 还款服务
 * Create Time: 2016/11/30 18:03
 * Author: Jamie.Chen
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnPayBackServiceImpl extends BaseService implements LnPayBackService {
	//leo update 2017/04/25
	private DpBalancePo dpbalanceadd;
    // 分布式服务方法使用
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private CardAcctInqService cardAcctInq;

    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private BalanceAddService balanceAdd;

    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private BalanceRedService balanceRed;

    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private LnContractMsgService lnContractMsg;

    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private LnAcctApplyService lnAcctApply;

    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private TxnJourGenService txnJourGen;

    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private LnTransTableService lnTransTable;

    //操作成功
    public static final String ERR_NORMAL                  = "0000";

    //客户已存在
    public static final String ERR_CUSTOMER_EXIST          = "0001";

    //必填项为空
    public static final String ERR_NECESSARY_FIELD_EMPTY   = "0002";

    //客户姓名未找到
    private static final String ERR_CUSTOMER_NAME_NOT_FOUND = "0003";

    //客户不存在
    public static final String ERR_CUSTOMER_NOT_FOUND      = "0004";

    //无更新
    public static final String ERR_NOTHING_UPDATED         = "0005";

    //客户更新失败
    public static final String ERR_CUSTOMER_UPDATE_FAIL    = "0006";

    //客户创建失败
    public static final String ERR_CUSTOMER_CREATE_FAIL    = "0007";

    //服务调用失败
    public static final String ERR_SERVICE_FAIL            = "0008";

    //合同未找到
    public static final String ERR_CONTRACT_NO_NOT_FOUND   = "0009";

    //合同状态错误
    public static final String ERR_CONTRACT_STATUS         = "0009";

    //扣款失败
    public static final String ERR_PAYBACK_LOAN_BAL        = "0010";

    /**
     * 贷款还款
     * @param inputMap
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unused")
	@Override
    public Map<String, Object> lnPayBackPay(Map<String, Object> inputMap) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String result;
        String associate_acct_id;
        if(!inputMap.get("contract_status").equals("3")){
            resultMap.put("code", ERR_CONTRACT_STATUS);
            resultMap.put("desc", "合同状态错误");
            resultMap.put("data", null);
            // 失败返回-合同状态错误
            return resultMap;
        }
        String card_number = inputMap.get("relate_card_no").toString();
        Map<String, Object> inqMap = new HashMap<String, Object>();
        inqMap.put("card_number", card_number);
        //resultMap = cardAcctInq.cardNoPinInq(inqMap);
        if(resultMap == null || resultMap.get("associate_acct_id").toString().length() == 0){
            resultMap.put("code", ERR_CUSTOMER_NOT_FOUND);
            resultMap.put("desc", "未查到关联账号");
            resultMap.put("data", null);
            return resultMap;
        }else{
            associate_acct_id = resultMap.get("associate_acct_id").toString();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("acct_nbr", associate_acct_id);
        map.put("money_add", inputMap.get("payback_loan_bal").toString());
        map.put("card_number", inputMap.get("relate_card_no").toString());
        map.put("create_user", inputMap.get("create_user"));
        map.put("update_user", inputMap.get("update_user"));
        if(inputMap.get("payback_way").equals("01")){
        	//leo update 2017/04/25
        	//resultMap = balanceAdd.nocardBalAddService(map);
            resultMap = balanceAdd.nocardBalAddService(dpbalanceadd);
            if(!resultMap.get("RESP-CODE").equals("0000")){
                resultMap.put("code", ERR_PAYBACK_LOAN_BAL);
                resultMap.put("desc", "交易失败");
                resultMap.put("data", null);
                return resultMap;
            }
        }
        if(inputMap.get("payback_way").equals("02")){
        	//leo update 2017/04/25
            //resultMap = balanceRed.acctBalRedService(map);
        	DpBalancePo dpbalancered = new DpBalancePo();
        	resultMap = balanceRed.acctBalRedService(dpbalancered);
            if(!resultMap.get("RESP-CODE").equals("0000")){
                resultMap.put("code", ERR_PAYBACK_LOAN_BAL);
                resultMap.put("desc", "交易取款失败");
                resultMap.put("data", null);
                return resultMap;
            }
        }
        map.clear();
        LnAcctPo lnacctpo=new LnAcctPo();
        map.put("cntrct_no", inputMap.get("contract_id").toString());
        resultMap = lnContractMsg.lnContractInq(map);
        if(resultMap == null){
            //报错回滚
            resultMap.get("error_status");
        }
        BigDecimal cntrct_amount_total = parseBigDecimal(resultMap.get("cntrct_amount_total"));//合同总金额
        BigDecimal remaining_loan_bal = parseBigDecimal(resultMap.get("remaining_loan_bal"));//合同剩余金额
        BigDecimal payback_loan_bal = parseBigDecimal(inputMap.get("payback_loan_bal"));
        BigDecimal ws_interst = cntrct_amount_total.subtract(remaining_loan_bal);
        BigDecimal amount_total = cntrct_amount_total.subtract(payback_loan_bal);
        BigDecimal ws_pay_bal;
        BigDecimal ws_remaining_loan_bal ;
        /* payback-loan-bal > ws-interst */
        if(payback_loan_bal.compareTo(ws_interst) == 1){
            ws_pay_bal = payback_loan_bal.subtract(ws_interst);
            ws_remaining_loan_bal = remaining_loan_bal.subtract(payback_loan_bal);
        }
        map.put("cust_number", inputMap.get("cust_no").toString());
        //resultMap = lnAcctApply.QueryAcctServiceByCustomerNumber(resultMap);
        lnacctpo = lnAcctApply.QueryAcctServiceByCustomerNumber(lnacctpo);
        if(resultMap.get("amt_toltal").toString().length() == 0 || resultMap.get("remain_amt").toString().length() == 0){
            //报错回滚
            String error_status = resultMap.get("status").toString();
        }
        BigDecimal amt_toltal = parseBigDecimal(resultMap.get("amt_toltal"));
        BigDecimal remain_amt = parseBigDecimal(resultMap.get("remain_amt"));
        BigDecimal ws_amt_toltol = amt_toltal.subtract(payback_loan_bal);
        BigDecimal ws_remain_amt = remain_amt.subtract(payback_loan_bal);
        map.put("wsAmtToltol", ws_amt_toltol);
        map.put("wsLoanRemainAmt", ws_remain_amt);
        map.put("loan_acct_id", inputMap.get("contract_id"));
        result = lnAcctApply.updLnAcctAmt(map);
        if(result.equals("error")){
            //报错回滚
            String error_status = resultMap.get("status").toString();
        }
        String cntrct_status = inputMap.get("contract_status").toString();
        /*  amount-total 不大于0 */
        if(amount_total.compareTo(new BigDecimal(0)) != 1){
            cntrct_status = "6";
            map.clear();
            map.put("cntrct_no", resultMap.get("contract_id").toString());
            map.put("cntrct_status", cntrct_status);
            map.put("update_user", inputMap.get("update_user"));
            map.put("update_time", DateTimeUtils.nowToSystem());
            resultMap = lnContractMsg.lnContractUpd(map);
            if(resultMap == null){
                String error_status = resultMap.get("error_status").toString();
            }
        }
        map.put("create_user", inputMap.get("create_user"));
        map.put("update_user", inputMap.get("update_user"));
        map.put("length", "11");
        map.put("initial", "1215");
        resultMap = txnJourGen.txnJourGen(map);
        String jour_nbr = resultMap.get("jour_nbr").toString();
        Map<String, Object> insertMap = new HashMap<String, Object>();
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String nowDate = dateFormat.format(now);
        String nowTime = timeFormat.format(now);
        String nowdateTime = dateTimeFormat.format(now);
        insertMap.put("lnctranf_id",jour_nbr);
        insertMap.put("lnctranf_code","1621");
        insertMap.put("lnctranf_account",associate_acct_id);
        insertMap.put("lnctranf_card_no",card_number);
        insertMap.put("lnctranf_desc","LOAN PAY BACK SUCCESS");
        insertMap.put("lnctranf_date", nowDate);
        insertMap.put("lnctranf_time", nowTime);
        insertMap.put("lnctranf_amt_before_trans", cntrct_amount_total);
        insertMap.put("lnctranf_amt_in_trans", payback_loan_bal);
        insertMap.put("lnctranf_amt_after_trans",amount_total);
        insertMap.put("lnctranf_remark","PAY BACK WAY IS : " + inputMap.get("payback_way"));
        insertMap.put("lnctranf_contract_id",inputMap.get("contract_id"));
        insertMap.put("lnctranf_pay_way",inputMap.get("payback_way"));
        insertMap.put("del","N");
        insertMap.put("create_time",nowdateTime);
        insertMap.put("create_user",inputMap.get("create_user"));
        insertMap.put("update_time",nowdateTime);
        insertMap.put("update_user",inputMap.get("update_user"));
        result = lnTransTable.insertLnTrans(insertMap);
        if(result.equals(null)){
            String error_status = resultMap.get("error_status").toString();
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("contract_id",inputMap.get("contract_id"));
        dataMap.put("cust_no",associate_acct_id);
        dataMap.put("contract_status",inputMap.get("contract_status"));
        dataMap.put("cntrct_amount",inputMap.get("cntrct_amount"));
        dataMap.put("relate_card_no",inputMap.get("relate_card_no"));
        dataMap.put("cntrct_amount_total",inputMap.get("cntrct_amount_total"));
        dataMap.put("remaining_loan_bal",ws_remain_amt.toString());
        dataMap.put("payback_loan_bal",inputMap.get("payback_loan_bal"));
        dataMap.put("new_status",cntrct_status);
        dataMap.put("check_id",inputMap.get("update_user"));
        dataMap.put("payback_way",inputMap.get("payback_way"));
        resultMap.clear();
        resultMap.put("code", ERR_NORMAL);
        resultMap.put("desc", "还款交易成功");
        resultMap.put("dataMap", dataMap);
        return resultMap;



    }

    /**
     *
     * @Title: parseBigDecimal
     * @Description: TODO Object转BigDecimal
     * @author Jamie.Chen
     * @param @param value
     * @param @return
     * @param @throws Exception
     * @return BigDecimal
     * @throws
     */
    public BigDecimal parseBigDecimal(Object value) throws Exception{
        if(value.toString().length() != 0){
            return BigDecimal.valueOf(Double.valueOf(value.toString()));
        }else{
            return null;
        }
    }
}
