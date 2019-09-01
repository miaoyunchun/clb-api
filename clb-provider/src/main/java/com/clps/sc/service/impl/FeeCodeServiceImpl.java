package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapUtils;
import com.clps.sc.service.FeeCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * SC
 * <p>
 * Created by boris on 2016/11/14.
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FeeCodeServiceImpl extends BaseService implements FeeCodeService {

    // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0001";
    private static final String ERR_EFFECTIVE_DATE_INVALID = "0002";
    private static final String ERR_FEE_CODE_NOT_EXIST = "0003";
    private static final String ERR_FEECODE_UPDATE_FAIL = "0004";
    private static final String ERR_FEECODE_CREATE_FAIL = "0005";
    private static final String ERR_NOTHING_UPDATED = "2501";
    private static final String ERR_FEECODE_EXIST = "3101";
    private static final String ERR_SERVICE_FAIL = "9997";
    private static final String ERR_NOT_IMPLEMENTED = "9998";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";


    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    private MapUtils mapUtils = new MapUtils();

    @Override
    public Map<String, Object> feeCodeAdd(Map<String, Object> map) throws Exception {
        log.info("SC - 新增费用信息");

        int re;

        try {
            re = dao.insertOneByObject("scFeeCodeMapper.insertFeeCodeService", map);
        } catch (DuplicateKeyException ex) {
            map.put("resp_code", ERR_FEECODE_EXIST);
            return map;
        }

        if (re == 1) {
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("resp_code", ERR_FEECODE_CREATE_FAIL);
            return map;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> feeCodeInq(Map<String, Object> map) throws Exception {
        return (Map<String, Object>) dao.selectOneMap("scFeeCodeMapper.selectFeeCodeService", map);
    }

    @Override
    public Map<String, Object> feeCodeUpd(Map<String, Object> map) throws Exception {
        log.info("SC - 更新费用信息");

        // 更新前数据
        Map<String, Object> origData = feeCodeInq(map);
        //保证前后端key一致
        map.put("fee_descript", map.get("fee_descirpt"));
        map.put("charge_type", map.get("type"));
        map.remove("fee_descirpt");
        map.remove("type");

        if (origData == null) {
            map.put("resp_out", ERR_FEE_CODE_NOT_EXIST);
            return map;
        }

        Iterator<String> iterator = map.keySet().iterator();
        Map<String, Object> modifiedData = new HashMap<>();
        modifiedData.put("update", "false");

        while (iterator.hasNext()) {
            String key = iterator.next();
            if (origData.containsKey(key)) {
                String value = origData.get(key).toString();
                if (map.containsKey(key)) {
                    if (!map.get(key).toString().equals(value)) {
                        modifiedData.put(key, map.get(key));
                        modifiedData.put("update", "true");
                    }
                }
            }
        }

        if (modifiedData.get("update").equals("false")) {
            map.put("resp_code", ERR_NOTHING_UPDATED);
            return map;
        }

        String currentDate = DateTimeUtils.changeToDate();
        String currentDateTime = DateTimeUtils.nowToSystem();

        map.put("create_time", currentDate);
        map.put("update_time", currentDate);
        map.put("last_maintain", currentDate);
        map.put("update_time", currentDateTime);

        modifiedData.put("fee_code", mapUtils.getMapStringValue(map, "fee_code"));
        modifiedData.put("last_maintain", currentDate);
        modifiedData.put("update_time", currentDateTime);
        modifiedData.put("update_user", mapUtils.getMapStringValue(map, "update_user"));

        int re = dao.updateByMap("scFeeCodeMapper.updateFeeCodeService", modifiedData);

        if (re == 1) {
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("resp_code", ERR_FEECODE_UPDATE_FAIL);
            return map;
        }
    }
}
