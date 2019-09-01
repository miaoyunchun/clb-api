package com.clps.dp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.service.InqServiceblessing;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class InqServiceImplblessing extends BaseService implements InqServiceblessing{
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());

		@SuppressWarnings("unchecked")
		public Map<String, Object> acctInqQueryService(Map<String, Object> map) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用单条查询服务实现");
			return (Map<String, Object>) dao.selectOneMap("dpInqMapperblessing.dpInqMapper", map);
		}
			
}
