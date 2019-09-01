package com.clps.fm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fm.pojo.FmActEtr2Po;
import com.clps.fm.service.FmAcctEntryQueryDetail;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmAcctEntryQueryDetailImpl extends BaseService implements FmAcctEntryQueryDetail{
    @Transactional(readOnly = true)//只读事务
    @Override
    public FmActEtr2Po queryAcctEntryDetail(FmActEtr2Po fm2Po) throws Exception {
        // 记录日志
        log.info("调用会计分录单条查询服务实现");
        return (FmActEtr2Po) dao.selectOneObject("FmINQMapper.QueryFmAcctEntryDetail",fm2Po);
    }
}
