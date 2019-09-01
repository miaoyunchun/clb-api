package com.clps.rm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.rm.service.MaintHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * RM - 维护历史查询
 *
 * @author Boris Zhao
 * @since 2017-04-20 14:39:51
 * @version v1.0
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class MaintHistoryServiceImpl extends BaseService implements MaintHistoryService {
    
    // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @Override
    public Map<String, Object> maintList(Map<String, Object> map) throws Exception {
        log.info("RM - 维护历史查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("maintHistoryMapper.maintListService_count", map);
        
        map = QueryListUtils.changeInputData(map, total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("maintHistoryMapper.maintListService", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        
        if (total != 0) {
            resultMap.put("nxt_time", list.get(list.size() - 1).get("time_stamp"));
        } else {
            resultMap.put("nxt_time", "");
        }
        resultMap.put("all_count", total);
        return resultMap;
    }
    
    @Override
    public Map<String, Object> maintDetailedList(Map<String, Object> map) throws Exception {
        log.info("RM - 维护历史明细查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("maintHistoryMapper.detailListService_count", map);
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("maintHistoryMapper.detailListService", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeDataForCLB(list, total, null);
        
        if (total != 0) {
            resultMap.put("nxt_item", list.get(list.size() - 1).get("item"));
        } else {
            resultMap.put("nxt_item", "");
        }
        
        return resultMap;
    }
}
