/**
 * @author Neil Ding
 * @Time：2017年1月17日 上午9:44:42
 * @version 1.0  
 */
package com.clps.pb.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.pb.service.PbTranResvLstInqService;

/**
 * @author Neil Ding
 * @Time：2017年1月17日 上午9:44:42
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class TranResvLstInqServiceImpl extends BaseService implements PbTranResvLstInqService {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.clps.pb.service.PbTranResvLstInqService#tranResvQueryLstService(java.
	 * util.Map)
	 */
	@Transactional(readOnly = true) // 只读事务
	@Override
	public List<Map<String, Object>> tranResvQueryLstService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");

		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "cust_number", "tran_stat");
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("tranResvLstInqMapper.tranResvQueryLstService", map);
		// 返回数据
		return  list;
	}

}
