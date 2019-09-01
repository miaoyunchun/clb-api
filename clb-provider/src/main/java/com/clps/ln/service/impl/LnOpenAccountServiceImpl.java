package com.clps.ln.service.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.ln.pojo.LnAcctPo;
import com.clps.ln.service.LnOpenAccountService;

/**
 * * 贷款开户
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnOpenAccountServiceImpl extends BaseService implements LnOpenAccountService {
 
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public LnAcctPo openAccountService(LnAcctPo lnacctpo) throws Exception {
		// 记录日志
				log.info("调用贷款开户服务实现");
				/*map.put("last_by", "");
				map.put("create_time", DateTimeUtils.nowToSystem());
				map.put("update_time", DateTimeUtils.nowToSystem());*/
				lnacctpo.setLast_by("");
				lnacctpo.setLoan_open_date(DateTimeUtils.changeToDate());
				lnacctpo.setCreate_time(DateTimeUtils.nowToSystem());
				lnacctpo.setUpdate_time(DateTimeUtils.nowToSystem());
				int re = dao.insertOneByObject("openAccountMapper.LnOpenAccountService", lnacctpo);
				if (re == 1) {
					// 插入了一条数据,返回插入数据
					return lnacctpo;
				} else {
					// 没有插入,返回空
					return null;
				}
			}
	
	/**
	 * 
	 * 更新申请状态
	 */
	@Override
	public Map<String, Object> updateApplyStatusService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("LN - 更新申请状态 ");
		int re = dao.updateByMap("openAccountMapper.lnApplyStatusUpdate", map);
		if (re == 1) {
			// 更改了一条数据,返回更改数据
			return map;
		} else {
			// 没有更改,返回空
			return null;
		}
	}
	}


