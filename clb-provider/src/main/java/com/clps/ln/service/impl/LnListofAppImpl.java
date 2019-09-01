package com.clps.ln.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;

import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.service.LnListofApp;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class LnListofAppImpl extends BaseService implements LnListofApp {
	
	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    /**
     * LN - 客户合同按客户号查询列表
     * @param "查询条件 客户号" map
     * @return "查询结果列表" map
     * @throws Exception
     */

	@Override
	public Map<String, Object> inlistQueryListService(Map<String, Object> map) throws Exception {
		 log.info("LN - 展期申请簿列表查询");

	        // 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        // 查询总条数
	        Long total = (Long) dao.selectOneObject("LnListofAppMapper.queryLnListofByCustomerNo_count", map);
	        // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("LnListofAppMapper.queryLnListofByCustomerNo", map);

	        // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);
	        if (total != 0) {
	            resultMap.put("lncntrct_cntrct_no", list.get(list.size() - 1).get("lncntrct_cntrct_no").toString());
	            resultMap.put("gxcapply_loan_ext_sts", list.get(list.size() - 1).get("gxcapply_loan_ext_sts").toString());
	        } else {
	            resultMap.put("lncntrct_cntrct_no", "");
	            resultMap.put("gxcapply_loan_ext_sts", "");
	        }
	        return resultMap;
	    }

}
