package com.clps.ln.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.service.LnContractDbListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class LnContractDbListServiceImpl extends BaseService implements LnContractDbListService{
	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
	@Override
	public Map<String, Object> LnContractDbList(Map<String, Object> map) throws Exception {
		log.info("LN - 查找呆滞合同账号");

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("LnContractDbListMapper.queryLnContractInqByContractId_count", map);
        
        //TODO 插入 
		map = QueryListUtils.changeInputData(map,total);
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("LnContractDbListMapper.queryLnContractInqByContractId", map);

        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
        if (total != 0) {
            resultMap.put("cntrct_no", list.get(list.size() - 1).get("cntrct_id"));
            resultMap.put("cntrct_status", list.get(list.size() - 1).get("cntrct_status"));
        } else {
            resultMap.put("cntrct_no", "");
            resultMap.put("cntrct_status", "");
        }
        return resultMap;
	}

}
