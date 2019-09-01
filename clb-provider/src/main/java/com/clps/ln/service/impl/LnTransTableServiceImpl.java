package com.clps.ln.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.ln.service.LnTransTableService;

/**
 * Project Name: clb-master
 * Package Name: com.clps.ln.service.impl
 * Description: TODO
 * Create Time: 2016/12/2 10:49
 * Author: Jamie.Chen
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnTransTableServiceImpl extends BaseService implements LnTransTableService {
    @Override
    public String insertLnTrans(Map<String, Object> map) throws Exception {
        // 记录日志
        log.info("LN - 交易历史表添加");
        int re = dao.insertOneByMap("lnTransTableMapper.insertLnTrans", map);
        if (re == 1) {
            // 更改了一条数据,返回更改数据
            return "Success";
        } else {
            // 没有更改,返回空
            return null;
        }
    }
}
