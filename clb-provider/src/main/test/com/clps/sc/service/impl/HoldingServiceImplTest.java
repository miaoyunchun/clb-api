package com.clps.sc.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.alibaba.dubbo.config.annotation.Reference;
import com.clps.sc.service.HoldingService;

public class HoldingServiceImplTest {

	// 分布式服务方法使用
	@Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
	private HoldingService service;



	@Test
	public void testHoldingList() throws Exception {
		Map<String, Object> map1=new HashMap<>();
		map1.put("acct_nbr", "31018880000000000070");
		
		Map<String, Object> map2=service.holdingList(map1);
		
		for (Entry<String,Object> en : map2.entrySet()) {
			System.out.println("key:"+en.getKey()+"  value:"+en.getValue());
		}
	}

}
