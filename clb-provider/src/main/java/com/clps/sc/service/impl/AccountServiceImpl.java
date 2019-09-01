package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapUtils;
import com.clps.sc.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * SC
 * <p>
 * Created by boris on 2016/11/14.
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class AccountServiceImpl extends BaseService implements AccountService {

    // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0001";
    private static final String ERR_END_DATE_INVALID = "0002";
    private static final String ERR_SAVING_ACCT_INVALID = "0003";
    private static final String ERR_SEX_INVALID = "0004";
    private static final String ERR_ACCT_INFO_CREATE_FAIL = "0005";
    private static final String ERR_FEECODE_INVALID = "0006";
    private static final String ERR_ACCOUNT_NOT_FOUND = "0007";
    private static final String ERR_NOTHING_UPDATED = "0008";
    private static final String ERR_CUST_UPDATE_FAIL = "0009";
    private static final String ERR_ACCOUNT_ALREADY_REVOKED = "0010";
    private static final String ERR_ACCOUNT_ALREADY_FROZEN = "0011";
    private static final String ERR_ACCOUNT_NOT_FROZEN = "0012";
    private static final String ERR_ACCOUNT_NOT_EMPTY = "0013";
    private static final String ERR_CUSTOMER_NUMBER_GENERATE_FAIL = "2501";
    private static final String ERR_ACCOUNT_EXIST = "3101";
    private static final String ERR_SERVICE_FAIL = "9997";
    private static final String ERR_NOT_IMPLEMENTED = "9998";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";

    // Account status
    private static final String ACCT_STATUS_NORMAL = "0";
    private static final String ACCT_STATUS_FROZEN = "7";
    private static final String ACCT_STATUS_REVOKED = "9";


    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    private MapUtils mapUtils = new MapUtils();

    @Override
    public Map<String, Object> accountAdd(Map<String, Object> map) throws Exception {
        log.info("SC - 新增客户账户信息");

        int re;

        try {
            re = dao.insertOneByObject("scAccountMapper.insertAccountService", map);
        } catch (DuplicateKeyException ex) {
            map.put("resp_code", ERR_ACCOUNT_EXIST);
            return map;
        }

        if (re == 1) {
            // 插入了一条数据,返回插入数据
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("resp_code", ERR_ACCT_INFO_CREATE_FAIL);
            return map;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> accountInq(Map<String, Object> map) throws Exception {
        return (Map<String, Object>) dao.selectOneMap("scAccountMapper.selectAccountService", map);
    }

    @Override
    public Map<String, Object> accountUpd(Map<String, Object> map) throws Exception {
        Map<String, Object> origData = accountInq(map);

        if (origData == null) {
            map.put("resp_code", ERR_ACCOUNT_NOT_FOUND);
            return map;
        }

        Map<String, Object> modifiedData = new HashMap<>();
        boolean isUpdateNeeded = false;
        for (String key : map.keySet()) {
            if (origData.containsKey(key)) {
                String value = mapUtils.getMapStringValue(origData, key);
                if (!mapUtils.getMapStringValue(map, key).equals(value)) {
                    modifiedData.put(key, mapUtils.getMapStringValue(map, key));
                    isUpdateNeeded = true;
                }
            }
        }

        if (!isUpdateNeeded) {
            map.put("resp_code", ERR_NOTHING_UPDATED);
            return map;
        }

        modifiedData.put("acct_nbr", mapUtils.getMapStringValue(map, "acct_nbr"));

        modifiedData.put("update_user", map.get("update_user"));
        modifiedData.put("update_time", DateTimeUtils.changeToDate());

        int re = dao.updateByMap("scAccountMapper.updateAccountService", modifiedData);

        if (re == 1) {
            // 插入了一条数据,返回插入数据
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("resp_code", ERR_CUST_UPDATE_FAIL);
            return map;
        }
    }

    @Override
    public Map<String, Object> accountDel(Map<String, Object> map) throws Exception {
        Map<String, Object> origData = accountInq(map);

        if (origData == null) {
            map.put("resp_code", ERR_ACCOUNT_NOT_FOUND);
            return map;
        }

        String acctStatus = mapUtils.getMapStringValue(origData, "acct_status");

        switch(acctStatus) {
            case ACCT_STATUS_REVOKED:
                map.put("acct_status", ACCT_STATUS_REVOKED);
                map.put("resp_code", ERR_ACCOUNT_ALREADY_REVOKED);
                return map;
                // break;
            case ACCT_STATUS_FROZEN:
                map.put("acct_status", ACCT_STATUS_FROZEN);
                map.put("resp_code", ERR_ACCOUNT_ALREADY_FROZEN);
                return map;
                // break;
        }

        if (!mapUtils.getMapStringValue(origData, "acct_bal").equals("0") ||
                !mapUtils.getMapStringValue(origData, "hold_amt").equals("0") ||
                !mapUtils.getMapStringValue(origData, "circul_amt").equals("0")) {
            map.put("resp_code", ERR_ACCOUNT_NOT_EMPTY);
            return map;
        }
        map.put("update_user", map.get("update_user"));
        map.put("update_time", DateTimeUtils.changeToDate());

        int re = dao.updateByMap("scAccountMapper.deleteAccountService", map);

        if (re == 1) {
            // 插入了一条数据,返回插入数据
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("resp_code", ERR_CUST_UPDATE_FAIL);
            return map;
        }
    }

    @Override
    public Map<String, Object> accountFreeze(Map<String, Object> map) throws Exception {
        Map<String, Object> origData = accountInq(map);

        if (origData == null) {
            map.put("resp_code", ERR_ACCOUNT_NOT_FOUND);
            return map;
        }

        switch (mapUtils.getMapStringValue(origData, "acct_status")) {
            case ACCT_STATUS_FROZEN:
                map.put("acct_status", ACCT_STATUS_FROZEN);
                map.put("resp_code", ERR_ACCOUNT_ALREADY_FROZEN);
                return map;
            case ACCT_STATUS_REVOKED:
                map.put("acct_status", ACCT_STATUS_REVOKED);
                map.put("resp_code", ERR_ACCOUNT_ALREADY_REVOKED);
                return map;
        }

        map.put("update_user", map.get("update_user"));
        map.put("update_time", DateTimeUtils.changeToDate());

        int re = dao.updateByMap("scAccountMapper.freezeAccountService", map);

        if (re == 1) {
            map.put("acct_status", ACCT_STATUS_FROZEN);
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("acct_status", ACCT_STATUS_NORMAL);
            map.put("resp_code", ERR_CUST_UPDATE_FAIL);
            return map;
        }
    }

    @Override
    public Map<String, Object> accountUnFreeze(Map<String, Object> map) throws Exception {
        Map<String, Object> origData = accountInq(map);

        if (origData == null) {
            map.put("resp_code", ERR_ACCOUNT_NOT_FOUND);
            return map;
        }

        switch (mapUtils.getMapStringValue(origData, "acct_status")) {
            case ACCT_STATUS_NORMAL:
                map.put("acct_status", ACCT_STATUS_NORMAL);
                map.put("resp_code", ERR_ACCOUNT_NOT_FROZEN);
                return map;
            case ACCT_STATUS_REVOKED:
                map.put("acct_status", ACCT_STATUS_REVOKED);
                map.put("resp_code", ERR_ACCOUNT_ALREADY_REVOKED);
                return map;
        }

        map.put("update_user", map.get("update_user"));
        map.put("update_time", DateTimeUtils.changeToDate());

        int re = dao.updateByMap("scAccountMapper.unfreezeAccountService", map);

        if (re == 1) {
            map.put("acct_status", ACCT_STATUS_NORMAL);
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("acct_status", ACCT_STATUS_FROZEN);
            map.put("resp_code", ERR_CUST_UPDATE_FAIL);
            return map;
        }
    }
}
