package com.clps.fm.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmItemDetailLstService;

@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class FmItemDetailLstServiceImpl extends BaseService implements FmItemDetailLstService{
private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
	@Override
	public Map<String, Object> LstItemDetailInq(Map<String, Object> map) throws Exception {
		 log.info("FM - 科目明细列表查询");
		 
		// 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        
	        log.info("前参数Map："+map.toString());
	    // 查询总条数
	        Long total = (Long) dao.selectOneObject("FmItemsDetailListMapper.queryFmItemDetail_count", map);
	      //TODO 插入 
			map = QueryListUtils.changeInputData(map,total);
			log.info("后参数Map："+map.toString());
	    // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("FmItemsDetailListMapper.queryFmItemDetail", map);
	    //
	        String txn_jour_t=list.get(list.size() - 1).get("txn_jour_t").toString();
        	String item_nbr=list.get(list.size() - 1).get("item_nbr").toString();
        	String txn_date_t=list.get(list.size() - 1).get("txn_date_t").toString();
       //
        	       	
	     // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
	        if (total != 0) {
	            resultMap.put("txn_jour_t", txn_jour_t);
	            resultMap.put("item_nbr", item_nbr);
	            resultMap.put("txn_date_t", txn_date_t);
	        } else {
	            resultMap.put("txn_jour_t", "");
	            resultMap.put("item_nbr", "");
	            resultMap.put("txn_date_t", "");
	        }
	        log.info("科目明细列表查询返回的Map:"+resultMap.toString());
	        return resultMap;
	}


}
