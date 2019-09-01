package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fm.pojo.FmLgdProcPo;
import com.clps.fm.service.FmitemUpdService;

/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmitemUpdServiceImpl extends BaseService implements FmitemUpdService {
	
	
	public int editOneService(FmLgdProcPo fmlgitm) throws Exception {
		log.info("调用修改服务实现");
		fmlgitm.setUpdate_time(DateTimeUtils.nowToSystem());
		return  dao.updateByObject("fmitemupdMapper.editOneServiceitemupd", fmlgitm);
	}

	
}
