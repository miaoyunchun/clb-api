package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.sc.service.HoldingService;
import com.clps.util.DecimalCalculate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class HoldingServiceImpl extends BaseService implements HoldingService {

    private static final String ERR_NORMAL = "0000";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0001";
    private static final String ERR_HOLDING_INFO_ADD_FAIL = "0002";
    private static final String ERR_HOLDING_INFO_LIST_QUERY_FAIL = "0003";
    private static final String ERR_HOLDING_INFO_UPD_FAIL = "0004";
    private static final String ERR_NO_SUCH_HOLDING_INFO = "0005";
    private static final String ERR_INSUFFICIENT_HOLDINGS = "0006";


    //非只读事务，支持当前事务(常见)，遇到异常Rollback
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public Map<String, Object> holdingAdd(Map<String, Object> map) throws Exception {
        log.info("进入HoldingAdd服务");
        map.put("create_time", DateTimeUtils.nowToSystem());
        map.put("update_time", DateTimeUtils.nowToSystem());
        String buy_num = map.get("buy_num").toString();
        String buy_amt = map.get("buy_amt").toString();
        //交易买入
        if (map.get("sc_tran_type").equals("B")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> queryMap = (Map<String, Object>) dao.selectOneMap("holdingMapper.holdingQueryOneService", map);
            //没有持仓信息，新建持仓
            if (queryMap == null || queryMap.size() == 0) {
                map.put("holding_num", buy_num);
                map.put("avail_num", buy_num);
                map.put("avg_amt", buy_amt);
                int re = dao.insertOneByObject("holdingMapper.insertService", map);
                if (re == 1) {
                    map.put("successful", "true");
                    map.put("resp_code", ERR_NORMAL);
                    map.put("resp_msg", "录入-买入成功!");
                    return map;
                } else {
                    map.put("successful", "false");
                    map.put("resp_code", ERR_HOLDING_INFO_ADD_FAIL);
                    map.put("resp_msg", "录入-买入失败!");
                    return map;
                }
            }
            //更新该账户有针对该证券的持仓信息
            else {
                String query_holding_num = queryMap.get("holding_num").toString();
                String query_avg_amt = queryMap.get("avg_amt").toString();
                String final_holding_num = DecimalCalculate.add(query_holding_num, buy_num);
                String final_avg_amt = DecimalCalculate.div(
                        DecimalCalculate.add(
                                DecimalCalculate.mul(query_avg_amt, query_holding_num),
                                DecimalCalculate.mul(buy_amt, buy_num)),
                        DecimalCalculate.add(buy_num, query_holding_num));
                map.put("holding_num", final_holding_num);
                map.put("avg_amt", final_avg_amt);
                int re = dao.updateByMap("holdingMapper.editOneService", map);
                if (re == 1) {
                    map.put("successful", "true");
                    map.put("resp_code", ERR_NORMAL);
                    map.put("resp_msg", "买入更新成功!");
                    return map;
                } else {
                    map.put("successful", "false");
                    map.put("resp_code", ERR_HOLDING_INFO_UPD_FAIL);
                    map.put("resp_msg", "买入更新失败!");
                    return map;
                }

            }
        }
        //交易类型为卖出
        else if (map.get("sc_tran_type").equals("S")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> queryMap = (Map<String, Object>) dao.selectOneMap("holdingMapper.holdingQueryOneService", map);
            String query_holding_num = queryMap.get("holding_num").toString();
            //没有相应的持仓信息
            if (queryMap.size() == 0) {
                map.put("successful", "false");
                map.put("resp_code", ERR_NO_SUCH_HOLDING_INFO);
                map.put("resp_msg", "卖出失败,无相应的持仓信息!");
            }
            //持仓量小于交易量时
            else if (DecimalCalculate.compareTo(query_holding_num, buy_num) < 0) {
                map.put("successful", "false");
                map.put("resp_code", ERR_INSUFFICIENT_HOLDINGS);
                map.put("resp_msg", "卖出失败,持仓量小于交易量!");
            }
            //卖出持仓
            else if (queryMap != null && DecimalCalculate.compareTo(query_holding_num, buy_num) >= 0) {
                String final_holding_num = DecimalCalculate.sub(query_holding_num, buy_num);
                String final_avail_num = DecimalCalculate.sub(query_holding_num, buy_num);
                map.put("holding_num", final_holding_num);
                map.put("avail_num", final_avail_num);
                int re = dao.updateByMap("holdingMapper.editOneService", map);
                if (re == 1) {
                    map.put("successful", "true");
                    map.put("resp_code", ERR_NORMAL);
                    map.put("resp_msg", "卖出成功!");
                    return map;
                } else {
                    map.put("successful", "false");
                    map.put("resp_code", ERR_HOLDING_INFO_UPD_FAIL);
                    map.put("resp_msg", "卖出失败!");
                    return map;
                }
            }
        }
        return null;
    }

    /**
     * 列表查询持仓信息信息服务
     *
     * @param map 用户持仓相关信息
     * @return 返回查询成功后的数据
     */
    @Transactional(readOnly = true) // 只读事务
    @Override
    public Map<String, Object> holdingList(Map<String, Object> map) throws Exception {
        // 记录日志
        log.info("调用订单流水号列表查询服务实现");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("holdingMapper.holdingList_count", map);

        map = QueryListUtils.changeInputData(map, total);

        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("holdingMapper.holdingList", map);

        // 处理返回
        Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        // 返回数据
        return reMap;
    }

}
