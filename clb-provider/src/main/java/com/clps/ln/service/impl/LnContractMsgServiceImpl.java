package com.clps.ln.service.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.ln.pojo.LnCheckContractPo;
import com.clps.ln.pojo.LnContractPo;
import com.clps.ln.service.LnContractMsgService;

/**
 * * 合同管理
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LnContractMsgServiceImpl extends BaseService implements LnContractMsgService {

    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    /**
     * LN - 增加客户合同信息
     * @param "合同信息字段" map
     * @return "合同信息字段" map
     * @throws Exception
     */
	@Override
	public Map<String, Object> lnContractAdd(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("LN - 合同信息录入");
		//map.put("lncntrct_open_date", DateTimeUtils.nowToSystem());
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("lnContractMsgMapper.insertLnContract", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	@Override
	public LnContractPo lnContractAddPo(LnContractPo lnProd) throws Exception {
		// 记录日志
		log.info("LN - 合同信息录入");
		lnProd.setCreate_time(DateTimeUtils.nowToSystem());
		lnProd.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.insertOneByObject("lnContractMsgMapper.insertLnContractPo", lnProd);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return lnProd;
		} else {
			// 没有插入,返回空
			return null;
		}
		
	}
	
    /**
     * LN - 查询客户合同信息
     * @param "合同信息  搜索条件 合同号" map
     * @return "合同信息" map
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> lnContractInq(Map<String, Object> map) throws Exception {
        log.info("LN - 贷款合同查询");
        return (Map<String, Object>) dao.selectOneMap("lnContractMsgMapper.selectLnContract", map);
    }
	@Override
	public LnContractPo lnContractInqPo(LnContractPo lnProd) throws Exception {
		log.info("LN - 贷款合同查询");
		return (LnContractPo) dao.selectOneObject("lnContractMsgMapper.selectLnContractPo", lnProd);
	}
	
	/**
     * LN - 修改客户合同信息
     * @param "合同更新信息" map
     * @return "更新结果" map
     * @throws Exception
     */
	@Override
	public Map<String, Object> lnContractUpd(Map<String, Object> map) throws Exception {
		log.info("LN - 贷款合同更新");
		// 更新前数据
       // Map<String, Object> origData = lnContractInq(map);
        // 检查更新前后数据是否有变动
        // 无变动则不更新直接返回
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.updateByMap("lnContractMsgMapper.updateLnContract", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
    /**
     * LN - 修改客户合同信息
     * @param "合同更新信息" map
     * @return "更新结果" map
     * @throws Exception
     */
	@Override
	public LnContractPo lnContractUpd(LnContractPo lnContractUpd) throws Exception {
		log.info("LN - 贷款合同更新");
		// 更新前数据
       // Map<String, Object> origData = lnContractInq(map);
        // 检查更新前后数据是否有变动
        // 无变动则不更新直接返回
		int re = dao.updateByObject("lnContractMsgMapper.updateLnContractPo", lnContractUpd);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return lnContractUpd;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	@Override
	public LnContractPo lnContractUpdPo(LnContractPo lnProd) throws Exception {
		log.info("LN - 贷款合同更新");
		lnProd.setUpdate_time(DateTimeUtils.nowToSystem());
		int re = dao.updateByObject("lnContractMsgMapper.updateLnContractPo", lnProd);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return lnProd;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	@Override
	public Map<String, Object> lnApplyStatusUpd(Map<String, Object> map) throws Exception {
		log.info("LN - 贷款合同更新");
		// 更新前数据
       // Map<String, Object> origData = lnContractInq(map);
        // 检查更新前后数据是否有变动
        // 无变动则不更新直接返回
		map.put("update_time", DateTimeUtils.nowToSystem());
		int re = dao.updateByMap("lnContractMsgMapper.updateApplyStatusService", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}
	
	
	
    /**
     * LN - 客户合同按客户号查询列表
     * @param "查询条件 客户号" map
     * @return "查询结果列表" map
     * @throws Exception
     */
	@Override
	public Map<String, Object> lnContractCustNOInq(Map<String, Object> map) throws Exception {
        log.info("LN - 贷款合同客户列表查询");

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        
        log.info("前参数Map："+map.toString());
        // 查询总条数
        Long total = (Long) dao.selectOneObject("lnContractMsgMapper.queryLnContractByCustomerNo_count", map);
        
      //TODO 插入 
		map = QueryListUtils.changeInputData(map,total);
		log.info("后参数Map："+map.toString());
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("lnContractMsgMapper.queryLnContractByCustomerNo", map);

        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
        if (total != 0) {
            resultMap.put("loan_nbr", list.get(list.size() - 1).get("loan_nbr"));
        } else {
            resultMap.put("loan_nbr", "");
        }
        return resultMap;
    }
	  /**
     * LN - 客户合同信息审核
     * @param "客户合同信息审核" map
     * @return "客户合同信息审核" map
     * @throws Exception
     */
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> lnContractCheck(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("LN - 合同信息审核");
		//map.put("lncntrct_open_date", DateTimeUtils.nowToSystem());
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		map.put("lnctrsts_intchk_date", DateTimeUtils.changeToDate());
		map.put("cntrct_status","2");
		int re = dao.insertOneByObject("lnContractMsgMapper.insertLnCheckContract", map);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return map;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	@Override
	public LnCheckContractPo lnContractCheckPo(LnCheckContractPo lnProd) throws Exception {
		// 记录日志
		log.info("LN - 合同信息审核");
		lnProd.setCreate_time(DateTimeUtils.nowToSystem());
		lnProd.setUpdate_time(DateTimeUtils.nowToSystem());
		lnProd.setLnctrsts_intchk_date(DateTimeUtils.changeToDate());
		lnProd.setLnctrsts_status("2");
		log.info(lnProd.toString());
		int re = dao.insertOneByObject("lnContractMsgMapper.insertLnCheckContractPo", lnProd);
		if (re == 1) {
			// 插入了一条数据,返回插入数据
			return lnProd;
		} else {
			// 没有插入,返回空
			return null;
		}
	}

	

}