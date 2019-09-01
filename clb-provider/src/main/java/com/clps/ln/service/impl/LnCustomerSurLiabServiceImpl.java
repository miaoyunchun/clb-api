package com.clps.ln.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.service.LnCustomerSurLiabService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnCustomerSurLiabServiceImpl extends BaseService implements LnCustomerSurLiabService {
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
    @Override
    public Map<String, Object> lnCustomerSurLiab(Map<String, Object> map) throws Exception {
        log.info("LN - 计算客户剩余负债");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("LnCustomerSurLiabMapper.queryLnCustomerSurLiabByCustomerNo_count", map);
        
        map = QueryListUtils.changeInputData(map, total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("LnCustomerSurLiabMapper.queryLnCustomerSurLiabByCustomerNo", map);
        
        // 返回map 
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        if (total != 0) {
            resultMap.put("loan_acct_id", list.get(list.size() - 1).get("cust_id"));
            resultMap.put("loan_remain_amt", list.get(list.size() - 1).get("remain_amt"));
        } else {
            resultMap.put("loan_acct_id", "");
            resultMap.put("loan_remain_amt", "");
        }
        return resultMap;
        
    }
    
}
