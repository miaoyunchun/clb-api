package com.clps.ab.service.impl;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.ab.service.MerchantManagementService;
import com.clps.ci.service.AccountManagementService;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.gb.service.TxnJourGenService;
import org.springframework.stereotype.Component;


/**
 * 分布式服务接口实现
 * 
 * @author Sunday
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.1") // 分布式服务注解和版本号

public class MerchantManagementServiceImpl extends BaseService implements MerchantManagementService {
	@Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
	private TxnJourGenService tjgservice;//生成merch_no
	//收单费用信息添加服务
	@SuppressWarnings("unchecked")
	@Override
	public int  MerchantManagementAddService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
				log.info("添加服务");
				//日期
				Date date=new Date();
				SimpleDateFormat mm= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				map.put("create_time", mm.format(date));
				map.put("update_time", mm.format(date));
				Map<String,Object> bbc=new HashMap<String, Object>();
				Object initial = "0" + map.get("province") + map.get("city") + map.get("area") + map.get("mcc_code");
				bbc.put("initial_head", initial);
				bbc.put("length", 15);
				bbc.put("create_user", "CLPS BANK USER");
				bbc.put("update_user", "CLPS BANK USER");
				bbc=tjgservice.txnJourGen(bbc);
				if(bbc.get("successful").equals("false"))
				{
					log.info("merch_no创建失败");
					return 0;
				}
				map.put("merch_no", bbc.get("jour_nbr"));
				return dao.insertOneByMap("MerchantManagementMapper.MerchantManagementAddMapper", map);
	}

	@Override
	public int MerchantManagementUpdService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		log.info("调用修改服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());

		return dao.updateByMap("MerchantManagementMapper.MerchantManagementUpdMapper", map);

	}

	@Override
	public Map<String, Object> MerchantManagementInqService(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("MerchantManagementMapper.MerchantManagementInqMapper", map);
	}

	@Override
	public Map<String, Object> MerchantManagementLstService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用多条查询服务实现");
		/*// 搜索条件默认值处理
		if (map.get("merch_no") == null || map.get("merch_no").toString().equals("")) {
			map.put("merch_no", "1");// 默认搜索值
		}*/
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");

		String merch_no=map.get("merch_no").toString();
		String merch_sts=map.get("merch_sts").toString();
		String merch_name=map.get("merch_name").toString();
		String province=map.get("province").toString();
		String city=map.get("city").toString();
		String area=map.get("area").toString();
		String mcc_code=map.get("mcc_code").toString();
		
		// 查询总条数
		Long total = (Long) dao.selectOneObject("MerchantManagementMapper.MerchantManagementLstMapperService_count", map);
		map = QueryListUtils.changeInputData(map,total);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("MerchantManagementMapper.MerchantManagementLstMapper", map);

		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
		if (total != 0) {
			reMap.put("merch_no", merch_no);
			reMap.put("merch_sts", merch_sts);
			reMap.put("merch_name", merch_name);
			reMap.put("province", province);
			reMap.put("city", city);
			reMap.put("area", area);
			reMap.put("mcc_code", mcc_code);
		} else {
			reMap.put("merch_no", "");
			reMap.put("merch_sts", "");
			reMap.put("merch_name", "");
			reMap.put("province", "");
			reMap.put("city", "");
			reMap.put("area", "");
			reMap.put("mcc_code", "");
		}
		// 返回数据
		return reMap;
		
		
	
		
	}
	

}
