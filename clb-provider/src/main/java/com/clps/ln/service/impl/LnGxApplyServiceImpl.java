package com.clps.ln.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.ln.pojo.LnGxapply;
import com.clps.ln.pojo.LnGxauditPo;
import com.clps.ln.service.LnGxApplyService;
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnGxApplyServiceImpl extends BaseService implements LnGxApplyService{

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int deleteGxApplyService(LnGxapply lnGxapply) throws Exception {
		// 记录日志
		log.info("调用删除服务实现");
		//map.put("update_time", DateTimeUtils.nowToSystem());
		lnGxapply.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.deleteByObject("gxMapper.deleteGxApplyService", lnGxapply);
	}

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> QueryOneGxApplyService(LnGxapply lnGxapply) throws Exception {
		// 记录日志
		log.info("展期申请簿查询服务实现");
		return  (Map<String, Object>) dao.selectOneObject("gxMapper.QueryOneGxApplyService", lnGxapply);
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public LnGxapply insertService(LnGxapply lnGxapply) throws Exception {
		log.info("调用申请簿添加服务实现");
		//map.put("gxcapply_apply_date", DateTimeUtils.changeToDate());
		//map.put("create_time", DateTimeUtils.nowToSystem());
		//map.put("update_time", DateTimeUtils.nowToSystem());
		lnGxapply.setGxcapply_apply_date(DateTimeUtils.changeToDate());
		lnGxapply.setCreate_time(DateTimeUtils.nowToSystem());
		lnGxapply.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("gxMapper.insertService",lnGxapply );
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return lnGxapply;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> QueryOneService(Map<String, Object> map) throws Exception {
		log.info("调用合同查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("gxMapper.QueryOneService", map);
	}

	
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public LnGxauditPo checkService(LnGxauditPo lnGxauditPo) throws Exception {
		// 记录日志
		log.info("调用展期申请簿审核服务实现");
		lnGxauditPo.setGxcaudit_audit_date(DateTimeUtils.changeToDate());
		lnGxauditPo.setCreate_time(DateTimeUtils.nowToSystem());
		lnGxauditPo.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("gxMapper.checkService",lnGxauditPo);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return   lnGxauditPo;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
}
