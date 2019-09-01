package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.PbFdmstInqService;

/**
 * @author feilong.song
 * @Time：2016年12月16日 上午10:29:35
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbFdmstInqServiceImpl extends BaseService implements PbFdmstInqService{
	
	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public Map<String, Object> selectQueryOneNowService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("pbMapper.selectQueryOneNowService", map);
	}                                                 

	

}
