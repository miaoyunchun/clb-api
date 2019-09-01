package com.clps.ab.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import org.springframework.stereotype.Component;
import com.clps.ab.service.AbManuposAddService;

import java.util.Map;

/**
 * Project Name: clb-master
 * Package Name: com.clps.ab.service.impl
 * Description: TODO
 * Create Time: 2017-02-15 15:40
 * Author: Jamie.Chen
 */
@Component
@Service(version = "1.0.1") // 分布式服务注解和版本号
public class AbmanuposAddServiceImpl extends BaseService implements AbManuposAddService {

    @Override
    public Map<String, Object> queryPosNo(Map<String, Object> map) throws Exception {
        Map resultMap = dao.selectOneMap("abEqupMentMapper.EqupMentInqMapper", map);
        return resultMap;
    }

    @Override
    public Map<String, Object> queryMerchNo(Map<String, Object> map) throws Exception {
        Map resultMap = dao.selectOneMap("MerchantManagementMapper.MerchantManagementInqMapper", map);
        return resultMap;
    }

    @Override
    public Map<String, Object> queryBlackCard(Map<String, Object> map) throws Exception {
        Map resultMap = dao.selectOneMap("abEqupMentMapper.blackListInqMapper", map);
        return resultMap;
    }

    @Override
    public Map<String, Object> queryMerchFeeInfo(Map<String, Object> map) throws Exception {
        Map resultMap = dao.selectOneMap("PosfeeManagementMapper.PosfeeManagementInqService", map);
        return resultMap;
    }

    @Override
    public int insertAbposh(Map<String, Object> map) throws Exception {
        int re = dao.insertOneByMap("abEqupMentMapper.abPosHistoryAdd", map);
        return re;
    }
}
