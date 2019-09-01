package com.clps.fm.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmActEtr2Po;
import com.clps.fm.pojo.FmActEtrPo;
import com.clps.fm.service.FmServiceInsert;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.3.0") // 分布式服务注解和版本号
public class FmServiceImplInsert extends BaseService implements FmServiceInsert {
	
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
@Override
public FmActEtrPo insertServiceOne(FmActEtrPo fmactetr) throws Exception {
	// 记录日志
//	List<Object> valueList = new ArrayList<Object>();
//	Iterator<Object> it2 = map.values().iterator();
//	while(it2.hasNext()){
//		
//		valueList.add(it2.next());//把value迭代存放到valueList
//	}
//	List<Map<?,?>> list = new ArrayList<Map<?,?>>();
	
	

	log.info("调用插入服务实现");
	fmactetr.setCreate_time(DateTimeUtils.nowToSystem());
	fmactetr.setUpdate_time(DateTimeUtils.nowToSystem());
//	map.put("create_time", DateTimeUtils.nowToSystem());
//	map.put("update_time", DateTimeUtils.nowToSystem());
	int re = dao.insertOneByObject("FmInsertMapper.insertServiceOne", fmactetr);

	if (re == 1) {
		// 插入了一条数据,返回插入数据
		return fmactetr;
	} else {
		// 没有插入,返回空
		return null;
	}
	
}
}
