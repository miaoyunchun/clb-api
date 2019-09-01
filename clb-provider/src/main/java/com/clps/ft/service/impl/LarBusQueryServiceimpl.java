package com.clps.ft.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.ft.service.LarBusQueryService;

/** 
* 分布式服务接口实现
* @Description: TODO
* @author  Andy.wang
* @date 2016年12月26日 下午1:44:49
*/
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LarBusQueryServiceimpl extends BaseService implements LarBusQueryService{
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> LarBusQueryOneService(Map<String, Object> map) throws Exception {
	// 记录日志
	log.info("调用单条查询服务实现");
	return (Map<String, Object>) dao.selectOneMap("larbusMapper.larbusQueryOneService", map);
	}

}
