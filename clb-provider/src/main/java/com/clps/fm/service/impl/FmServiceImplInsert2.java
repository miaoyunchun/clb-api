/**
 * 
 */
package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmActEtr2Po;
import com.clps.fm.service.FmServiceInsert2;

/**
 * TODO: Descriptions here
 * 
 * @author TODO: Aaron
 *
 * 2017-05-04 下午5:32:46
 *
 * @version v1.0
 */


@Component
@Service(version = "1.3.0") // 分布式服务注解和版本号
public class FmServiceImplInsert2 extends BaseService implements FmServiceInsert2{

	
@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
public FmActEtr2Po insertServiceSec(FmActEtr2Po fmactetr2) throws Exception {

	log.info("调用插入服务实现");
	fmactetr2.setCreate_time(DateTimeUtils.nowToSystem());
	fmactetr2.setUpdate_time(DateTimeUtils.nowToSystem());
//	map.put("create_time", DateTimeUtils.nowToSystem());
//	map.put("update_time", DateTimeUtils.nowToSystem());
	int re = dao.insertOneByObject("FmInsertMapper.insertServiceSec", fmactetr2);
	
	if (re == 1) {
		// 插入了一条数据,返回插入数据
		return fmactetr2;
	} else {
		// 没有插入,返回空
		return null;
	}
	
}


}
