package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.sc.service.ScorderService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Component
@Service(version = "1.0.0")// 分布式服务注解和版本号
public class ScorderServiceImpl extends BaseService implements ScorderService {

    /**
     * 列表查询订单流水号信息服务
     *
     * @param map 证券产品相关信息
     * @return 返回订单流水号相关信息
     */
    @Transactional(readOnly = true)//只读事务
    @Override
    public Map<String, Object> scorderList(Map<String, Object> map) throws Exception {
        // 查询总条数
        Long total = (Long) dao.selectOneObject("ScorderMapper.scorderList_count", map);

        map = QueryListUtils.changeInputData(map, total);

        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("ScorderMapper.scorderList", map);

        // 处理返回
        Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        // 返回数据
        return reMap;
    }

}
