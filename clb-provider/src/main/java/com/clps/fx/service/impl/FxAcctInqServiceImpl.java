package com.clps.fx.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.clps.cp.service.AcctNbrGenerateService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.fx.pojo.FxAcctPo;
import com.clps.fx.service.FxAcctInq;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.service.FxAcctInq;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.service.AcctOpenService;

/**
 * 分布式服务接口实现
 *
 * @author snow
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxAcctInqServiceImpl extends BaseService implements FxAcctInq{

	
    // 分布式服务方法使用
    @Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
    private CardAcctInqService acctMsgInq;

    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    public Map<String, Object> publicAcctInq(FxAcctPo fxacct) throws Exception {
    	 DpAcctPo dpacct = new  DpAcctPo();
        // 记录日志
        log.info("外汇账户查询");
        Map<String, Object> reslutMap = new HashMap<String, Object>();
        reslutMap = (Map<String, Object>) dao.selectOneObject("FxAcctOpen.fxPublicAcctInq", fxacct);
        dpacct.setAcct_nbr(fxacct.getAcct_nbr());
        //Map<String, Object> dpacctMap = acctMsgInq.acctInfoInq(dpacct);
        dpacct = acctMsgInq.acctInfoInq(dpacct);
        reslutMap.put("prod_id",dpacct.getProd_id());
        reslutMap.put("prod_spec_date",dpacct.getProd_spec_date());
        reslutMap.put("associate_org_id",dpacct.getAssociate_org_id());
        reslutMap.put("add_by",dpacct.getAdd_by());
        reslutMap.put("subject_no",dpacct.getSubject_no());

        return reslutMap;
    }

}
