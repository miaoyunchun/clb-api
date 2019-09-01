package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.sc.service.HoldfService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 持仓信息更新
 *
 * @author lonnis
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class HoldfServiceImpl extends BaseService implements HoldfService {
    //非只读事务，支持当前事务(常见)，遇到异常Rollback
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @Override
    public int updateHoldingInfo(Map<String, Object> map) throws Exception {
        log.info("持仓信息更新");
        map.put("update_time", DateTimeUtils.nowToSystem());
        return dao.updateByMap("holdMapper.updateHoldingInfo", map);
    }
}
