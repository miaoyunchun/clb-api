package com.clps.ln.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.service.LnInstallmService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Project Name: clb-master
 * Package Name: com.clps.ln.service.impl
 * Description: TODO 等额本息还款计划表试算
 * Create Time: 2016/11/24 17:27
 * Author: Jamie.Chen
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnInstallmServiceImpl extends BaseService implements LnInstallmService {

    @Override
    public String lnInstallmCal(List<Map<?, ?>> list) throws Exception {
        // 记录日志
        log.info("LN - 等额本息还款计划表试算表添加");
        int t = list.size();
        int re = dao.insertListByObject("lnInstallmMapper.lnInstallmCal", list);
        
        // The number of affected rows is not predictable when using INSERT ON DUPLICATE KEY UPDATE
        // So I'll take it as successful when there is something affected
        if (re > 0) {
            // 更改了一条数据,返回更改数据
            return "success";
        } else {
            // 没有更改,返回空
            return "error";
        }
    }

    @Override
    public Map<String, Object> lnInstallmInq(Map<String, Object> map) throws Exception {
        log.info("LN - 等额本息还款计划表试算表查询");

        // 查询总条数
        Long total = (Long) dao.selectOneObject("lnInstallmMapper.queryTableByCalKey_count", map);
    
        map = QueryListUtils.changeInputData(map,total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("lnInstallmMapper.queryTableByCalKey", map);

        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null, total));
        if (total != 0) {
            resultMap.put("cal_key", list.get(list.size() - 1).get("ws_cal_key"));
            resultMap.put("periods_totol", list.get(list.size() - 1).get("periods"));
        } else {
            resultMap.put("cal_key", "");
            resultMap.put("periods_totol", "");
        }
        return resultMap;
    }
}
