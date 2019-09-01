/**
 * Project Name:clb-provider
 * File Name:FxGxQueryHistoryListServiceImpl.java
 * Package Name:com.clps.fx.service.impl
 * Date:2017年1月13日下午2:20:13
 * Copyright (c) 2017, christ@163.com All Rights Reserved.
 *
*/

package com.clps.fx.service.impl;

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
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.pojo.FxPersonalExPo;
import com.clps.fx.pojo.FxPersonalLimitPo;
import com.clps.fx.service.FxGxQueryHistoryInqAndListService;
import com.clps.gb.service.TxnJourGenService;

/**
 * ClassName:FxGxQueryHistoryListServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD 个人结购汇历史列表/详细查询.. <br/>
 * CLB-FX-GXTRNLG-LST,CLB-FX-GXTRNLG-INQ
 * Date:     2017年1月13日 下午2:20:13 <br/>
 * @author   christ.guan
 * @version  
 * @since    JDK 1.8
 * @see 	 
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FxGxQueryHistoryInqAndListServiceImpl extends BaseService implements FxGxQueryHistoryInqAndListService{
	//错误码
	private static final String ERR_NORMAL = "0000";
	private static final String ERR_DUPLICATE = "0001";
	private static final String ERR_UNHANDLED_EXCEPTION = "9999";
	// The returning map
    Map<String, Object> returnMap = new HashMap<>();
    // 自动生成流水号服务
    @Reference(version = "1.0.0")
    private TxnJourGenService GBJour;
    
    /**
     *  fx-个人结购汇历史列表（服务号：CLB-FX-GXTRNLG-LST）.
     *@see com.clps.fx.service.FxGxQueryHistoryInqAndListService#acctIndividualList(java.util.Map)
     * @param map 个人结购汇历史列表查询条件
     *      <br> 包括
     *      <br> (1) CARD_NBR 交易卡号
     *      <br> (2) Tran_type 交易类型
     * @return map 查询结果
     * @throws Exception
     */
	@Transactional(readOnly = true)//只读事务
	@Override
	public Map<String, Object> acctIndividualList(Map<String, Object> map) throws Exception {
		
		log.info("fx-个人结购汇历史列表");
		// TODO Auto-generated method stub
		// 处理日期范围
		QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
		// 查询总条数
		Long total = (Long) dao.selectOneObject("FxGxQueryHistoryInqAndListMapper.demoQueryListService_count", map);

		//TODO 插入 
		map = QueryListUtils.changeInputData(map,total);
		// 查询总数据
		List<Map<String, Object>> list = dao.selectListMap("FxGxQueryHistoryInqAndListMapper.acctIndividualList", map);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("data_1", "独立数据1");
		data.put("data_2", "独立数据2");
		data.put("data_3", "独立数据3");

		//TODO 最后一个data参数需要改为 QueryListUtils.createPageDataMap(map,data,total);
		Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,data,total));
		// 返回数据
		return reMap;
	}

	/**
	* TODO 个人结购汇详细查询（服务号：CLB-FX-GXTRNLG-INQ）.
	* @see com.clps.fx.service.FxGxQueryHistoryInqAndListService#acctIndividualInq(java.util.Map)
    *
    * @param map 个人结购汇历史列表查询条件
    *      <br> 包括
    *      <br> (1) Report_NBR 报表序号
    * @return map 查询结果
    *      <br>错误码：
    *      <br>(1) 0000 = 输入的报表序号有误
    *      <br>(2) 0001 = 输入的报表序号有误
    *      <br>(3) 0002 = 数据库查询失败
    *      <br>(4) 0003 = 失败
    *      <br>(5) 0004 = 没有传入值
    * @throws Exception
    */
	@Transactional(readOnly = true)//只读事务
	@Override
	public FxPersonalExPo acctIndividualInq(FxPersonalExPo fxperex) throws Exception {
		log.info("fx-个人结购汇详细查询");
		return (FxPersonalExPo) dao.selectOneObject("FxGxQueryHistoryInqAndListMapper.perExInq", fxperex);
	}
	
	 @SuppressWarnings({ "unchecked", "unused" })
	 @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
	 @Override
		public Map<String, Object> personalTransLogAdd(FxPersonalExPo fxperex) throws Exception {
			
			// 记录日志
		    log.info("调用单条添加服务实现");
		    //调用GB自动生成服务 生成流水号
	        Map<String, Object> gbmap = new HashMap<String,Object>();
	        gbmap.put("create_user", "fxacex");
	        gbmap.put("update_user", "fxacex");
	        gbmap.put("initial", "0001");
	        gbmap.put("length", "19");
	        gbmap=GBJour.txnJourGen(gbmap);
	        fxperex.setReport_nbr(gbmap.get("jour_nbr").toString());
	        fxperex.setTran_date(DateTimeUtils.changeToDate());
	        fxperex.setTran_time(DateTimeUtils.changeToTime());
		    int a = dao.insertOneByObject("FxGxQueryHistoryInqAndListMapper.perExAdd", fxperex);
		    if(1 != a){
	            returnMap.put("resp_code", ERR_UNHANDLED_EXCEPTION);
	            return returnMap;
		    }else{
	        	returnMap = MapAndObjectUtils.copyPropertiesToMap(fxperex, returnMap);
	            returnMap.put("resp_code", ERR_NORMAL);
	            return returnMap;
		    }
		}

}

