package com.clps.dp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.pojo.DpGxlrebkfPo;
import com.clps.dp.service.GxwflmInqService;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class GxwflmInqServiceImpl extends BaseService implements GxwflmInqService{
	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());

		@SuppressWarnings("unchecked")
		@Transactional(readOnly = true)//只读事务
		@Override
		public Map<String, Object> acctGxwflmListInqService(Map<String, Object> map) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("大额流失客户列表查询服务实现");
			// 处理日期范围
			//QueryListUtils.changeTimeSearch(map, "update_time", "create_time","start_date","end_date");
			Long total = (Long) dao.selectOneObject("dpGxwflmInqMapper.dpGxwflmCount", map);
			List<Map<String, Object>> list = dao.selectListMap("dpGxwflmInqMapper.dpGxwflmListInqMapper", map);
			// 处理返回
			Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
			// 返回数据
			return reMap;
		}
		@SuppressWarnings("unchecked")
		@Transactional(readOnly = true)//只读事务
		@Override
		public Map<String, Object> acctGxwflmInqService(DpGxlrebkfPo gxlrebkf) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用大额流失客户详细查询服务实现");
			return (Map<String, Object>) dao.selectOneObject("dpGxwflmInqMapper.dpGxwflmInq", gxlrebkf);
		}
			
}
