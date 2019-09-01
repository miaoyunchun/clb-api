package com.clps.ln.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.service.LnContractInquiryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class LnContractInquiryServiceImpl extends BaseService implements LnContractInquiryService {
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @Override
    public Map<String, Object> LnContractInquiry(Map<String, Object> map) throws Exception {
        log.info("LN - 合同查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("LnContractInquiryMapper.queryLnContractInqByCustomerNo_count", map);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("LnContractInquiryMapper.queryLnContractInqByCustomerNo", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);
        
        if (total != 0) {
            resultMap.put("lncntrct_cntrct_no", list.get(list.size() - 1).get("lncntrct_cntrct_no"));
            resultMap.put("lncntrct_cntrct_status", list.get(list.size() - 1).get("lncntrct_cntrct_status"));
        } else {
            resultMap.put("lncntrct_cntrct_no", "");
            resultMap.put("lncntrct_cntrct_status", "");
        }
        
        return resultMap;
    }
    
}
