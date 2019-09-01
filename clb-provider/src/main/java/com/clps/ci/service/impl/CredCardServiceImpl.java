package com.clps.ci.service.impl;



import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.service.BalanceInqService;
import com.clps.ci.service.CredCardService;

/**
 * 分布式服务接口实现
 * 
 * @author WEI
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CredCardServiceImpl extends BaseService implements CredCardService{

	// 日志对象
	private Logger log = LoggerFactory.getLogger(getClass().getName());
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> insertService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
		//Map<String,Object> map2= (Map<String, Object>) dao.selectOneMap("CredCardMapper.insertService", map);
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("CredCardMapper.insertService", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editOneService(Map<String, Object> map) throws Exception {
		log.info("调用更新服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("CredCardMapper.credcardUpdMapper", map);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> cardQueryOneService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("CredCardMapper.cardInqMapper", map);
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> custQueryOneService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("CredCardMapper.custInqMapper", map);
	}
}
