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
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.ft.service.FtMsgAddService;
import com.clps.util.DataTypeChange;

/**
 * FtMsgAddServiceImpl <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason: TODO ADD REASON. <br/>
 * 
 * @author terry.zhang
 * @version
 * @since JDK 1.8
 * @see
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class FtMsgAddServiceImpl extends BaseService implements FtMsgAddService {

	private final String ERROR_INPUT_EMPTY = "0001";
	private final String ERROR_OUT_RANGE = "0002";
	private final String ERROR_INQ = "6666";
	private final String GROUP_CATALOG = "BEPS";

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class) // 非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> ftMsgAddService(Map<String, Object> map) throws Exception {
		// 记录日志
		log.info("进入报文发起服务");
		// 返回的数据
		Map<String, Object> resMap = new HashMap<>();
		// 来账往账表中的共有数据 17
		Map<String, Object> reMap = new HashMap<>();
		// 插入往账表中的map 25
		Map<String, Object> vMap = new HashMap<>();
		// 插入来账表中的map 25
		Map<String, Object> nMap = new HashMap<>();
		// 从登记簿表中查出的数据
		Map<String, Object> queryRMap = (Map<String, Object>) dao.selectOneObject("payMapper.payQueryOneService", map);

		Map<String, Object> maxMap = new HashMap<>();
		// 判断需要查出来的数据，需要插入报文表的数据是否为空 16 个字段
		if (DataTypeChange.checkNULL(queryRMap.get("TXN_JOUR")) || (DataTypeChange.checkNULL(queryRMap.get("TXN_DATE")))
				|| (DataTypeChange.checkNULL(queryRMap.get("BUSINESS_TYPE")))
				|| (DataTypeChange.checkNULL(queryRMap.get("DEDUC_CARD_NO")))
				|| (DataTypeChange.checkNULL(queryRMap.get("DEDUC_NAME")))
				|| (DataTypeChange.checkNULL(queryRMap.get("DEDUC_BANK_CODE")))
				|| (DataTypeChange.checkNULL(queryRMap.get("DEDUC_BANK_NUMBER")))
				|| (DataTypeChange.checkNULL(queryRMap.get("RECV_BANK_CODE")))
				|| (DataTypeChange.checkNULL(queryRMap.get("RECV_BANK_NUMBER")))
				|| (DataTypeChange.checkNULL(queryRMap.get("RECV_CARD_NO")))
				|| (DataTypeChange.checkNULL(queryRMap.get("RECV_NAME")))
				|| (DataTypeChange.checkNULL(queryRMap.get("CHECK_TIME")))
				|| (DataTypeChange.checkNULL(queryRMap.get("ADDTION_INF")))
				|| (DataTypeChange.checkNULL(queryRMap.get("CHECK_RESULT")))
				|| (DataTypeChange.checkNULL(queryRMap.get("REC_ACCT")))) {
			resMap.put("resp_code", ERROR_INPUT_EMPTY);
			resMap.put("successful", "false");
			resMap.put("resp_msg", "必需值不能为null");
			return resMap;
		}

		// 判断最大回执时间是否符合规范
		if ((DataTypeChange.StrToLong(DataTypeChange.ObjToStr(map.get("MAX_RETURN_TIME"))) > 5)
				|| (DataTypeChange.StrToLong(DataTypeChange.ObjToStr(map.get("MAX_RETURN_TIME"))) <= 0)) {

			resMap.put("resp_code", ERROR_OUT_RANGE);
			resMap.put("successful", "false");
			resMap.put("resp_msg", "最大回执时间为0到5");
			return resMap;
		}

		// 前台输入字段 因为要插入往账和来账两张表 表中字段一样 可有些值不一样 定义了两个Map
		reMap.put("MSG_NUM", map.get("MSG_NUM"));
		reMap.put("MSG_ID", map.get("MSG_ID"));
		reMap.put("TXN_AMT", map.get("TRANS -AMT"));
		reMap.put("TXN_CCY", map.get("TRANS -CCY"));
		reMap.put("PRODUCT_CODE", map.get("PRODUCT_CODE"));
		reMap.put("ORIGINAL_MSG_ID", map.get("MSG_ID"));
		reMap.put("MAX_RETURN_TIME", map.get("MAX_RETURN_TIME"));
		// 登记表中字段
		reMap.put("TXN_JOUR", queryRMap.get("TXN_JOUR"));
		reMap.put("TXN_DATE", queryRMap.get("TXN_DATE"));
		reMap.put("BUSINESS_TYPE", queryRMap.get("BUSINESS_TYPE"));
		reMap.put("AUDIT_TIME1", queryRMap.get("CHECK_TIME"));
		reMap.put("AUDIT_TIME2", queryRMap.get("CHECK_TIME"));
		reMap.put("ADDTION_INF", queryRMap.get("ADDTION-INF"));
		reMap.put("CLARIFY_RESULT", queryRMap.get("CHECK_RESULT"));

		// 截取登记账号的第二位到第十四位为原支付序号
		reMap.put("ORIGINAL_PAY_SEQ", DataTypeChange.subStr(DataTypeChange.ObjToStr(queryRMap.get("REC_ACCT")), 1, 15));

		// 根据接收系统为大小额来发送报文
		if (map.get("RECV_SYSTEM").equals("HVPS")) {
			reMap.put("MSG_STATUS", "2");
			reMap.put("UPDATE_STATUS_TIME", DateTimeUtils.nowToSystem());

			vMap.putAll(reMap);
			vMap.put("DEDUC_CARD_NO", queryRMap.get("DEDUC_CARD_NO"));
			vMap.put("DEDUC_NAME", queryRMap.get("DEDUC_NAME"));
			vMap.put("DEDUC_BANK_CODE", queryRMap.get("DEDUC_BANK_CODE"));
			vMap.put("DEDUC_BANK_NUMBER", queryRMap.get("DEDUC_BANK_NUMBER"));
			vMap.put("RECV_BANK_CODE", queryRMap.get("RECV_BANK_CODE"));
			vMap.put("RECV_BANK_NUMBER", queryRMap.get("RECV_BANK_NUMBER"));
			vMap.put("RECV_CARD_NO", queryRMap.get("RECV_CARD_NO"));
			vMap.put("RECV_NAME", queryRMap.get("RECV_NAME"));
			// 往账插入
			int i = dao.insertOneByMap("ftMsgAddMapper.ftVMsgAddVService", vMap);
			if (i != 1) {
				resMap.put("resp_code", ERROR_INQ);
				resMap.put("successful", "false");
				resMap.put("resp_msg", "往账数据插入失败");
				return resMap;
			}
			nMap.putAll(reMap);
			nMap.put("DEDUC_CARD_NO", queryRMap.get("RECV_CARD_NO"));
			nMap.put("DEDUC_NAME", queryRMap.get("RECV_NAME"));
			nMap.put("DEDUC_BANK_CODE", queryRMap.get("RECV_BANK_CODE"));
			nMap.put("DEDUC_BANK_NUMBER", queryRMap.get("RECV_BANK_NUMBER"));
			nMap.put("RECV_BANK_CODE", queryRMap.get("DEDUC_BANK_CODE"));
			nMap.put("RECV_BANK_NUMBER", queryRMap.get("DEDUC_BANK_NUMBER"));
			nMap.put("RECV_CARD_NO", queryRMap.get("DEDUC_CARD_NO"));
			nMap.put("RECV_NAME", queryRMap.get("DEDUC_NAME"));

			// 来账插入
			int m = dao.insertOneByMap("ftMsgAddMapper.ftNMsgAddService", nMap);
			if (m != 1){
				resMap.put("resp_code", ERROR_INQ);
				resMap.put("successful", "false");
				resMap.put("resp_msg", "来账数据插入失败");
				return resMap;
			}

			// 插入数据到LOG表中
			map.put("SEND_LOG_NUM", "1201701123");
			map.put("CURR_STATUS", "2");
			int n = dao.insertOneByMap("ftMsgAddMapper.ftLogMsgAddService", map);
			if (n != 1){
				resMap.put("resp_code", ERROR_INQ);
				resMap.put("successful", "false");
				resMap.put("resp_msg", "日志来账数据插入失败");
				return resMap;
			}
		} else if (map.get("RECV_SYSTEM").equals("BEPS")) {
			reMap.put("MSG_STATUS", "1");
			reMap.put("UPDATE_STATUS_TIME", DateTimeUtils.nowToSystem());

			vMap.putAll(reMap);
			vMap.put("DEDUC_CARD_NO", queryRMap.get("DEDUC_CARD_NO"));
			vMap.put("DEDUC_NAME", queryRMap.get("DEDUC_NAME"));
			vMap.put("DEDUC_BANK_CODE", queryRMap.get("DEDUC_BANK_CODE"));
			vMap.put("DEDUC_BANK_NUMBER", queryRMap.get("DEDUC_BANK_NUMBER"));
			vMap.put("RECV_BANK_CODE", queryRMap.get("RECV_BANK_CODE"));
			vMap.put("RECV_BANK_NUMBER", queryRMap.get("RECV_BANK_NUMBER"));
			vMap.put("RECV_CARD_NO", queryRMap.get("RECV_CARD_NO"));
			vMap.put("RECV_NAME", queryRMap.get("RECV_NAME"));
			// 往账插入
			int i = dao.insertOneByMap("ftMsgAddMapper.ftVMsgAddVService", vMap);
			if (i != 1) {
				resMap.put("resp_code", ERROR_INQ);
				resMap.put("successful", "false");
				resMap.put("resp_msg", "往账数据插入失败");
				return resMap;
			}

			// 如果报文标识的第6位到第8位为127或者121
			if (DataTypeChange.subStr(DataTypeChange.ObjToStr(map.get("MSG_ID")), 5, 8).equals("121")
					|| DataTypeChange.subStr(DataTypeChange.ObjToStr(map.get("MSG_ID")), 5, 8).equals("127")) {
				nMap.putAll(reMap);
				nMap.put("DEDUC_CARD_NO", queryRMap.get("RECV_CARD_NO"));
				nMap.put("DEDUC_NAME", queryRMap.get("RECV_NAME"));
				nMap.put("DEDUC_BANK_CODE", queryRMap.get("RECV_BANK_CODE"));
				nMap.put("DEDUC_BANK_NUMBER", queryRMap.get("RECV_BANK_NUMBER"));
				nMap.put("RECV_BANK_CODE", queryRMap.get("DEDUC_BANK_CODE"));
				nMap.put("RECV_BANK_NUMBER", queryRMap.get("DEDUC_BANK_NUMBER"));
				nMap.put("RECV_CARD_NO", queryRMap.get("DEDUC_CARD_NO"));
				nMap.put("RECV_NAME", queryRMap.get("DEDUC_NAME"));

				// 来账插入
				int m = dao.insertOneByMap("ftMsgAddMapper.ftNMsgAddService", nMap);
				if (m != 1)  {
					resMap.put("resp_code", ERROR_INQ);
					resMap.put("successful", "false");
					resMap.put("resp_msg", "来账数据插入失败");
					return resMap;
				}
			}
			// 查出符合记录的总条数
			Long vtotal = (long) dao.selectOneObject("ftMsgAddMapper.ftVQueryListService_count", map);
			// 查出所有符合条件的记录
			List<Map<String, Object>> vlist = dao.selectListMap("ftMsgAddMapper.ftVQueryListService", map);
			// 查出报文组包号的最大值
			maxMap.put("pkg_num", "pkg_num");
			// 从数据库里截取PKG_NUM的第五位到第九位并查出最大值
			String max1 = (String) dao.selectOneObject("ftMsgAddMapper.ftQueryMaxService", maxMap);
			// 转换成Long类型进行累加1操作
			Long lmax1 = DataTypeChange.StrToLong(max1) + 1;
			// PKG_NUM 由G-CATALOG SEQ G-FLAG三部分组成
			String pkg_num = null;
			// 交易总金额
			int total_txn_amt = 0;
			// 如果查出来的数量大于等于10且是10的倍数，插入报文组包
			if (vtotal >= 10 && vtotal % 10 == 0) {
				for (int j = 1; j < vlist.size(); j++) {
					Map<String, Object> gMap = vlist.get(j);
					int txt_amt = (int) gMap.get("TXN_AMT");
					// 累加
					total_txn_amt += txt_amt;
					// 报文组包拼接
					pkg_num = GROUP_CATALOG + DataTypeChange.LongToString(lmax1) + DataTypeChange.getGFlag(0);
					gMap.put("PKG_NUM", pkg_num);
					// RPT_SEND_DATE发包日期 将系统时间(年月日格式)做去除-处理 8位结构 yyyyMMdd
					gMap.put("RPT_SEND_DATE",
							DataTypeChange.deleteChar("-", DateTimeUtils.changeToDate(DateTimeUtils.nowToSystem())));
					gMap.put("OPP_BANK_CODE", gMap.get("RECV-BANK-CODE"));
					gMap.put("PKG_STATUS", "2");
					gMap.put("TOTAL_TXN_AMT", "0");

					Map<String, Object> mmap = new HashMap<>();
					mmap.put("MSG_NUM", gMap.get("MSG_NUM"));
					mmap.put("UPDATE_STATUS_TIME", DateTimeUtils.nowToSystem());
					// 更新往账报文表
					int n = dao.updateByMap("ftMsgAddMapper.ftVEdit", mmap);
					if (n != 1)  {
						resMap.put("resp_code", "ERR_UPD");
						resMap.put("successful", "false");
						resMap.put("resp_msg", "往账数据更新失败");
						return resMap;
					}

					// 插入报文组包
					int k = dao.insertOneByMap("ftMsgAddMapper.ftGMsgAddService", gMap);
					if (k != 1) {
						resMap.put("resp_code", ERROR_INQ);
						resMap.put("successful", "false");
						resMap.put("resp_msg", "报文组包插入失败");
						return resMap;
					}
					if (j % 10 == 0) {
						lmax1 += 1;
						// 累加10 次记一次总额 更新记录
						map.put("TOTAL_TXN_AMT", total_txn_amt);
						int i1 = dao.updateByMap("ftMsgAddMapper.ftGEdit", gMap);
						if (i1 != 1) {
							resMap.put("resp_code", "ERR_UPD");
							resMap.put("successful", "false");
							resMap.put("resp_msg", "报文组包更新失败");
							return resMap;
						}
						Map<String, Object> lmap2 = new HashMap<>();
						lmap2.put("PKG_NUM", pkg_num);
						lmap2.put("SEND_LOG_NUM", "1201701122");
						lmap2.put("CURR_STATUS", "2");
						int m = dao.insertOneByMap("ftMsgAddMapper.ftLogMsgAddService", lmap2);
						if (m != 1) {
							resMap.put("resp_code", ERROR_INQ);
							resMap.put("successful", "false");
							resMap.put("resp_msg", "报文组包日志更新失败");
							return resMap;
						}

						// 更新10条数据的总金额，并把总金额置为零继续累加
						total_txn_amt = 0;
					}
				}
			}

			// 查出符合记录的总条数
			Long ntotal = (long) dao.selectOneObject("ftMsgAddMapper.ftNQueryListService_count", map);
			// 查出所有符合条件的记录
			List<Map<String, Object>> nlist = dao.selectListMap("ftMsgAddMapper.ftNQueryListService", map);

			// 从数据库里截取PKG_NUM的第五位到第九位并查出最大值
			String max2 = (String) dao.selectOneObject("ftMsgAddMapper.ftQueryMaxService", maxMap);
			// 转换成Long类型进行累加1操作
			Long lmax2 = DataTypeChange.StrToLong(max2) + 1;
			// 如果查出来的数量大于等于10且是10的倍数，插入报文组包
			if (ntotal >= 10 && ntotal % 10 == 0) {
				for (int j = 1; j < nlist.size(); j++) {
					Map<String, Object> gMap = nlist.get(j);
					int txt_amt = (int) gMap.get("TXN_AMT");
					total_txn_amt += txt_amt;
					pkg_num = GROUP_CATALOG + DataTypeChange.LongToString(lmax2) + DataTypeChange.getGFlag(1);
					gMap.put("PKG_NUM", pkg_num);
					// RPT_SEND_DATE发包日期 将系统时间(年月日格式)做去除-处理 8位结构 yyyyMMdd
					gMap.put("RPT_SEND_DATE",
							DataTypeChange.deleteChar("-", DateTimeUtils.changeToDate(DateTimeUtils.nowToSystem())));
					gMap.put("OPP_BANK_CODE", gMap.get("RECV-BANK-CODE"));
					gMap.put("PKG_STATUS", "2");
					gMap.put("TOTAL_TXN_AMT", "0");

					Map<String, Object> mmap = new HashMap<>();
					mmap.put("MSG_NUM", gMap.get("MSG_NUM"));
					mmap.put("UPDATE_STATUS_TIME", DateTimeUtils.nowToSystem());
					// 更新来账报文表
					int l=dao.updateByMap("ftMsgAddMapper.ftNEdit", mmap);
					if (l != 1) {
						resMap.put("resp_code", ERROR_INQ);
						resMap.put("successful", "false");
						resMap.put("resp_msg", "更新来账报文表失败");
						return resMap;
					}
					
					// 插入报文组表数据
					int q=dao.insertOneByMap("ftMsgAddMapper.ftGMsgAddService", gMap);
					if (q != 1) {
						resMap.put("resp_code", ERROR_INQ);
						resMap.put("successful", "false");
						resMap.put("resp_msg", "插入报文组表失败");
						return resMap;
					}
					if (j % 10 == 0) {
						lmax2 += 1;
						map.put("TOTAL_TXN_AMT", total_txn_amt);
						int u=dao.updateByMap("ftMsgAddMapper.ftGEdit", gMap);
						if (q != 1) {
							resMap.put("resp_code", ERROR_INQ);
							resMap.put("successful", "false");
							resMap.put("resp_msg", "更新报文组表失败");
							return resMap;
						}
						
						// 更新10条数据的总金额，并把总金额置为零继续累加
						total_txn_amt = 0;

						Map<String, Object> lmap1 = new HashMap<>();
						lmap1.put("PKG_NUM", pkg_num);
						lmap1.put("SEND_LOG_NUM", "1201701122");
						lmap1.put("CURR_STATUS", "2");
						int w=dao.insertOneByMap("ftMsgAddMapper.ftLogMsgAddService", lmap1);
						if (w != 1) {
							resMap.put("resp_code", ERROR_INQ);
							resMap.put("successful", "false");
							resMap.put("resp_msg", "插入日志表失败");
							return resMap;
						}
					}
				}
			}
		}

		return resMap;

	}
}
