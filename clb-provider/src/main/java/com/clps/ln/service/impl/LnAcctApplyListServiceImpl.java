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
import com.clps.ln.service.LnAcctApplyListService;

@Transactional // spring事务注解
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnAcctApplyListServiceImpl extends BaseService implements LnAcctApplyListService{

	
	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
	@Override
	public Map<String, Object> lnAcctCustInq(Map<String, Object> map) throws Exception {
		 log.info("LN - 贷款申请书列表查询");
		 
		// 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        
	        log.info("前参数Map："+map.toString());
	    // 查询总条数
	        Long total = (Long) dao.selectOneObject("lnAcctApplyListMapper.queryLnAcctByCustomerNo_count", map);
	    // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("lnAcctApplyListMapper.queryLnAcctByCustomerNo", map);
//	        String id_type=list.get(list.size()-1).get("custmoer_number").toString().substring(0,1);
//        	String id_number=list.get(list.size()-1).get("custmoer_number").toString().substring(1,18);
	     // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, map);
//	        if (total != 0) {
//	            resultMap.put("id_type", id_type);
//	            resultMap.put("id_number", id_number);
//	        } else {
//	            resultMap.put("id_type", "");
//	            resultMap.put("id_number", "");
//	        }
	        log.info("贷款申请书列表查询返回的Map:"+resultMap.toString());
	        return resultMap;
	}

}
