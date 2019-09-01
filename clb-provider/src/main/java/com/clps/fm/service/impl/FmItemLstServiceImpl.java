package com.clps.fm.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmItemLstService;
import com.clps.ln.service.LnAcctApplyListService;

@Transactional // spring事务注解
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class FmItemLstServiceImpl extends BaseService implements FmItemLstService  {
	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
	public Map<String, Object> ltemInfoLstInq(Map<String, Object> map) throws Exception {
		 log.info("FM - 科目信息列表查询");
		 
		// 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        
	        log.info("前参数Map："+map.toString());
	    // 查询总条数
	        Long total = (Long) dao.selectOneObject("FmItemLstMapper.queryFmIteminfo_count", map);
	      //TODO 插入 
			map = QueryListUtils.changeInputData(map,total);
			log.info("后参数Map："+map.toString());
	    // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("FmItemLstMapper.queryFmIteminfo", map);
	    //
	        String item_key=list.get(list.size() - 1).get("item_key").toString();
        	String buss_type=list.get(list.size() - 1).get("buss_type").toString();
       //
        	//Map<String, Object> map1=new HashMap<String, Object>();
        	
        	//map1.put("type", id_type);
        	//map1.put("number", id_number);
        	//list.add(map1);
        	
	     // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
	        if (total != 0) {
	        	
	            resultMap.put("item_key", item_key);
	            resultMap.put("buss_type", buss_type);
	        } else {
	            resultMap.put("item_key", "");
	            resultMap.put("buss_type", "");
	        }
	        log.info("科目信息列表查询返回的Map:"+resultMap.toString());
	        return resultMap;
	}

}
