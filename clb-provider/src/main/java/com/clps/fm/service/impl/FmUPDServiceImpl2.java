/**
 * 
 */
package com.clps.fm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmActEtr2Po;
import com.clps.fm.service.FmUPDService2;

/**
 * TODO: Descriptions here
 * 
 * @author TODO: Aaron
 *
 * 2017-05-06 上午11:15:15
 *
 * @version v1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmUPDServiceImpl2 extends BaseService implements FmUPDService2{
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	@Override
	public FmActEtr2Po editSecService(FmActEtr2Po fmactetr2) throws Exception{
		log.info("调用插入服务实现");
		fmactetr2.setCreate_time(DateTimeUtils.nowToSystem());
		fmactetr2.setUpdate_time(DateTimeUtils.nowToSystem());
		
		int re=dao.updateByObject("FmUPDMapper.editOneService2", fmactetr2);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return fmactetr2;
		} else {
			// 没有插入,返回空
			return null;
		}
		
	}

}
