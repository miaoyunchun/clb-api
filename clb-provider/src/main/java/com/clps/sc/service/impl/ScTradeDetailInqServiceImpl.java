package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.sc.service.ScTradeDetailInqService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * SC - 查询订单流水详细
 * <br>
 * Created by boris on 2016/11/14.
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class ScTradeDetailInqServiceImpl extends BaseService implements ScTradeDetailInqService {

    // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0001";
    private static final String ERR_SERVICE_FAIL = "9997";
    private static final String ERR_NOT_IMPLEMENTED = "9998";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";

    /**
     * 查询订单流水详细
     *
     * @param map 查询条件
     * @return 查询成功返回Map，查询失败返回null
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> tradeDetailInq(Map<String, Object> map) throws Exception {
        return (Map<String, Object>) dao.selectOneMap("ScTradeLogMapper.queryTradeLog", map);
    }

    @Override
    public Map<String, Object> tradeList(Map<String, Object> map) throws Exception {
        log.info("SC - 成交历史查询");

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("ScTradeLogMapper.tradeHistory_count", map);

        map = QueryListUtils.changeInputData(map, total);
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("ScTradeLogMapper.tradeHistory", map);

        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        // 返回map
        return resultMap;
    }
}
