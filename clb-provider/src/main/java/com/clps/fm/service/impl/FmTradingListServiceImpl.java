package com.clps.fm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmTradingListService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmTradingListServiceImpl extends BaseService implements FmTradingListService {
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * fm查询所有科目
     * @param map
     * @return 科目信息列表
     * @throws Exception
     */
    @Override
    public Map<String, Object> tradingListQueryService(Map<String, Object> map) throws Exception {
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");

        // 查询总条数
        Long total = (Long) dao.selectOneObject("FmTradingListMapper.tradingListService_count", map);

        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("FmTradingListMapper.tradingListService", map);

        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);
        return resultMap;

    }
}
