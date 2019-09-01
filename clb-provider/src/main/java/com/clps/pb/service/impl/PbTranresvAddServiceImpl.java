package com.clps.pb.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.pb.service.PbTranresvAddService;

/**
 * desc:新增预约转账服务接口实现类
 * @author Kevin.meng
 * @Time：2016年12月30日 下午5:39:54
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbTranresvAddServiceImpl extends BaseService implements PbTranresvAddService{
	//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public Integer pbTranresvinsertService(Map<String, Object> map)
			throws Exception {
		// 记录日志
		log.info("新增预约转账服务");
		int re = dao.insertOneByObject("PbTranresvAddMapper.insertService", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return re;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public Map<String, Object> selectOutCcy(Map<String, Object> map)
			throws Exception {
		//记录日志
		log.info("调用查询服务");
		//查询转出账户币种
		return (Map<String, Object>) dao.selectOneMap("PbTranresvAddMapper.selectOutCcy", map);
	}
	
	//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public Map<String, Object> selectInCcy(Map<String, Object> map)
			throws Exception {
		//记录日志
		log.info("调用查询服务");
		//查询转入账户币种
		return (Map<String, Object>) dao.selectOneMap("PbTranresvAddMapper.selectInCcy", map);
	}

}
