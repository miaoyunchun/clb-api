/**
 * Project Name:clb-provider
 * File Name:FtServiceImpl.java
 * Package Name:com.clps.ft.service.impl
 * Date:2016年12月9日下午2:53:58
 * Copyright (c) 2016, tsqking@163.com All Rights Reserved.
 *
*/

package com.clps.ft.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.ft.service.FtPayInsertService;

/**
 * ClassName:FtServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * Date: 2016年12月9日 下午2:53:58 <br/>
 * 
 * @author Charles.Tan
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FtPayInsertServiceImpl extends BaseService implements FtPayInsertService {
	@Override
	public Map<String, Object> insertService(Map<String, Object> map) throws Exception {
		Map<String, Object> resMap = new HashMap<>();
		log.info("调用插入服务实现");
		// 生成交易代码
		if (map.get("BUSINESS_TYPE").equals("A100") && map.get("GIRO_WAY").equals("1")) {
			map.put("TXN_CODE", 1415);
		}
		if (map.get("BUSINESS_TYPE").equals("A100") && map.get("GIRO_WAY").equals("2")) {
			map.put("TXN_CODE", 1416);
		}
		if (map.get("BUSINESS_TYPE").equals("B100") && map.get("GIRO_WAY").equals("2")) {
			map.put("TXN_CODE", 1417);
		}

		// 手续费计算
		if (Double.parseDouble((String) map.get("TRANS_AMT")) <= 2000.00) {
			map.put("SERVICE_CHARGE_AMT", 2.00);
		}
		if (Double.parseDouble((String) map.get("TRANS_AMT")) > 2000.00
				&& Double.parseDouble((String) map.get("TRANS_AMT")) <= 5000.00) {
			map.put("SERVICE_CHARGE_AMT", 5.00);

		}
		if (Double.parseDouble((String) map.get("TRANS_AMT")) > 5000.00
				&& Double.parseDouble((String) map.get("TRANS_AMT")) <= 10000.00) {
			map.put("SERVICE_CHARGE_AMT", 10.00);

		}
		if (Double.parseDouble((String) map.get("TRANS_AMT")) > 10000.00
				&& Double.parseDouble((String) map.get("TRANS_AMT")) <= 50000.00) {
			map.put("SERVICE_CHARGE_AMT", 15.00);

		}
		if (Double.parseDouble((String) map.get("TRANS_AMT")) > 50000.00) {
			if ((Double.parseDouble((String) map.get("TRANS_AMT")) * 0.0003) < 50.00) {
				map.put("SERVICE_CHARGE_AMT", Double.parseDouble((String) map.get("TRANS_AMT")) * 0.0003);
			} else
				map.put("SERVICE_CHARGE_AMT", 50.00);
		}

		// 判断空值
		if (map.get("REC_ACCT") == null || map.get("TXN_JOUR") == null || map.get("TXN_STATUS") == null
				|| map.get("TXN_DATE") == null || map.get("TXN_CODE") == null || map.get("BUSINESS_TYPE") == null
				|| map.get("DEDUC_CARD_NO") == null || map.get("DEDUC_NAME") == null
				|| map.get("DEDUC_BANK_CODE") == null || map.get("DEDUC_BANK_NAME") == null
				|| map.get("DEDUC_BANK_NUMBER") == null || map.get("PAY_PASSWORD") == null
				|| map.get("RECV_BANK_CODE") == null || map.get("RECV_BANK_NAME") == null
				|| map.get("RECV_BANK_NUMBER") == null || map.get("RECV_CARD_NO") == null
				|| map.get("RECV_NAME") == null || map.get("TRANS_AMT") == null || map.get("TRANS_CCY") == null
				|| map.get("GIRO_WAY") == null || map.get("TRANS_TYPE") == null || map.get("VOUCHER_KIND") == null
				|| map.get("CHARGE_TYPE") == null || map.get("SERVICE_CHARGE_FUNC") == null
				|| map.get("SERVICE_CHARGE_AMT") == null || map.get("RECORD_ID") == null || map.get("CHECK_ID") == null
				|| map.get("CHECK_RESULT") == null || map.get("REJECT_REASON") == null || map.get("CHECK_DATE") == null
				|| map.get("CHECK_TIME") == null || map.get("Remark") == null || map.get("ACCUNT_WAY") == null) {
			resMap.put("resp_code", "ERROR_INPUT_EMPTY");
			return resMap;
		}
		int re = 0;
		try {
			re = dao.insertOneByMap("FtPayInsertMapper.insertService", map);
		} catch (DuplicateKeyException e) {
			resMap.put("resp_code", "ERR_ITEM_KEY_EXIST");
			return resMap;
		}
		if (re == 1) {
			resMap.put("resp_code", "INSERT_SERVICE_SUCCESS");
			return resMap;
		} else
			resMap.put("resp_code", "ERR_TRANID_CREATE_FAIL");
		return resMap;
	}

}
