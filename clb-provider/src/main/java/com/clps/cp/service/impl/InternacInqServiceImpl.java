package com.clps.cp.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.cp.service.InternacAcctInqService;
import com.clps.fx.pojo.InternacAcct;
import com.clps.fx.pojo.InternacCodePo;
/**
 * @author david
 * 2016年9月18日
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class InternacInqServiceImpl extends BaseService implements InternacAcctInqService{

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)//只读事务
    @Override
    public InternacAcct InternacAcctInq(InternacCodePo internaccode) throws Exception {
        // 记录日志
        log.info("调用内部户查询服务实现");
        //内部户代码查询
        internaccode = (InternacCodePo) dao.selectOneObject("internacAcctInqMapper.internacCodeInq", internaccode);
        //内部户查询
        InternacAcct internacacct = new InternacAcct();
        internacacct.setAcct_code(internaccode.getAcct_code());
        return (InternacAcct) dao.selectOneObject("internacAcctInqMapper.internacAcctInq", internacacct);
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)//只读事务
    @Override
    public InternacCodePo InternacAcctCodeInq(InternacCodePo internaccode) throws Exception {
        // 记录日志
        log.info("调用内部户查询服务实现");
        //内部户代码查询
        return (InternacCodePo) dao.selectOneObject("internacAcctInqMapper.internacCodeInq", internaccode);
        //内部户查询
/*        InternacAcct internacacct = new InternacAcct();
        internacacct.setAcct_code(internaccode.getAcct_code());
        return (InternacAcct) dao.selectOneObject("internacAcctInqMapper.internacAcctInq", internacacct);*/
    }
    
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)//只读事务
    @Override
    public InternacAcct InternacAcctQury(InternacAcct internacct) throws Exception {
        // 记录日志
        log.info("调用内部户查询服务实现");
        //内部户代码查询
        return (InternacAcct) dao.selectOneObject("internacAcctInqMapper.internacAcctInq", internacct);
        //内部户查询
/*        InternacAcct internacacct = new InternacAcct();
        internacacct.setAcct_code(internaccode.getAcct_code());
        return (InternacAcct) dao.selectOneObject("internacAcctInqMapper.internacAcctInq", internacacct);*/
    }

}
