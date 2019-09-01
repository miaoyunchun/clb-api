package com.clps.fm.service.impl;



import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.FmINQService;
@Component
@Transactional
@Service(version="1.0.0")
public class FmINQServiceImpl extends BaseService implements FmINQService{
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> fmQueryOneService(Map<String, Object> map) throws Exception {
		log.info("FM - 会计分录列表查询");
//		 map.put("offset", "0");
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");

		log.info("前参数Map："+map.toString());
		// 查询总条数
		Long total = (Long) dao.selectOneObject("FmINQMapper.queryFmAcctDetail_count", map);
		//TODO 插入
//			map = QueryListUtils.changeInputData(map,total);
		log.info("后参数Map："+map.toString());
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("FmINQMapper.queryFmAcctDetail", map);
		//
		String tran_id=list.get(list.size() - 1).get("tran_id").toString();
		String cond_seq=list.get(list.size() - 1).get("cond_seq").toString();
		//

		// 返回map
		Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
		if (total != 0) {
			resultMap.put("tran_id", tran_id);
			resultMap.put("cond_seq", cond_seq);
		} else {
			resultMap.put("tran_id", "");
			resultMap.put("cond_seq", "");
		}
		log.info("会计分录列表查询返回的Map:"+resultMap.toString());
		return resultMap;
	}

}