package com.clps.cp.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fm.pojo.FmActEtr2Po;
import com.clps.fm.pojo.FmActEtrPo;
import com.clps.fm.service.FmAcctEntryQueryByKey;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FmAcctEntryQueryByKeyImpl extends BaseService implements FmAcctEntryQueryByKey {
    @Transactional(readOnly = true)//只读事务
    @Override
    public FmActEtrPo queryAcctEntryByKey(FmActEtrPo fmactetr) throws Exception {
        // 记录日志
        log.info("调用单条查询会计分录服务实现");
        return (FmActEtrPo) dao.selectOneObject("FmAcctEntryByKeyMapper.QueryAcctEntryByKey",fmactetr);
    }
}
