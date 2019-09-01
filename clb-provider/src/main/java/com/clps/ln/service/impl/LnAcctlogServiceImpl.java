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
import com.clps.ln.service.LnAcctlogService;

@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnAcctlogServiceImpl extends BaseService implements LnAcctlogService{

	
	
	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
    
    
	@Override
	public Map<String, Object> lnAcctlogInq(Map<String, Object> map) throws Exception {
		log.info("LN - 交易历史查询");
		 
		// 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        
	    // 查询总条数
	        Long total = (Long) dao.selectOneObject("lnacctlogMapper.queryLnAcctlogByIdCodeCardno_count", map);
	        
	    // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("lnacctlogMapper.queryLnAcctlogByIdCodeCardno", map);
	     // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total,map);
	        if (total != 0) {
	            resultMap.put("contract_id" , list.get(list.size() - 1).get("contract_id"));
	            resultMap.put("code",  list.get(list.size() - 1).get("code"));
	            resultMap.put("card_no",  list.get(list.size() - 1).get("card_no"));
	        } else {
	            resultMap.put("contract_id", "");
	            resultMap.put("code", "");
	            resultMap.put("card_no", "");
	        }
	        return resultMap;
	}
}

