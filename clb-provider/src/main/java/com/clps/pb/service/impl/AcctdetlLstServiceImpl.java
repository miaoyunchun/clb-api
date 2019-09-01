
package com.clps.pb.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.AcctdetlLstService;


/**
 * 财富账户分组搜索
 * 
 * @author Clement.zhu
 * 2017年1月8日 下午1:42:36     
 */
@Component
@Service(version="1.0.0")//分布式服务注解和版本号
public class AcctdetlLstServiceImpl extends BaseService implements AcctdetlLstService {

	 /**
     * 财富账户分组搜索
     * @param map 财富户口等级等信息字段
     * @return map 返回账户号等信息字段
     * @throws Exception
     */
	@Transactional(readOnly = true)//只读事务
	@Override
	public List<Map<String, Object>> accountLstInq(Map<String, Object> map) throws Exception {
		log.info("开始分组搜索服务");
		if(map.get("accum_tot").equals("01")){
			map.put("min_inte_accum_tot",0.00);
			map.put("max_inte_accum_tot",99.00);
		}
		else if(map.get("accum_tot").equals("02")){
			map.put("min_inte_accum_tot",100.00);
			map.put("max_inte_accum_tot",499.00);
		}
		else if(map.get("accum_tot").equals("03")){
			map.put("min_inte_accum_tot",500.00);
			map.put("max_inte_accum_tot",Double.MAX_VALUE);
		}
		if(map.get("loc_avr_wth").equals("01")){
			map.put("min_loc_avr_wth",0.00);
			map.put("max_loc_avr_wth",499999.99);
		}
		else if(map.get("loc_avr_wth").equals("02")){
			map.put("min_loc_avr_wth",500000.00);
			map.put("max_loc_avr_wth",999999.99);
		}
		else if(map.get("loc_avr_wth").equals("03")){
			map.put("min_loc_avr_wth",1000000.00);
			map.put("max_loc_avr_wth",Double.MAX_VALUE);
		}
		if(map.get("dp_tot_amt").equals("01")){
			map.put("min_loc_current_bal",0.00);
			map.put("max_loc_current_bal",9999.99);
		}
		else if(map.get("dp_tot_amt").equals("02")){
			map.put("min_loc_current_bal",10000.00);
			map.put("max_loc_current_bal",49999.99);
		}
		else if(map.get("dp_tot_amt").equals("03")){
			map.put("min_loc_current_bal",50000.00);
			map.put("max_loc_current_bal",99999.99);
		}
		else if(map.get("dp_tot_amt").equals("04")){
			map.put("min_loc_current_bal",100000.00);
			map.put("max_loc_current_bal",Double.MAX_VALUE);
		}
		List<Map<String, Object>> selectList = dao.selectListMap("AcctdetlLstMapper.accountLstInq", map);
		if(selectList.size()>0){
			return selectList;
		}else{
			return null;
		}
	}
}

