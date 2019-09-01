package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.gb.service.TxnJourGenService;
import com.clps.sc.service.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单详细查询
 *
 * @author Lacus, Boris
 * @since 2016年12月15日 上午11:21:49
 */
@Component
@Service(version = "1.0.0")
public class OrderServiceImpl extends BaseService implements OrderService {

    // 证券产品服务
    @Reference(version = "1.0.0")
    private ProductMaintService productMaintService;

    // SC账户服务
    @Reference(version = "1.0.0")
    private AccountService accountService;

    // 持仓相关服务
    @Reference(version = "1.0.0")
    private ScholdfService scholdfService;
    @Reference(version = "1.0.0")
    private HoldfService holdfService;

    @Reference(version = "1.0.0")
    private TxnJourGenService txnJourGenService;

    private static final String ACCT_STAT_NORMAL = "0";

    private static final String ERR_NORMAL = "0000";
    private static final String ERR_ACCOUNT_NOT_EXIST = "0003";
    private static final String ERR_BAD_ACCOUNT_STAT = "0004";
    private static final String ERR_ACCOUNT_UPDATE_FAIL = "0005";
    private static final String ERR_HOLDING_UPD_FAIL = "0006";
    private static final String ERR_ORDER_CREATE_FAIL = "0007";
    private static final String ERR_NOTHING_UPDATED = "0008";
    private static final String ERR_ITEM_ALREADY_EXIST = "3101";
    private static final String ERR_PRODUCT_NOT_EXIST = "2504";
    private static final String ERR_CURRENCY_NOT_MATCH = "8100";
    private static final String ERR_INSUFFICIENT_HOLDING = "8200";


    /**
     * 订单详细查询
     *
     * @param map 查询条件
     * @return 查询成功返回Map，查询失败返回null
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)//只读事务
    @Override
    public Map<String, Object> queryOrderDetails(Map<String, Object> map) throws Exception {
        log.info("订单详细查询");
        return (Map<String, Object>) dao.selectOneMap("orderMapper.queryOrderDetails", map);
    }

    /**
     * 交易下单
     *
     * @param map 订单信息
     * @return Map 订单信息
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Map<String, Object> addOrder(Map<String, Object> map) throws Exception {

        // Check if the product is valid
        Map<String, Object> scProduct = new HashMap<>();
        String scCode = MapUtils.getString(map, "sc_code");
        scProduct.put("sc_code", scCode);
        scProduct.put("sedol", MapUtils.getString(map, "sedol"));
        scProduct = productMaintService.productInq(scProduct);

        if (scProduct == null) {
            map.put("resp", ERR_PRODUCT_NOT_EXIST);
            return map;
        }

        // Check if this account exists with status normal
        Map<String, Object> scAccount = new HashMap<>();
        scAccount.put("acct_nbr", MapUtils.getString(map, "acct_nbr"));
        scAccount = accountService.accountInq(scAccount);

        if (scAccount == null) {
            map.put("resp", ERR_ACCOUNT_NOT_EXIST);
            return map;
        }

        if (!MapUtils.getString(scAccount, "acct_status").equals(ACCT_STAT_NORMAL)) {
            map.put("resp", ERR_BAD_ACCOUNT_STAT);
            return map;
        }

        if ("B".equalsIgnoreCase(MapUtils.getString(map, "sc_tran_type"))) {
            if (!MapUtils.getString(map, "sc_ccy").equals(MapUtils.getString(scAccount, "acct_ccy"))) {
                map.put("resp", ERR_CURRENCY_NOT_MATCH);
                return map;
            }

            Double feeAmt1 = MapUtils.getDouble(map, "fee_amt1", 0.0);
            Double feeAmt2 = MapUtils.getDouble(map, "fee_amt2", 0.0);
            Double feeAmt3 = MapUtils.getDouble(map, "fee_amt3", 0.0);
            Double feeAmt4 = MapUtils.getDouble(map, "fee_amt4", 0.0);
            Double totalAmt = feeAmt1 + feeAmt2 + feeAmt3 + feeAmt4;

            Double circulAmt = MapUtils.getDouble(scAccount, "circul_amt", 0.0);
            Double acctBal = MapUtils.getDouble(scAccount, "acct_bal", 0.0);
            Double tradeAmt = MapUtils.getDouble(map, "trade_amt", 0.0);
            circulAmt += tradeAmt;
            acctBal -= tradeAmt;

            scAccount.put("circul_amt", circulAmt);
            scAccount.put("acct_bal", acctBal);
            scAccount = accountService.accountUpd(scAccount);

            if (!ERR_NORMAL.equals(MapUtils.getString(scAccount, "resp_code")) &&
                    !ERR_NOTHING_UPDATED.equals(MapUtils.getString(scAccount, "resp_code"))) {
                map.put("resp", ERR_ACCOUNT_UPDATE_FAIL);
                return map;
            }
        } else if ("S".equalsIgnoreCase(MapUtils.getString(map, "sc_tran_type"))) {

            Map<String, Object> scHolding = new HashMap<>();
            scHolding.put("acct_nbr", MapUtils.getString(map, "acct_nbr"));
            scHolding.put("sc_code", MapUtils.getString(map, "sc_code"));
            scHolding = scholdfService.queryHoldingInfo(scHolding);
            if (scHolding == null) {
                map.put("resp", ERR_INSUFFICIENT_HOLDING);
                return map;
            }
            Double holdingAmt = MapUtils.getDouble(scHolding, "holding_num");
            Double transAmt = MapUtils.getDouble(map, "sc_tran_num");
            holdingAmt -= transAmt;
            scHolding.put("holding_num", holdingAmt);
            int re = holdfService.updateHoldingInfo(scHolding);
            if (re != 1) {
                map.put("resp", ERR_HOLDING_UPD_FAIL);
                return map;
            }
        }

        Map<String, Object> jourNbr = new HashMap<>();
        jourNbr.put("initial", "ORD");
        jourNbr.put("length", "19");
        jourNbr.put("create_user", "AUTO");
        jourNbr.put("update_user", "AUTO");
        jourNbr.put("create_time", DateTimeUtils.nowToSystem());
        jourNbr.put("update_time", DateTimeUtils.nowToSystem());
        jourNbr = txnJourGenService.txnJourGen(jourNbr);
        map.put("jour_nbr", MapUtils.getString(jourNbr, "jour_nbr"));

        int re = 0;

        try {
            re = dao.insertOneByMap("ScorderMapper.addOrder", map);
        } catch (DuplicateKeyException e) {
            map.put("resp", ERR_ITEM_ALREADY_EXIST);
        }

        if (re == 1) {
            map.put("resp", ERR_NORMAL);
        } else {
            map.put("resp", ERR_ORDER_CREATE_FAIL);
        }
        return map;
    }
}
