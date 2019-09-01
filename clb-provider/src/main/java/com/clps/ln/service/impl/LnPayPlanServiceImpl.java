package com.clps.ln.service.impl;

/**
 * Project Name: clb-master
 * Package Name: com.clps.ln.service.impl
 * Description: TODO 等额本息还款计划表
 * Create Time: 2016/11/17 11:08
 * Author: Jamie.Chen
 */

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.service.LnPayPlanService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnPayPlanServiceImpl extends BaseService implements LnPayPlanService {
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    /**
     * LN - 等额本息还款计划表添加
     *
     * @param "等额本息还款计划" map
     * @return String
     * @throws Exception
     */
    @Override
    public String lnPayPlanAdd(List<Map<?, ?>> list) throws Exception {
        // 记录日志
        log.info("LN - 等额本息还款计划表添加");
        int t = list.size();
        int re = dao.insertListByMap("lnPayPlanMapper.lnPayPlanAdd", list);
        if (re == t) {
            // 更改了一条数据,返回更改数据
            return "success";
        } else {
            // 没有更改,返回空
            return "error";
        }
    }
    
    /**
     * LN - 等额本息还款计划表查询
     *
     * @param map
     * @return map
     * @throws Exception
     */
    @Override
    public Map<String, Object> lnPayPlanInq(Map<String, Object> map) throws Exception {
        log.info("LN - 等额本息还款计划表查询");
        
        // 查询总条数
        Long total = (Long) dao.selectOneObject("lnPayPlanMapper.queryTableByContract_count", map);
        
        map = QueryListUtils.changeInputData(map, total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("lnPayPlanMapper.queryTableByContract", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        return resultMap;
    }
}
