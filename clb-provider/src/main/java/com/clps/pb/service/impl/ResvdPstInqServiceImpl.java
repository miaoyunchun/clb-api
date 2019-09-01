package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.PbResvdPstInqService;

/**
 * @author Neil Ding
 * @Time：2016年12月19日 下午2:25:15
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class ResvdPstInqServiceImpl extends BaseService implements PbResvdPstInqService{

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> resvdPstQueryOneService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("ResvdPstInqMapper.resvdPstQueryOneService", map);
	}

}
