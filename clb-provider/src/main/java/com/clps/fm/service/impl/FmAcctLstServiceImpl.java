package com.clps.fm.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmAcctDetailLstService;
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class FmAcctLstServiceImpl extends BaseService implements FmAcctDetailLstService{
private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    
	@Override
	public Map<String, Object> InqAccountingDetail(Map<String, Object> map) throws Exception {
		 log.info("FM - 记账明细列表查询");
		 
		// 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        
	        log.info("前参数Map："+map.toString());
	    // 查询总条数
	        Long total = (Long) dao.selectOneObject("FmAcctDetailListMapper.queryFmAcctDetail_count", map);
	      //TODO 插入 
			map = QueryListUtils.changeInputData(map,total);
			log.info("后参数Map："+map.toString());
	    // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("FmAcctDetailListMapper.queryFmAcctDetail", map);
	    //
	        String tran_jour=list.get(list.size() - 1).get("tran_jour").toString();
        	String tran_id=list.get(list.size() - 1).get("tran_id").toString();
        	String tran_date=list.get(list.size() - 1).get("tran_date").toString();
       //
        	       	
	     // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
	        if (total != 0) {
	            resultMap.put("tran_jour", tran_jour);
	            resultMap.put("tran_id", tran_id);
	            resultMap.put("tran_date", tran_date);
	        } else {
	            resultMap.put("tran_jour", "");
	            resultMap.put("tran_id", "");
	            resultMap.put("tran_date", "");
	        }
	        log.info("记账明细列表查询返回的Map:"+resultMap.toString());
		return resultMap;
	}

}
