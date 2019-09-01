package com.clps.ab.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.demo.service.DemoService;
import com.clps.ab.service.EqupMentService;
import com.clps.gb.service.TxnJourGenService;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Service(version = "1.0.1") // 分布式服务注解和版本号
public class EqupMentServiceImpl extends BaseService implements EqupMentService{
	@Reference(version = "1.0.0") // dubbo的服务注解,内有版本号
	private TxnJourGenService tjgservice;//生成pos_no
	//收单费用信息添加服务
	@SuppressWarnings("unchecked")
	public int EqupMentAddService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("信用卡收单终端信息添加服务实现");
		//日期
		Date date=new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		map.put("create_time", df.format(date));
		map.put("update_time", df.format(date));
		Map<String,Object> posmap=new HashMap<String, Object>();
		posmap.put("initial", map.get("mcc_code"));
		posmap.put("length", 15);
		posmap.put("create_user", map.get("create_user"));
		posmap.put("update_user", map.get("update_user"));
		posmap=tjgservice.txnJourGen(posmap);
		if(posmap.get("successful").equals("false"))
		{
			log.info("终端序号pos_no创建失败");
			return 0;
		}
		map.put("pos_no", posmap.get("jour_nbr"));
		return dao.insertOneByMap("abEqupMentMapper.EqupMentAddMapper", map);
	}
	
	//收单费用信息修改服务
	@SuppressWarnings("unchecked")
	public int EqupMentUpdService(Map<String, Object> map) throws Exception {
		log.info("信用卡收单终端信息维护服务实现");
		map.put("update_time", DateTimeUtils.nowToSystem());
		return dao.updateByMap("abEqupMentMapper.EqupMentUpdMapper", map);
	}
	
	//信用卡收单终端信息查询服务
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public Map<String, Object> EqupMentInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用信用卡收单终端信息查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("abEqupMentMapper.EqupMentInqMapper", map);
	}
	
	//信用卡收单终端信息清单查询
	public Map<String, Object> EqupMentListInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用信用卡收单终端信息清单查询服务实现");
		Long total = (Long) dao.selectOneObject("abEqupMentMapper.ListCount", map);
		List<Map<String, Object>> list = dao.selectListMap("abEqupMentMapper.EqupMentListInqMapper", map);
		// 处理返回
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, null);
		// 返回数据
		return reMap;
	}

}


