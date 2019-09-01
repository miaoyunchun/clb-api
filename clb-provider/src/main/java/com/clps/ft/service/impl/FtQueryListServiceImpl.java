/**
 * Project Name:clb-provider
 * File Name:FtQueryServiceImpl.java
 * Package Name:com.clps.ft.service.impl
 * Date:2016年12月28日下午5:19:12
 * Copyright (c) 2016, tsqking@163.com All Rights Reserved.
 *
*/

package com.clps.ft.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ft.service.FtQueryListService;

/**
 * ClassName:FtQueryServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年12月22日 下午5:19:12 <br/>
 * 
 * @author Charles
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
@Service(version = "1.0.0")
public class FtQueryListServiceImpl extends BaseService implements FtQueryListService {

	// 大额系统报文查询列表
	@SuppressWarnings("all")
	@Override
	@Transactional(readOnly = true) // 只读事务
	public Map<String, Object> FtQueryService(Map<String, Object> map) throws Exception {
		log.info("调用查询服务实现");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("FtQueryListMapper.queryService_count", map);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("FtQueryListMapper.queryService", map);
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, map);
		// 返回数据
		return reMap;
	}

}
