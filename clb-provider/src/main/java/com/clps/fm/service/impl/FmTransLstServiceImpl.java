package com.clps.fm.service.impl;

import java.util.List;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmTransLstService;
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmTransLstServiceImpl extends BaseService implements FmTransLstService{
private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
	@Override
	public Map<String, Object> LstTransactionInfo(Map<String, Object> map) throws Exception {
		 log.info("FM - 交易流水列表查询");
		 
		// 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        
	        log.info("前参数Map："+map.toString());
	    // 查询总条数
	        Long total = (Long) dao.selectOneObject("FmTransactionListMapper.queryFmTransInfo_count", map);
	      //TODO 插入 
			map = QueryListUtils.changeInputData(map,total);
			log.info("后参数Map："+map.toString());
	    // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("FmTransactionListMapper.queryFmTransInfo", map);
	    //
	        String txn_jour=list.get(list.size() - 1).get("txn_jour").toString();
        	String txn_type=list.get(list.size() - 1).get("txn_type").toString();
        	String busn_date=list.get(list.size() - 1).get("busn_date").toString();
       //
        	       	
	     // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
	        if (total != 0) {
	            resultMap.put("txn_jour_t", txn_jour);
	            resultMap.put("txn_type", txn_type);
	            resultMap.put("busn_date", busn_date);
	        } else {
	            resultMap.put("txn_jour", "");
	            resultMap.put("txn_type", "");
	            resultMap.put("busn_date", "");
	        }
	        log.info("交易流水列表查询返回的Map:"+resultMap.toString());
	        return resultMap;
	}

	

}
