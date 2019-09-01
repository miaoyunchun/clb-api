package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.sc.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SC
 * <p>
 * Created by boris on 2016/11/14.
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CustomerServiceImpl extends BaseService implements CustomerService {

    // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_CUST_INFO_CREATE_FAIL = "0005";
    private static final String ERR_CUSTOMER_NOT_FOUND = "0006";
    private static final String ERR_NOTHING_UPDATED = "0007";
    private static final String ERR_CUST_UPDATE_FAIL = "0008";
    private static final String ERR_CUSTOMER_EXIST = "3101";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";

    private static final String KEY_RESP_CODE = "resp_code";
    private static final String KEY_UPDATE_TIME = "update_time";
    private static final String KEY_UPDATE_USER = "update_user";

    private static final String GENDER_DIGIT_MALE = "1";
    private static final String GENDER_DIGIT_FEMALE = "0";

    // 日志对象
    private Logger logger = LoggerFactory.getLogger(getClass().getName());

    private MapUtils mapUtils = new MapUtils();

    @Override
    public Map<String, Object> customerAdd(Map<String, Object> map) throws Exception {
        logger.info("SC - 新增客户基础信息");

        int re;

        try {
            re = dao.insertOneByObject("scCustomerMapper.insertCustomerService", map);
        } catch (DuplicateKeyException ex) {
            logger.warn("Duplicate key found when inserting new customer. ", ex);
            map.put(KEY_RESP_CODE, ERR_CUSTOMER_EXIST);
            return map;
        } catch (Exception ex) {
            logger.warn("Unhandled exception occured during inserting new customer. ", ex);
            map.put(KEY_RESP_CODE, ERR_UNHANDLED_EXCEPTION);
            map.put("resp_msg", ex.getMessage());
            return map;
        }

        if (re == 1) {
            // Transform sex from 0/1 to F/M
            String sex = mapUtils.getMapStringValue(map, "sex");
            if (GENDER_DIGIT_FEMALE.equals(sex)) {
                map.put("sex", "F");

            } else if (GENDER_DIGIT_MALE.equals(sex)) {
                map.put("sex", "M");

            }
            // 插入了一条数据,返回插入数据
            map.put(KEY_RESP_CODE, ERR_NORMAL);
            return map;
        } else {
            map.put(KEY_RESP_CODE, ERR_CUST_INFO_CREATE_FAIL);
            return map;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> customerInq(Map<String, Object> map) throws Exception {
        return (Map<String, Object>) dao.selectOneMap("scCustomerMapper.selectCustomerService", map);
    }

    @Override
    public Map<String, Object> customerUpd(Map<String, Object> map) throws Exception {
        Map<String, Object> origData = customerInq(map);

        if (origData == null) {
            map.put(KEY_RESP_CODE, ERR_CUSTOMER_NOT_FOUND);
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
            map.put(KEY_RESP_CODE, ERR_NOTHING_UPDATED);
            return map;
        }

        modifiedData.put("acct_nbr", mapUtils.getMapStringValue(map, "acct_nbr"));

        modifiedData.put(KEY_UPDATE_USER, map.get(KEY_UPDATE_USER));
        modifiedData.put(KEY_UPDATE_TIME,DateTimeUtils.nowToSystem() );//map.get(KEY_UPDATE_TIME)

        int re = dao.updateByMap("scCustomerMapper.updateCustomerService", modifiedData);

        if (re == 1) {
            // 插入了一条数据,返回插入数据
            map.put(KEY_RESP_CODE, ERR_NORMAL);
            return map;
        } else {
            map.put(KEY_RESP_CODE, ERR_CUST_UPDATE_FAIL);
            return map;
        }

    }

    @Override
    public Map<String, Object> customerLst(Map<String, Object> map) throws Exception {
        logger.info("SC - 客户查询列表");

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, KEY_UPDATE_TIME, "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("scCustomerMapper.customerListService_count", map);

        map = QueryListUtils.changeInputData(map, total);
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("scCustomerMapper.customerListService", map);

        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        // 返回map
        return resultMap;
    }
}
