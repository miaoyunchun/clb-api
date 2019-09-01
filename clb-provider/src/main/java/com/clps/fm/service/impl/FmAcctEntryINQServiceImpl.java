package com.clps.fm.service.impl;



import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmAcctEntryINQService;
@Component
@Transactional
@Service(version="1.0.0")
public class FmAcctEntryINQServiceImpl extends BaseService implements FmAcctEntryINQService {
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> fmQueryOneService(Map<String, Object> map) throws Exception {
		 log.info("FM - 会计分录列表查询");

		// 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        
	        log.info("前参数Map："+map.toString());
	    // 查询总条数
	        Long total = (Long) dao.selectOneObject("FmAcctEntryINQMapper.queryFmAcctDetail_count", map);

			log.info("后参数Map："+map.toString());
	    // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("FmAcctEntryINQMapper.queryFmAcctDetail", map);
       	       	
	     // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));

	        log.info("会计分录列表查询返回的Map:"+resultMap.toString());
	        return resultMap;
	}
	
}