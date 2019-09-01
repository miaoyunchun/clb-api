package com.clps.sc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.sc.service.ScholdfService;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 持仓查询
 *
 * @author Ruby.Zhao
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class ScholdfServiceImpl extends BaseService implements ScholdfService {

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)//只读事务
    @Override
    public Map<String, Object> queryHoldingInfo(Map<String, Object> map) throws Exception {
        // 记录日志
        log.info("调用单条查询服务实现");
        return (Map<String, Object>) dao.selectOneMap("scholdfMapper.queryHoldingInfo", map);
    }
}

