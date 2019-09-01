/**
 * 
 */
package com.clps.fx.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.fx.pojo.FxCashExPo;
import com.clps.fx.pojo.FxPersonalAcctOppPo;
import com.clps.fx.pojo.FxPersonalAcctPo;
import com.clps.fx.service.FxPersonalAcct;

/**
 * TODO: Descriptions here
 * 
 * @author TODO: leo.wang
 *
 * 2017-05-07 下午2:38:20
 *
 * @version v1.0
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxPersonalAcctServiceImpl extends BaseService implements FxPersonalAcct{
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public FxPersonalAcctOppPo perAcctOppInq(FxPersonalAcctOppPo fxperacctopp) throws Exception {
		
		// 记录日志
	    log.info("调用单条查询服务实现");
	    return (FxPersonalAcctOppPo) dao.selectOneObject("personalAcct.perAcctOppInq", fxperacctopp);
//	    FxPersonalAcctOppPo fxperacctopp1 = new FxPersonalAcctOppPo();
//	    fxperacctopp1 = (FxPersonalAcctOppPo) dao.selectOneObject("personalAcct.perAcctOppInq", fxperacctopp);
//	    if(fxperacctopp1 == null){
//	    	return fxperacctopp;
//	    }else{
//	    	return fxperacctopp1;
//	    }
		//return (FxPersonalAcctOppPo) dao.selectOneObject("personalAcct.perAcctOppInq", fxperacctopp);
	}
	
	 @SuppressWarnings({ "unchecked", "unused" })
	 @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	 @Override
		public FxPersonalAcctOppPo perAcctOppAdd(FxPersonalAcctOppPo fxperacctopp) throws Exception {
			
			// 记录日志
		    log.info("调用单条添加服务实现");
			int re = dao.insertOneByObject("personalAcct.perAcctOppAdd", fxperacctopp);
			if (re == 1) {
				// 插入了一条数据,返回插入数据
				return fxperacctopp;
			} else {
				// 没有插入,返回空
				return null;
			}
		}
	 
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = true)//只读事务
		@Override
		public FxPersonalAcctPo perAcctInq(FxPersonalAcctPo fxperacct) throws Exception {
			
			// 记录日志
		    log.info("调用单条查询服务实现");
			return (FxPersonalAcctPo) dao.selectOneObject("personalAcct.perAcctInq", fxperacct);
		}
		
		 @SuppressWarnings({ "unchecked", "unused" })
		 @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
		 @Override
			public FxPersonalAcctPo perAcctAdd(FxPersonalAcctPo fxperacct) throws Exception {
				
				// 记录日志
			    log.info("调用单条添加服务实现");
			    fxperacct.setCreate_time(DateTimeUtils.changeToDate());
			    fxperacct.setOpen_date(DateTimeUtils.changeToDate());
			    fxperacct.setOpen_time(DateTimeUtils.changeToTime());
			    fxperacct.setEnd_date("9999-01-01");
				int re =  dao.insertOneByObject("personalAcct.perAcctAdd", fxperacct);
				if (re == 1) {
					// 插入了一条数据,返回插入数据
					return fxperacct;
				} else {
					// 没有插入,返回空
					return null;
				}
			}
}
