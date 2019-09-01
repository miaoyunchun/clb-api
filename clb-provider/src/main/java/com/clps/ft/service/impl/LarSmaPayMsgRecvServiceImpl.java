
package com.clps.ft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.StringUtils;
import com.clps.ft.service.LarSmaPayMsgRecvService;

/**
 * 
 * ClassName: LarSmaPayMsgRecvServiceImpl. Function: 大小额支付业务报文接收. Reason: TODO
 * ADD REASON(可选). date: 2017年1月10日 上午9:48:27
 *
 * @author tony.tan
 * @version
 *
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class LarSmaPayMsgRecvServiceImpl extends BaseService implements LarSmaPayMsgRecvService {

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class) // 非只读事务，支持当前事务(常见)，遇到异常Rollback
	@Override
	public Map<String, Object> msgRecv(Map<String, Object> map) throws Exception {
		log.info("大小额支付业务报文接收服务");
		map.put("create_time", DateTimeUtils.nowToSystem());
		map.put("update_time", DateTimeUtils.nowToSystem());
		String MSG_NUM = map.get("MSG_NUM").toString();
		String PKG_NUM = map.get("PKG_NUM").toString();
		if (MSG_NUM != null && !MSG_NUM.equals("") && PKG_NUM.equals("")) {
			Random rd = new Random();
			// 随机取0和1
			int rdNum = rd.nextInt(2);
			if (rdNum == 0) {
				map.put("MSG_STATUS", "4");
			} else if (rdNum == 1) {
				map.put("MSG_STATUS", "5");
			}
			int re = dao.updateByMap("msgRecvMapper.larBusUpdateService", map);
			if (re == 1) {
				map.put("successful", "true");
				map.put("resp_code", "0000");
				map.put("resp_msg", "往账报文明细更新成功!");
				return map;
			} else {
				map.put("successful", "false");
				map.put("resp_code", "0001");
				map.put("resp_msg", "往账报文明细更新失败!");
				return map;
			}
		} else if (MSG_NUM.equals("") && PKG_NUM != null && !PKG_NUM.equals("")) {
			List<Map<String,Object>> list=dao.selectListMap("msgRecvMapper.smalQueryListService", map);
               System.out.println("queryMap:"+list);
               //MSG_NUM=queryMap.get("MSG_NUM").toString();
               int i=0;
               // 随机取0和1
               Random rd = new Random();
      			int rdNum = rd.nextInt(2);
               for (Map<String, Object> tMap : list) {
            	MSG_NUM=tMap.get("MSG_NUM").toString();
            	String MSG_ID=tMap.get("MSG_ID").toString();
            	System.out.println("Msg:"+MSG_NUM);
            	Map<String,Object> smalMap=new HashMap<>();
            	smalMap.put("MSG_NUM", MSG_NUM);
            	smalMap.put("PKG_NUM", PKG_NUM);
       			if (rdNum == 0) {
       				smalMap.put("MSG_STATUS", "4");
       				smalMap.put("PKG_STATUS","1");
       			} else if (rdNum == 1) {
       				smalMap.put("MSG_STATUS", "5");
       				smalMap.put("PKG_STATUS","2");
       			}
       			if (MSG_ID.equals("beps.127.001.01")) {
       				//来账报文明细中新增一条回执
       				System.out.println("smalMap:"+smalMap);
       				@SuppressWarnings("unchecked")
					Map<String, Object> queryMap=(Map<String, Object>) dao.selectOneMap("msgRecvMapper.larbusQueryOneService", smalMap);
       				System.out.println("queryMap:"+queryMap);
       				//ORIGINAL_PAY_SEQ+ “11” BUSINESS-TYPE B100，MSG-STATUS为2   ORIGINAL-MSG-ID A127
       				String q_MSG_NUM=queryMap.get("MSG_NUM").toString();
       				StringBuffer sb=new StringBuffer(q_MSG_NUM);
       				sb.replace(14, 16, "11");
       				queryMap.remove("MSG_NUM");
       				queryMap.remove("MSG_ID");
       				queryMap.remove("BUSINESS_TYPE");
       				queryMap.remove("MSG_STATUS");
       				queryMap.remove("ORIGINAL_MSG_ID");
       				queryMap.put("MSG_ID", "beps128.001.01");
       				queryMap.put("MSG_NUM", sb.toString());
       				queryMap.put("BUSINESS_TYPE", "B100");
       				queryMap.put("MSG_STATUS", "2");
       				queryMap.put("ORIGINAL_MSG_ID", "A127");
       				//
       				dao.insertOneByMap("msgRecvMapper.insertLarBusService", queryMap);
       				//记录复制写进报文包文件，报文编号中最后一位为1
       				StringBuffer sb1=new StringBuffer(MSG_NUM);
       				sb1.replace(15, 16, "1");
       				tMap.remove("MSG_NUM");
       				tMap.put("MSG_NUM", sb1.toString());
       				System.out.println("tMap:"+tMap);
       				dao.insertOneByMap("msgRecvMapper.insertSmalBusService", tMap);
				}
       			int re1 = dao.updateByMap("msgRecvMapper.larBusUpdateService", smalMap);
       			int re2=dao.updateByMap("msgRecvMapper.smalBusUpdateService", smalMap);
       		    System.out.println("re:----------"+re2);
       		    i+=re1;
			}
               if (i!=0) {
   				map.put("successful", "true");
   				map.put("resp_code", "0000");
   				map.put("resp_msg", "往账报文明细更新成功!");
   				return map;
   			} else {
   				map.put("successful", "false");
   				map.put("resp_code", "0001");
   				map.put("resp_msg", "往账报文明细更新失败!");
   				return map;
   			}
		}
		return null;
	}

}
