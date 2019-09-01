package com.clps.cp.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.cp.service.ProQueryListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class ProQueryListServiceImpl extends BaseService implements ProQueryListService{
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * cp 查询所有产品信息
     * @param map
     * @return "产品信息结果列表"
     * @throws Exception
     */
    public Map<String, Object> proListQueryService(Map<String, Object> map) throws Exception {

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");

        // 查询总条数
        Long total = (Long) dao.selectOneObject("productParamMapper.proQueryListService_count", map);

        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("productParamMapper.proQueryListService", map);
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);
        return resultMap;

    }
}
