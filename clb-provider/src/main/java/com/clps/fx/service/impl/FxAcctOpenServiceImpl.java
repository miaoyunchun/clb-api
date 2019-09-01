package com.clps.fx.service.impl;

import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Reference;
import com.clps.cp.service.AcctNbrGenerateService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.pojo.FxAcctPo;
import com.clps.fx.service.FxAcctOpen;
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
public class FxAcctOpenServiceImpl extends BaseService implements FxAcctOpen{
    @Reference(version = "1.0.0")
    private AcctOpenService AcctOpenAddService;
    
    @SuppressWarnings({ "unchecked", "unused" })
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    public FxAcctPo FxPublicAcctOpen(FxAcctPo fxacct,DpAcctPo dpacct) throws Exception {
        // 记录日志
        log.info("");
        //插入Dp账户表
        dpacct.setAcct_curr_bal(fxacct.getBalance());
        dpacct.setCreate_time(DateTimeUtils.changeToDate());
        dpacct.setUpdate_time(DateTimeUtils.changeToDate());
        Map<String,Object> map =AcctOpenAddService.AcctOpenAddService(dpacct);
        //插入FX账户表
        fxacct.setAcct_nbr(dpacct.getAcct_nbr());
        fxacct.setCreate_time(DateTimeUtils.changeToDate());
        fxacct.setUpdate_time(DateTimeUtils.changeToDate());
        int re = dao.insertOneByObject("FxAcctOpen.FxPublicAcctOpen", fxacct);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return fxacct;
		} else {
			// 没有插入,返回空
			return null;
		}


    }

}
