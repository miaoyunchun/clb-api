package com.clps.cp.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.cp.pojo.CpOrgparm;
import com.clps.cp.service.OrganizationParameterService;


/**
 * 分布式服务接口实现
 * 
 * @author liuchen
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号

public class OrgParameterServiceImpl  extends BaseService implements OrganizationParameterService{

	// 日志对象
		private Logger log = LoggerFactory.getLogger(getClass().getName());

		@Override
		public CpOrgparm queryOrgParameterService(CpOrgparm cpOrg) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用单条查询服务实现");
			return (CpOrgparm) dao.selectOneObject("cpOrgParameterMapper.queryCpOrgParameterMapper", cpOrg);
		}


		@Override
		public int editOrgParameterService(CpOrgparm cpOrg) throws Exception {
			// TODO Auto-generated method stub
			log.info("调用修改服务实现");
			cpOrg.setUpdate_time(DateTimeUtils.nowToSystem());
			return dao.updateByObject("cpOrgParameterMapper.editCpOrgParameterMapper", cpOrg);
		}

		
		
		@Override
		public CpOrgparm insertOrgParameterService(CpOrgparm cpOrg) throws Exception {
			// TODO Auto-generated method stub
			// 记录日志
			log.info("调用插入服务实现");
			cpOrg.setCreate_time(DateTimeUtils.nowToSystem());
			cpOrg.setUpdate_time(DateTimeUtils.nowToSystem());
			int re = dao.insertOneByObject("cpOrgParameterMapper.insertCpOrgParameterMapper", cpOrg);
			if (re == 1) {
				// 插入了一条数据,返回插入数据
				return cpOrg;
			} else {
				// 没有插入,返回空
				return null;
			}
		}

}
