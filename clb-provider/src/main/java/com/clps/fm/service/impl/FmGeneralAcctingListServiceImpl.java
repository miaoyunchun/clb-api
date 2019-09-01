package com.clps.fm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmGeneralAcctingListService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmGeneralAcctingListServiceImpl extends BaseService implements FmGeneralAcctingListService {
    @Override
    /**
     * fm
     * 查询总账列表
     */
    public Map<String, Object> fmGeneralAcctingListService(Map<String, Object> map) throws Exception {
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");

        // 查询总条数
        Long total = (Long) dao.selectOneObject("FmItemLstMapper.queryFmIteminfo_count", map);

        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("FmItemLstMapper.queryFmIteminfo", map);

        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);

        return resultMap;
    }
}
