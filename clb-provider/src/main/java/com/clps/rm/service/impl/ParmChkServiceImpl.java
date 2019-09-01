package com.clps.rm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.rm.pojo.RmparmPo;
import com.clps.rm.service.ParmChkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * RM - 个人客户相关服务
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class ParmChkServiceImpl extends BaseService implements ParmChkService {

    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * RM - 查询参数
     * @param rmparmPo 参数查询条件
     * @return RmparmPo 查询结果
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public RmparmPo parmInq(RmparmPo rmparmPo) throws Exception {
        log.info("RM - 参数代码查询");
        return (RmparmPo) dao.selectOneObject("parmMapper.selectParm", rmparmPo);
    }
}
