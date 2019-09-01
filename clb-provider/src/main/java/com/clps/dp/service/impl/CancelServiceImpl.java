package com.clps.dp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.dp.pojo.DpGxlrebkfPo;
import com.clps.dp.pojo.DpGxregflfPo;
import com.clps.dp.service.CancelService;
import com.clps.rm.service.util.RmHelperUtil;
import com.clps.core.sys.util.MapAndObjectUtils;

/**
 * 分布式服务接口实现
 * 
 * @author blessing
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.1") // 分布式服务注解和版本号
public class CancelServiceImpl extends BaseService implements CancelService{
	 // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_DUPLICATE = "0001";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    // The returning map
    Map<String, Object> returnMap = new HashMap<>();
	
    
    // 日志对象	
	private Logger log = LoggerFactory.getLogger(getClass().getName());

	/**
	 * 大额销户需授权销户登记簿添加VBS-DP-GXWFAU-ADD
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> acctCancelService(DpGxlrebkfPo gxlrebkf) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		gxlrebkf.setWriteoff_date(DateTimeUtils.changeToDate());
		gxlrebkf.setWriteoff_time(DateTimeUtils.changeToTime());
		gxlrebkf.setCreate_time(DateTimeUtils.changeToTime());
		gxlrebkf.setUpdate_time(DateTimeUtils.changeToTime());
		int bkf=dao.insertOneByObject("dpCancelMapper.bkfAddMapper", gxlrebkf);
		if(bkf!=0)
			log.info("写入清户标志文件");
		returnMap = MapAndObjectUtils.copyPropertiesToMap(gxlrebkf, returnMap);
		return returnMap;
	}
	
	/**
	 * 写入清户标志文件（小额）VBS-DP-GXWFTG-ADD
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> writeCancelFlag(DpGxregflfPo gxregflf) throws Exception {
		// TODO Auto-generated method stub
		// 记录日志
		gxregflf.setWriteoff_date(DateTimeUtils.changeToDate());
		gxregflf.setWriteoff_time(DateTimeUtils.changeToTime());
		gxregflf.setCreate_time(DateTimeUtils.changeToTime());
		gxregflf.setUpdate_time(DateTimeUtils.changeToTime());
		 // Response code
        int re;
        
        try {
            re = dao.insertOneByObject("dpCancelMapper.flfAddMapper", gxregflf);
        } catch (DuplicateKeyException ex) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(gxregflf, returnMap);
            returnMap.put("resp_code", ERR_DUPLICATE);
            return returnMap;
        }
        if (re == 1) {
        	returnMap = MapAndObjectUtils.copyPropertiesToMap(gxregflf, returnMap);
            returnMap.put("resp_code", ERR_NORMAL);
            return returnMap;
        } else {
            // 没有插入,返回数据为空，返回值为ERR_UNHANDLED_EXCEPTION
            returnMap = new HashMap<>();
            returnMap.put("resp_code", ERR_UNHANDLED_EXCEPTION);
            return returnMap;
		
        }
	
}}
