package com.clps.pb.service.impl;

import java.text.DateFormat;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.pb.service.PbResvdpstUpdService;

/**
 * @author feilong.song
 * @Time：2017年1月3日 下午12:52:55
 * @version 1.0
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PbResvdpstUpdServiceImpl extends BaseService implements PbResvdpstUpdService{

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	public int resvdpstUdpService(Map<String, Object> map) throws Exception {
		log.info("调用更新服务实现");
		//获取修改的预约日期
		long rsv_date = DateFormat.getDateInstance().parse((String) map.get("rsv_date")).getTime();
		//获取更新当前的日期
		String date_upd_str= DateTimeUtils.nowToSystem();
		long date_upd = DateFormat.getDateInstance().parse(date_upd_str).getTime();
		//判断修改的预约日期是否大于当前的日期
		if(rsv_date < date_upd)
		{
			//返回2表示修改的预约日期rsv_date小于当前时间
			return 2;
		}
		if((!map.get("rsv_open_mthd").equals("T")))
		{
			//返回3表示开户方式rsv_open_mthd!="T"
			return 3;
		}
		map.put("update_time",date_upd_str );
		return dao.updateByMap("pbResvdpstMapper.updateService", map);
	}

	@Override
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)//只读事务
	public Map<String, Object> account2DpInqService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("调用单条查询服务实现");
		return (Map<String, Object>) dao.selectOneMap("pbResvdpstMapper.selectService", map);
	}

}
