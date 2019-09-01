package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.sc.service.ProductMaintService;
import org.apache.commons.collections.MapUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
@Component
@Transactional
@Service(version = "1.0.0")
public class ProductMaintServiceImpl extends BaseService implements ProductMaintService {

    private static final String ERR_NORMAL = "0000";
    private static final String ERR_NOTHING_UPDATED = "0001";
    private static final String ERR_PRODUCT_UPDATE_FAIL = "0002";
    private static final String ERR_ITEM_EXIST = "0003";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "2301";
    private static final String ERR_PRODUCT_NOT_EXIST = "2501";
    private static final String ERR_SERVICE_FAIL = "9998";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";

    /**
     * 证券产品信息新增
     *
     * @param map 输入数据
     * @return map 包括返回值
     * @throws Exception
     * @author Logan
     */
    @Override
    public Map<String, Object> prodfAdd(Map<String, Object> map) throws Exception {
        // 记录日志
        log.info("SC - 证券产品信息新增服务");

        String currentDateTime = DateTimeUtils.nowToSystem();
        String currentDate = DateTimeUtils.changeToDate();

        // 处理日期
        map.put("create_time", currentDateTime);
        map.put("update_time", currentDateTime);

        Map<String, Object> prodPriceMap = new HashMap<>();
        String scPrice = MapUtils.getString(map, "sc_price");
        prodPriceMap.putAll(map);
        prodPriceMap.put("price_effect_date", currentDate);
        prodPriceMap.put("last_price_efct_date", currentDate);
        prodPriceMap.put("hihg_price", scPrice);
        prodPriceMap.put("last_hihg_price", scPrice);
        prodPriceMap.put("low_price", scPrice);
        prodPriceMap.put("last_low_price", scPrice);
        prodPriceMap.put("price", scPrice);
        prodPriceMap.put("open_price", scPrice);
        prodPriceMap.put("last_open_price", scPrice);
        prodPriceMap.put("close_price", scPrice);
        prodPriceMap.put("last_close_price", scPrice);

        int prodAddResult = -1;
        int priceAddResult = -1;

        try {
            prodAddResult = dao.insertOneByObject("scProductMapper.insertProductService", map);
            priceAddResult = dao.insertOneByObject("priceMapper.insertPrice", prodPriceMap);
        } catch (DuplicateKeyException dupkeyException) {
            map.put("resp", ERR_ITEM_EXIST);
            return map;
        }

        if (prodAddResult == 1 && priceAddResult == 1) {
            map.put("resp", ERR_NORMAL);
            return map;
        } else {
            map.put("resp", ERR_UNHANDLED_EXCEPTION);
            return map;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> productInq(Map<String, Object> map) throws Exception {
        return (Map<String, Object>) dao.selectOneMap("scProductMapper.selectProductService", map);
    }

    @Override
    public Map<String, Object> productUpd(Map<String, Object> map) throws Exception {
        log.info("SC - 更新证券产品信息");

        // 更新前数据
        Map<String, Object> origData = productInq(map);

        if (origData == null) {
            map.put("resp_out", ERR_PRODUCT_NOT_EXIST);
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

        if (!MapUtils.getString(map, "sc_code", "").equals("")) {
            modifiedData.put("sc_code", MapUtils.getString(map, "sc_code"));
        } else if (!MapUtils.getString(map, "sedol", "").equals("")) {
            modifiedData.put("sedol", MapUtils.getString(map, "sedol"));
        }
        modifiedData.put("last_maintain", currentDate);
        modifiedData.put("update_time", currentDateTime);
        modifiedData.put("update_user", MapUtils.getString(map, "update_user"));

        int re = dao.updateByMap("scProductMapper.updateProductService", modifiedData);

        if (re == 1) {
            map.put("resp_code", ERR_NORMAL);
            return map;
        } else {
            map.put("resp_code", ERR_PRODUCT_UPDATE_FAIL);
            return map;
        }
    }

    @Override
    public Map<String, Object> productList(Map<String, Object> map) throws Exception {
        log.info("SC - 证券产品列表");

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("scProductMapper.productListService_count", map);

        map = QueryListUtils.changeInputData(map, total);
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("scProductMapper.productListService", map);

        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));

        // 返回map
        return resultMap;
    }
}
