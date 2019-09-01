package com.clps.fx.service.impl;

import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.demo.pojo.DemoPo;
import com.clps.fx.pojo.FxVerifyPo;
import com.clps.fx.service.VerifyflService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * TODO: Descriptions here
 * 
 * @author TODO: leo.wang
 *
 * 2017-04-10 上午9:19:00
 *
 * @version v1.0
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class VerifyflServiceImpl extends BaseService implements VerifyflService{

	//核准件信息查询
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> QueryVerifyflInfo(FxVerifyPo fxverify) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneObject("verifyflInfo.queryVerifyflInfo", fxverify);
	}
	
	//核准件信息添加
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public FxVerifyPo insertVerifyflInfo(FxVerifyPo fxverify) throws Exception {
		// 记录日志
		log.info("调用插入服务实现");
//		map.put("create_time", DateTimeUtils.nowToSystem());
//		map.put("update_time", DateTimeUtils.nowToSystem());
		fxverify.setCreate_user(DateTimeUtils.nowToSystem());
		fxverify.setUpdate_user(DateTimeUtils.nowToSystem());
		fxverify.setCreate_time(DateTimeUtils.nowToSystem());
		fxverify.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("verifyflInfo.insertVerifyflInfo", fxverify);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return fxverify;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	//核准件信息修改
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editVerfyflInfo(FxVerifyPo fxverify) throws Exception {
		log.info("调用修改服务实现");
//		map.put("update_time", DateTimeUtils.nowToSystem());
		fxverify.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("verifyflInfo.editVerifyflInfo", fxverify);
	}
	
	//原map方法取消
/*	@Override
	public Map<String, Object> insertVerifyflInfo(Map<String, Object> map) throws Exception {
		log.info("调用插入服务实现");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("verifyflInfo.insertVerifyflInfo", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> QueryVerifyflInfo(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("verifyflInfo.queryVerifyflInfo", map);
	}

	@Override
	public int editVerfyflInfo(Map<String, Object> map) throws Exception {
		log.info("调用修改服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("verifyflInfo.editVerifyflInfo", map);
	}
*/
	
}
