package com.clps.cp.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.cp.service.OrgQueryListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class OrgQueryListServiceImpl extends BaseService implements OrgQueryListService {

    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * cp部分查询所有机构
     * @param map 查询条件 机构号
     * @return 查询结果列表 map
     * @throws Exception
     */
    @Override
    public Map<String, Object> orgListQueryService(Map<String, Object> map) throws Exception {

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");

        // 查询总条数
        Long total = (Long) dao.selectOneObject("cpOrgParameterMapper.orgQueryListService_count", map);

        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("cpOrgParameterMapper.orgQueryListService", map);

        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);

        return resultMap;
    }
}
