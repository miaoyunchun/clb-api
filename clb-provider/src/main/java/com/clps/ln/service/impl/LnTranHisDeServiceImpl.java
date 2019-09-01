package com.clps.ln.service.impl;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.ln.service.LnTranHisDeService;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnTranHisDeServiceImpl extends BaseService implements LnTranHisDeService{
	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> lnTransInq(Map<String, Object> map) throws Exception {
    	log.info("LN -交易历史明细查询");
        return (Map<String, Object>) dao.selectOneMap("LnTranHisDeMapper.selectLnTranHisDe", map);
	}

}
