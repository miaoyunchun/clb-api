package com.clps.ft.service.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.clps.core.common.service.BaseService;
import com.clps.ft.service.FtPayInfoAddService;
import com.clps.gb.service.TxnJourGenService;

public class FtPayInfoAddServiceImpl extends BaseService implements FtPayInfoAddService{
		
	public Map<String, Object> insertPayInfoService(Map<String, Object> map) throws Exception{
		
//		@Reference(version = "1.0.0")
//		// dubbo的服务注解,内有版本号
//		private TxnJourGenService GBJour;
		
//		Map<String, Object> gbmap = new HashMap<String,Object>();
//		gbmap.put("create_user", "test");
//		gbmap.put("update_user", "test");
//		gbmap.put("initial", "62128502");
//		gbmap.put("length", "19");
//		gbmap=GBJour.txnJourGen(gbmap);
//		map.put("txn_jour",gbmap.get("jour_nbr"));
		
		
		if (map.get("giro_way")=="2"){
			if(map.get("business_type")=="A100"){
				map.put("txn_code", "1416");
			}
			if(map.get("business_type")=="B100"){
				map.put("txn_code", "1417");
			}
		}
		
		
		
		int re = dao.insertOneByObject("FtPayInfoMapper.insertPaymentInfo", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
		
	}

	
}
