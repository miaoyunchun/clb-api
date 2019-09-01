package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.gb.service.TxnJourGenService;
import com.clps.sc.service.*;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单交易
 *
 * @author Ruby.Zhao
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class OrderTradeServiceImpl extends BaseService implements OrderTradeService {

    private static final String ORDER_STAT_NEW = "1";
    private static final String ORDER_STAT_CANCELLED = "5";
    private static final String ORDER_STAT_FINISHED = "8";

    private static final String ERR_NORMAL = "0000";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0001";
    private static final String ERR_NOTHING_UPDATED = "0002";
    private static final String ERR_INSUFFICIENT_BALANCE = "0003";
    private static final String ERR_PRODUCT_UPD_FAILED = "0004";
    private static final String ERR_PROD_INSUFFICIENT_ISSUE_BAL = "0005";

    // 分布式服务方法使用
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private ScOrderUpdService scOrderUpdService;

    @Reference(version = "1.0.0")
    private OrderService orderService;

    @Reference(version = "1.0.0")
    private AccountService accountService;

    @Reference(version = "1.0.0")
    private ProductMaintService productMaintService;

    @Reference(version = "1.0.0")
    private TxnJourGenService txnJourGenService;

    // 日志对象
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    //非只读事务，支持当前事务(常见)，遇到异常Rollback
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Map<String, Object> editOrderTradeService(Map<String, Object> map) throws Exception {

        logger.info("订单交易功能实现");

        // Get order info
        map = orderService.queryOrderDetails(map);

        if (ORDER_STAT_NEW.equals(MapUtils.getString(map, "order_status"))) {
            // Calculate total trade amount and update account
            Double tradeAmt = MapUtils.getDouble(map, "trade_amt");
            Double tranNum = MapUtils.getDouble(map, "sc_tran_num");
            Double totalTradeAmt = tradeAmt * tranNum;

            map.put("avg_amt", totalTradeAmt);

            Map<String, Object> scAccount = new HashMap<>();
            scAccount.put("acct_nbr", MapUtils.getString(map, "acct_nbr"));
            scAccount = accountService.accountInq(scAccount);
            Double circulAmt = MapUtils.getDouble(scAccount, "circul_amt");
            Double acctBal = MapUtils.getDouble(scAccount, "acct_bal");
            if ("B".equalsIgnoreCase(MapUtils.getString(map, "sc_tran_type"))) {
                if (circulAmt >= totalTradeAmt) {
                    circulAmt -= totalTradeAmt;
                } else if (circulAmt + acctBal >= totalTradeAmt) {
                    circulAmt = 0.0;
                    Double remainAmt = totalTradeAmt - tradeAmt;
                    acctBal -= remainAmt;
                } else {
                    map.put("order_status", ORDER_STAT_CANCELLED);
                    scOrderUpdService.orderUpdService(map);
                    map.put("resp", ERR_INSUFFICIENT_BALANCE);
                    return map;
                }
            } else if ("S".equalsIgnoreCase(MapUtils.getString(map, "sc_tran_type"))) {
                circulAmt += totalTradeAmt;
            }
            scAccount.put("acct_bal", acctBal);
            scAccount.put("circul_amt", circulAmt);
            accountService.accountUpd(scAccount);

            // Update product information
            Map<String, Object> scProduct = new HashMap<>();
            scProduct.put("sc_code", MapUtils.getString(map, "sc_code"));
            scProduct = productMaintService.productInq(scProduct);
            Integer issueSizeBal = MapUtils.getInteger(scProduct, "issue_size_bal");
            if ("B".equalsIgnoreCase(MapUtils.getString(map, "sc_tran_type"))) {
                if (issueSizeBal >= tranNum) {
                    issueSizeBal -= tranNum.intValue();
                } else {
                    map.put("order_status", ORDER_STAT_CANCELLED);
                    scOrderUpdService.orderUpdService(map);
                    map.put("resp", ERR_PROD_INSUFFICIENT_ISSUE_BAL);
                    return map;
                }
            } else if ("S".equalsIgnoreCase(MapUtils.getString(map, "sc_tran_type"))) {
                issueSizeBal += tranNum.intValue();
            }
            scProduct.put("issue_size_bal", issueSizeBal);
            scProduct = productMaintService.productUpd(scProduct);
            if (!ERR_NORMAL.equals(MapUtils.getString(scProduct, "resp_code")) &&
                    !ERR_NOTHING_UPDATED.equals(MapUtils.getString(scProduct, "resp_out"))) {
                map.put("resp", ERR_PRODUCT_UPD_FAILED);
                return map;
            }

            // If everything goes smoothly, mark the order finished
            map.put("order_status", ORDER_STAT_FINISHED);
            scOrderUpdService.orderUpdService(map);
            // Insert trade log
            Map<String, Object> jourNbr = new HashMap<>();
            jourNbr.put("initial", "TRD");
            jourNbr.put("length", "19");
            jourNbr.put("create_user", "AUTO");
            jourNbr.put("update_user", "AUTO");
            jourNbr.put("create_time", DateTimeUtils.nowToSystem());
            jourNbr.put("update_time", DateTimeUtils.nowToSystem());
            jourNbr = txnJourGenService.txnJourGen(jourNbr);
            map.put("tran_log_nbr", MapUtils.getString(jourNbr, "jour_nbr"));
            dao.insertOneByMap("ScTradeLogMapper.addTradeLog", map);
            map.put("resp", ERR_NORMAL);
            return map;
        } else {
            map.put("resp", ERR_NOTHING_UPDATED);
            return map;
        }
    }
}

