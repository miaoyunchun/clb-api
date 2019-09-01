package com.clps.ln.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.pojo.LnGxapply;
import com.clps.ln.pojo.LnGxauditPo;
import com.clps.ln.service.LnGxAuditService;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnGxAuditServiceImpl extends BaseService implements LnGxAuditService{

	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
	
    @SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> QueryOneGxAuditService(LnGxauditPo lnGxauditPo) throws Exception {
		// 记录日志
		log.info("展期申请簿查询服务实现");
		return (Map<String, Object>) dao.selectOneObject("lngxauditMapper.QueryOneGxAuditService",lnGxauditPo );
	}

	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public int editLnGxApplyService(LnGxapply lnGxapply) throws Exception {
		log.info("调用展期申请簿更新服务实现");
		//map.put("update_time", DateTimeUtils.nowToSystem());
		lnGxapply.setUpdate_time(DateTimeUtils.nowToSystem());
		return dao.updateByObject("lngxauditMapper.editLnGxApplyService", lnGxapply);
	}

	@Override
	public Map<String, Object> QueryListGxAuditService(Map<String, Object> map) throws Exception {
		 log.info("LN - 展期审批历史列表查询");

	        // 处理日期范围
	        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
	        // 查询总条数
	        Long total = (Long) dao.selectOneObject("lngxauditMapper.queryLnListofByCustomerNo_count", map);
	        System.out.println(total);
	        // 查询总数据
	        List<Map<String, Object>> list = dao.selectListMap("lngxauditMapper.queryLnListofByCustomerNo", map);

	        // 返回map
	        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
	        if (total != 0) {
	            resultMap.put("gxcaudit_loan_nbr", list.get(list.size() - 1).get("gxcaudit_loan_nbr").toString());
	            resultMap.put("gxcaudit_audit_nbr", list.get(list.size() - 1).get("gxcaudit_audit_nbr").toString());
	            resultMap.put("gxcaudit_loan_ext_sts", list.get(list.size() - 1).get("gxcaudit_loan_ext_sts").toString());
	        } else {
	            resultMap.put("gxcaudit_loan_nbr", "");
	            resultMap.put("gxcaudit_audit_nbr", "");
	            resultMap.put("gxcaudit_loan_ext_sts", "");
	        }
	        return resultMap;
	}

}
