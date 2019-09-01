package com.clps.dp.service.impl;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.demo.pojo.DemoPo;
import com.clps.dp.pojo.DpAcctPo;
import com.clps.dp.pojo.DpCardPo;
import com.clps.dp.service.CardAcctInqService;



/**
 * 分布式服务接口实现
 * 
 * @author leo
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CardAcctMsgInqImpl extends BaseService implements CardAcctInqService{

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public DpCardPo cardNoPinInq(DpCardPo dpcard) throws Exception {
		// TODO Auto-generated method stub
				// 记录日志
				log.info("调用单条查询服务实现");
				return  (DpCardPo)dao.selectOneObject("DpCardOpenMapper.cardInfoInqMappper", dpcard);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> acctMsgInq(Map<String, Object> map) throws Exception {
		// TODO Auto-generated method stub
				// 记录日志
				log.info("调用单条查询服务实现");
				return (Map<String, Object>) dao.selectOneMap("dpCardAcctInqMapper.acctMsgInqMapper", map);
	}
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public DpAcctPo acctInfoInq(DpAcctPo dpacct) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (DpAcctPo) dao.selectOneObject("acctOpenMapper.acctInfoInq", dpacct);
	}

}
