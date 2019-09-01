package com.clps.fm.service.impl;

//import java.util.HashMap;
//import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
//import com.clps.core.sys.util.DateTimeUtils;
//import com.clps.core.sys.util.QueryListUtils;
import com.clps.fm.service.ItemInfoInqService;
/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class ItemInfoInqServiceImpl extends BaseService implements ItemInfoInqService {
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> inqQueryOneService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("itemInfoInqMapper.inqQueryOneService", map);
	}
}