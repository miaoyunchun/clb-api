/**
 * Project Name:clb-provider
 * File Name:FtCheckServiceImpl.java
 * Package Name:com.clps.ft.service.impl
 * Date:2016年12月31日上午10:01:03
 * Copyright (c) 2016, tsqking@163.com All Rights Reserved.
 *
*/

package com.clps.ft.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.data.dao.Dao;
import com.clps.core.common.service.BaseService;
import com.clps.core.common.util.JsonResponseUtils;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.dp.pojo.DpBalancePo;
import com.clps.dp.service.BalanceInqService;
import com.clps.dp.service.BalanceRedService;
import com.clps.dp.service.CardAcctInqService;
import com.clps.ft.service.FtPayCheckService;
import com.clps.ft.service.LarSmaPayQueryService;

/**
 * ClassName:FtCheckServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年12月31日 上午10:01:03 <br/>
 * 
 * @author Charles
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
@SuppressWarnings("all")
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FtPayCheckServiceImpl extends BaseService implements FtPayCheckService {
	// 获得账户查询servic层对象
	@Reference(version = "1.0.0")
	private CardAcctInqService acctInqService;
	// 获得余额查询servic层对象
	@Reference(version = "1.0.0")
	private BalanceInqService balanceInqService;
	// 获得DP扣款服务对象
	@Reference(version = "1.0.0")
	private BalanceRedService balanceRedService;
	//leo update 2017/04/25
	private DpBalancePo dpbalancered;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class) // 非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> editService(Map<String, Object> map) throws Exception {
		log.info("调用修改服务");
		map.put("CHECK_DATE", DateTimeUtils.changeFormat(DateTimeUtils.nowToSystem(), "yyyyMMdd"));
		map.put("CHECK_TIME", DateTimeUtils.changeFormat(DateTimeUtils.nowToSystem(), "HH24:mm"));
		// 创建一个存放返回码的map
		Map<String, Object> resMap = new HashMap();
		// 创建一个存放调用dp模块所需参数的map
		Map<String, Object> dpMap = new HashMap();
		// 通过主键查询
		Map<String, Object> selectMap = (Map<String, Object>) dao.selectOneMap("FtPayCheckMapper.selectService", map);
		// 判断通过主键能否查询到相应的值
		if (selectMap == null) {
			resMap.put("resp_code", "ERROR_DONT_EXIT");
			return resMap;
		}
		// 判断空值(除收款人信息以外的所有信息填写是否都有值)
		if (selectMap.get("REC_ACCT") == null || selectMap.get("TXN_JOUR") == null
				|| selectMap.get("TXN_STATUS") == null || selectMap.get("TXN_DATE") == null
				|| selectMap.get("TXN_CODE") == null || selectMap.get("BUSINESS_TYPE") == null
				|| selectMap.get("DEDUC_CARD_NO") == null || selectMap.get("DEDUC_NAME") == null
				|| selectMap.get("DEDUC_BANK_CODE") == null || selectMap.get("DEDUC_BANK_NAME") == null
				|| selectMap.get("DEDUC_BANK_NUMBER") == null || selectMap.get("PAY_PASSWORD") == null
				|| selectMap.get("TRANS_AMT") == null || selectMap.get("TRANS_CCY") == null
				|| selectMap.get("GIRO_WAY") == null || selectMap.get("TRANS_TYPE") == null
				|| selectMap.get("VOUCHER_KIND") == null || selectMap.get("CHARGE_TYPE") == null
				|| selectMap.get("SERVICE_CHARGE_FUNC") == null || selectMap.get("SERVICE_CHARGE_AMT") == null
				|| selectMap.get("RECORD_ID") == null || selectMap.get("CHECK_ID") == null
				|| selectMap.get("CHECK_RESULT") == null || selectMap.get("REJECT_REASON") == null
				|| selectMap.get("CHECK_DATE") == null || selectMap.get("CHECK_TIME") == null
				|| selectMap.get("Remark") == null || selectMap.get("ACCUNT_WAY") == null) {
			resMap.put("resp_code", "ERROR_INPUT_EMPTY");
			return resMap;
		}
		// 从查询返回的数据中拿到调用dp扣款服务所需要的项
		dpMap.put("card_number", map.get("CARD_NUMBER"));
		dpMap.put("card_pin", selectMap.get("PAY_PASSWORD"));
		dpMap.put("money_red", selectMap.get("TRANS_AMT"));
		dpMap.put("curr_balance", selectMap.get("ACCT_CURR_BAL"));
		// 判断扣款人账户是否存在
		if (selectMap.get("DEDUC_CARD_NO") == null) {
			resMap.put("resp_code", "账户不存在");
			return resMap;
		}

		// 判断汇划途径为2，且为小额贷记业务
		if (selectMap.get("TXN_CODE").equals("1416") && selectMap.get("GIRO_WAY").equals("2")) {
			// 通过卡号获得关联账户号
			Map<String, Object> queryAssociaIdMap = new HashMap<>();
			queryAssociaIdMap.put("card_number", map.get("CARD_NUMBER"));
			//Map<String, Object> acctMsgInq = acctInqService.cardNoPinInq(queryAssociaIdMap);
			//resMap.put("ASSOCIATE_ACCT_ID", acctMsgInq.get("associate_acct_id"));
			// 获得账户号
			Map<String, Object> queryBalMap = new HashMap<>();
			queryBalMap.put("acct_nbr", selectMap.get("REC_ACCT"));
			// 通过账户号获得账户余额和客户号
			/*Map<String, Object> acctBalMap = balanceInqService.acctBalQueryService(queryBalMap);
			resMap.put("CUST_NBR ", acctBalMap.get("cust_number"));
			resMap.put("ACCT_CURR_BAL", acctBalMap.get("acct_curr_bal"));leo*/

			// 判断余额是否充足，比较余额和本次交易金额
			if (Double.parseDouble((String) map.get("ACCT_CURR_BAL")) <= Double
					.parseDouble((String) selectMap.get("TRANS_AMT"))) {
				resMap.put("resp_code", "余额不足");
				return resMap;
			}
			// 判断金额是否小于5万
			if (Double.parseDouble((String) map.get("TRANS_AMT")) > 50000.00) {
				resMap.put("resp_code", "金额超过限额");
				return resMap;
			}

			// 判断扣款账户是否和系统记录一致
			if (!map.get("card_number").equals(selectMap.get("DEDUC_CARD_NO"))) {
				map.put("ACCUNT_WAY", 1);
				resMap.put("resp_code", "请手动入账");
				return resMap;
			}
			map.put("ACCUNT_WAY", 2);

			// 如果五项同时成立
			if (selectMap.get("REC_ACCT") != null && selectMap.get("TXN_JOUR") != null
					&& selectMap.get("TXN_STATUS") != null && selectMap.get("TXN_DATE") != null
					&& selectMap.get("TXN_CODE") != null && selectMap.get("BUSINESS_TYPE") != null
					&& selectMap.get("DEDUC_CARD_NO") != null && selectMap.get("DEDUC_NAME") != null
					&& selectMap.get("DEDUC_BANK_CODE") != null && selectMap.get("DEDUC_BANK_NAME") != null
					&& selectMap.get("DEDUC_BANK_NUMBER") != null && selectMap.get("PAY_PASSWORD") != null
					&& selectMap.get("TRANS_AMT") != null && selectMap.get("TRANS_CCY") != null
					&& selectMap.get("GIRO_WAY") != null && selectMap.get("TRANS_TYPE") != null
					&& selectMap.get("VOUCHER_KIND") != null && selectMap.get("CHARGE_TYPE") != null
					&& selectMap.get("SERVICE_CHARGE_FUNC") != null && selectMap.get("SERVICE_CHARGE_AMT") != null
					&& selectMap.get("RECORD_ID") != null && selectMap.get("CHECK_ID") != null
					&& selectMap.get("CHECK_RESULT") != null && selectMap.get("REJECT_REASON") != null
					&& selectMap.get("CHECK_DATE") != null && selectMap.get("CHECK_TIME") != null
					&& selectMap.get("Remark") != null && selectMap.get("ACCUNT_WAY") != null
					&& selectMap.get("DEDUC_CARD_NO") != null
					&& Double.parseDouble((String) map.get("ACCT_CURR_BAL")) >= Double
							.parseDouble((String) selectMap.get("TRANS_AMT"))
					&& Double.parseDouble((String) map.get("TRANS_AMT")) < 50000.00
					&& map.get("DEDUC_NAME").equals(selectMap.get("DEDUC_NAME"))) {
				map.put("TXN_STATUS", 2);
				// 更新数据
				dao.updateByMap("FtPayCheckMapper.updateService", map);
				// 同时调用DP模块进行扣款。调用账务程序进行记账。
				//leo uopdate 2017/04/25 
				//balanceRedService.havecardBalRedService(dpMap);
				balanceRedService.havecardBalRedService(dpbalancered);
				// 调用报文发起程序。

				resMap.put("resp_code", "复核成功");
				return resMap;
			} else {
				map.put("TXN_STATUS", 2);
				resMap.put("resp_code", "复核不通过");
				return resMap;
			}
		}

		if (selectMap.get("TXN_CODE").equals("1417")) {
			// 判断金额是否小于5万
			if (Double.parseDouble((String) map.get("TRANS_AMT")) > 50000.00) {
				resMap.put("resp_code", "金额超过限额");
				return resMap;
			}
			// 判断三项是否同时成立
			if (selectMap.get("REC_ACCT") != null && selectMap.get("TXN_JOUR") != null
					&& selectMap.get("TXN_STATUS") != null && selectMap.get("TXN_DATE") != null
					&& selectMap.get("TXN_CODE") != null && selectMap.get("BUSINESS_TYPE") != null
					&& selectMap.get("DEDUC_CARD_NO") != null && selectMap.get("DEDUC_NAME") != null
					&& selectMap.get("DEDUC_BANK_CODE") != null && selectMap.get("DEDUC_BANK_NAME") != null
					&& selectMap.get("DEDUC_BANK_NUMBER") != null && selectMap.get("PAY_PASSWORD") != null
					&& selectMap.get("TRANS_AMT") != null && selectMap.get("TRANS_CCY") != null
					&& selectMap.get("GIRO_WAY") != null && selectMap.get("TRANS_TYPE") != null
					&& selectMap.get("VOUCHER_KIND") != null && selectMap.get("CHARGE_TYPE") != null
					&& selectMap.get("SERVICE_CHARGE_FUNC") != null && selectMap.get("SERVICE_CHARGE_AMT") != null
					&& selectMap.get("RECORD_ID") != null && selectMap.get("CHECK_ID") != null
					&& selectMap.get("CHECK_RESULT") != null && selectMap.get("REJECT_REASON") != null
					&& selectMap.get("CHECK_DATE") != null && selectMap.get("CHECK_TIME") != null
					&& selectMap.get("Remark") != null && selectMap.get("ACCUNT_WAY") != null
					&& selectMap.get("DEDUC_CARD_NO") != null
					&& Double.parseDouble((String) map.get("TRANS_AMT")) < 50000.00) {
				map.put("TXN_STATUS", 2);
				// 更新数据
				dao.updateByMap("FtPayCheckMapper.updateService", map);
				// 同时调用DP模块进行扣款。调用账务程序进行记账（借：活期存款贷：联行往账）。
				//leo uopdate 2017/04/25 
				//balanceRedService.havecardBalRedService(dpMap);
				balanceRedService.havecardBalRedService(dpbalancered);
				// 调用报文发起程序。

				resMap.put("resp_code", "复核成功");
				return resMap;
			} else {
				map.put("TXN_STATUS", 2);
				resMap.put("resp_code", "复核不通过");
				return resMap;
			}
		}

		if (selectMap.get("TXN_CODE").equals("1415")) {
			// 判断余额是否充足，比较余额和本次交易金额
			if (Double.parseDouble((String) map.get("ACCT_CURR_BAL")) <= Double
					.parseDouble((String) selectMap.get("TRANS_AMT"))) {
				resMap.put("resp_code", "余额不足");
				return resMap;
			}
			// 判断金额是否大于5万
			if (Double.parseDouble((String) map.get("TRANS_AMT")) < 50000.00) {
				resMap.put("resp_code", "金额低于最低限额");
				return resMap;
			}
			// 判断扣款账户是否和系统记录一致
			if (!map.get("card_number").equals(selectMap.get("DEDUC_CARD_NO"))) {
				map.put("ACCUNT_WAY", 1);
				resMap.put("resp_code", "请手动入账");
				return resMap;
			}
			map.put("ACCUNT_WAY", 2);

			// 若五者都满足
			if (selectMap.get("REC_ACCT") != null && selectMap.get("TXN_JOUR") != null
					&& selectMap.get("TXN_STATUS") != null && selectMap.get("TXN_DATE") != null
					&& selectMap.get("TXN_CODE") != null && selectMap.get("BUSINESS_TYPE") != null
					&& selectMap.get("DEDUC_CARD_NO") != null && selectMap.get("DEDUC_NAME") != null
					&& selectMap.get("DEDUC_BANK_CODE") != null && selectMap.get("DEDUC_BANK_NAME") != null
					&& selectMap.get("DEDUC_BANK_NUMBER") != null && selectMap.get("PAY_PASSWORD") != null
					&& selectMap.get("TRANS_AMT") != null && selectMap.get("TRANS_CCY") != null
					&& selectMap.get("GIRO_WAY") != null && selectMap.get("TRANS_TYPE") != null
					&& selectMap.get("VOUCHER_KIND") != null && selectMap.get("CHARGE_TYPE") != null
					&& selectMap.get("SERVICE_CHARGE_FUNC") != null && selectMap.get("SERVICE_CHARGE_AMT") != null
					&& selectMap.get("RECORD_ID") != null && selectMap.get("CHECK_ID") != null
					&& selectMap.get("CHECK_RESULT") != null && selectMap.get("REJECT_REASON") != null
					&& selectMap.get("CHECK_DATE") != null && selectMap.get("CHECK_TIME") != null
					&& selectMap.get("Remark") != null && selectMap.get("ACCUNT_WAY") != null
					&& selectMap.get("DEDUC_CARD_NO") != null
					&& Double.parseDouble((String) map.get("ACCT_CURR_BAL")) >= Double
							.parseDouble((String) selectMap.get("TRANS_AMT"))
					&& map.get("DEDUC_NAME").equals(selectMap.get("DEDUC_NAME"))
					&& (Double.parseDouble((String) map.get("TRANS_AMT")) > 50000.00)) {
				map.put("TXN_STATUS", 2);
				// 更新数据
				dao.updateByMap("FtPayCheckMapper.updateService", map);
				// 同时调用DP模块进行扣款。调用账务程序进行记账（借：活期存款贷：联行往账）。
				//leo uopdate 2017/04/25 
				//balanceRedService.havecardBalRedService(dpMap);
				balanceRedService.havecardBalRedService(dpbalancered);
				// 调用报文发起程序。

				resMap.put("resp_code", "复核成功");
				return resMap;

			} else {
				map.put("TXN_STATUS", 2);
				resMap.put("resp_code", "复核不通过");
				return resMap;
			}
		}

		return resMap;
	}

}
