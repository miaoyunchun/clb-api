package com.clps.fm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmAcctingListQueryService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmAcctingListQueryServiceImpl extends BaseService implements FmAcctingListQueryService{
    @Override
    public Map<String, Object> InqAccountingDetail(Map<String, Object> map) throws Exception {
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");

        // 查询总条数
        Long total = (Long) dao.selectOneObject("FmAcctDetailListMapper.queryFmAcctDetail_count", map);

        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("FmAcctDetailListMapper.queryFmAcctDetail", map);

        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);

        return resultMap;
    }
}
