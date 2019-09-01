package com.clps.rm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.rm.pojo.RmcustPo;
import com.clps.rm.service.PersonalAccountService;
import com.clps.rm.service.util.RmHelperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RM - 个人客户相关服务实现
 *
 * @author Boris Zhao
 *
 * @since 2016年9月8日 16:17
 *
 * @version v1.0
 *
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class PersonalAccountServiceImpl extends BaseService implements PersonalAccountService {
    
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_CUSTOMER_EXIST = "0001";
    private static final String ERR_CUSTOMER_NOT_FOUND = "0004";
    private static final String ERR_NOTHING_UPDATED = "0005";
    private static final String ERR_CUSTOMER_UPDATE_FAIL = "0006";
    private static final String ERR_CUSTOMER_CREATE_FAIL = "0007";
    
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @Override
    public Map<String, Object> personalAccountAdd(RmcustPo rmcustPo) throws Exception {
        log.info("RM - 增加个人客户");
        
        int re = 0;
        
        Map<String, Object> returnMap = new HashMap<>();
        
        try {
            re = dao.insertOneByObject("personalAccountMapper.insertPersonalCustomer", rmcustPo);
        } catch (DuplicateKeyException dupkeyException) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmcustPo, returnMap);
            returnMap.put("resp_out", ERR_CUSTOMER_EXIST);
            return returnMap;
        }
        
        if (re == 1) {
            // 插入了一条数据,返回插入数据
            
            String currentDate = DateTimeUtils.nowToSystem();
            
            // 写入历史
            RmHelperUtil.writeHistory(rmcustPo.getCust_number(),
                                      currentDate,
                                      "New personal account",
                                      rmcustPo.getCust_maker(),
                                      "",
                                      currentDate,
                                      rmcustPo.getCreate_user(),
                                      currentDate,
                                      rmcustPo.getCreate_user(),
                                      dao);
            
            // 逐条写入历史详细
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust org ID",
                                          "",
                                          rmcustPo.getCust_org_id(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust gender",
                                          "",
                                          rmcustPo.getCust_gender(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust nationality",
                                          "",
                                          rmcustPo.getCust_nationality(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust name",
                                          "",
                                          rmcustPo.getCust_name(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust city",
                                          "",
                                          rmcustPo.getCust_city(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust address",
                                          "",
                                          rmcustPo.getCust_address(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            if (!StringUtils.isBlank(rmcustPo.getCust_address2())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust address2",
                                              "",
                                              rmcustPo.getCust_address2(),
                                              currentDate,
                                              rmcustPo.getCreate_user(),
                                              currentDate,
                                              rmcustPo.getCreate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_address3())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust address3",
                                              "",
                                              rmcustPo.getCust_address3(),
                                              currentDate,
                                              rmcustPo.getCreate_user(),
                                              currentDate,
                                              rmcustPo.getCreate_user(),
                                              dao);
            }
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust postal code",
                                          "",
                                          rmcustPo.getCust_post_code(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust phone number",
                                          "",
                                          rmcustPo.getCust_phone_number(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust hand phone",
                                          "",
                                          rmcustPo.getCust_hand_phone(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                          currentDate,
                                          "Cust email",
                                          "",
                                          rmcustPo.getCust_email(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          currentDate,
                                          rmcustPo.getCreate_user(),
                                          dao);
            
            if (!StringUtils.isBlank(rmcustPo.getCust_cred_assess())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust credit assess",
                                              "",
                                              rmcustPo.getCust_cred_assess(),
                                              currentDate,
                                              rmcustPo.getCreate_user(),
                                              currentDate,
                                              rmcustPo.getCreate_user(),
                                              dao);
            }
    
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmcustPo, returnMap);
            returnMap.put("resp_out", ERR_NORMAL);
            return returnMap;
        } else {
            // 没有插入,返回空
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmcustPo, returnMap);
            returnMap.put("resp_out", ERR_CUSTOMER_CREATE_FAIL);
            return returnMap;
        }
    }
    
    @Override
    public RmcustPo personalAccountInq(RmcustPo rmcustPo) throws Exception {
        log.info("RM - 个人客户查询");
        return (RmcustPo) dao.selectOneObject("personalAccountMapper.selectPersonalCustomer", rmcustPo);
    }
    
    @Override
    public Map<String, Object> personalAccountUpd(RmcustPo rmcustPo) throws Exception {
        log.info("RM - 个人客户更新");
    
        final Integer CMP_RES_SAME = 1;
        
        Map<String, Object> returnMap = new HashMap<>();
        
        // 更新前数据
        RmcustPo origData = personalAccountInq(rmcustPo);
        
        if (origData == null) {
            returnMap.put("resp_out", ERR_CUSTOMER_NOT_FOUND);
            return returnMap;
        }
        
        if (RmHelperUtil.compare(origData, rmcustPo) == CMP_RES_SAME) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmcustPo, returnMap);
            returnMap.put("resp_out", ERR_NOTHING_UPDATED);
            return returnMap;
        }
        
        int re = dao.updateByObject("personalAccountMapper.updatePersonalCustomer", rmcustPo);
        if (re == 1) {
            // 插入了一条数据,返回插入数据
            
            String currentDate = DateTimeUtils.nowToSystem();
            
            // 写入历史
            RmHelperUtil.writeHistory(rmcustPo.getCust_number(),
                                      currentDate,
                                      "Upd personal account",
                                      rmcustPo.getCust_maker(),
                                      "",
                                      currentDate,
                                      rmcustPo.getUpdate_user(),
                                      currentDate,
                                      rmcustPo.getUpdate_user(),
                                      dao);
            
            // 逐条写入历史详细
            // 仅当新数据非空且不同于旧数据时，才会记录更新历史
            if (!StringUtils.isBlank(rmcustPo.getCust_org_id())
                && !origData.getCust_org_id().equals(rmcustPo.getCust_org_id())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust org ID",
                                              origData.getCust_org_id(),
                                              rmcustPo.getCust_org_id(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_gender())
                && !origData.getCust_gender().equals(rmcustPo.getCust_gender())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust gender",
                                              origData.getCust_gender(),
                                              rmcustPo.getCust_gender(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_nationality())
                && !origData.getCust_nationality().equals(rmcustPo.getCust_nationality())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust nationality",
                                              origData.getCust_nationality(),
                                              rmcustPo.getCust_nationality(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_name())
                && !origData.getCust_name().equals(rmcustPo.getCust_name())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust name",
                                              origData.getCust_name(),
                                              rmcustPo.getCust_name(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_city())
                && !origData.getCust_city().equals(rmcustPo.getCust_city())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust city",
                                              origData.getCust_city(),
                                              rmcustPo.getCust_city(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_address())
                && !origData.getCust_address().equals(rmcustPo.getCust_address())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust address",
                                              origData.getCust_address(),
                                              rmcustPo.getCust_address(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_address2())
                && !origData.getCust_address2().equals(rmcustPo.getCust_address2())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust address2",
                                              origData.getCust_address2(),
                                              rmcustPo.getCust_address2(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_address3())
                && !origData.getCust_address3().equals(rmcustPo.getCust_address3())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust address3",
                                              origData.getCust_address3(),
                                              rmcustPo.getCust_address3(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_post_code())
                && !origData.getCust_post_code().equals(rmcustPo.getCust_post_code())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust postal code",
                                              origData.getCust_post_code(),
                                              rmcustPo.getCust_post_code(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_phone_number())
                && !origData.getCust_phone_number().equals(rmcustPo.getCust_phone_number())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust phone number",
                                              origData.getCust_phone_number(),
                                              rmcustPo.getCust_phone_number(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_hand_phone())
                && !origData.getCust_hand_phone().equals(rmcustPo.getCust_hand_phone())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust hand phone",
                                              origData.getCust_hand_phone(),
                                              rmcustPo.getCust_hand_phone(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_email())
                && !origData.getCust_email().equals(rmcustPo.getCust_email())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust email",
                                              origData.getCust_email(),
                                              rmcustPo.getCust_email(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
            
            if (!StringUtils.isBlank(rmcustPo.getCust_cred_assess())
                && !origData.getCust_cred_assess().equals(rmcustPo.getCust_cred_assess())) {
                RmHelperUtil.writeHistoryItem(rmcustPo.getCust_number(),
                                              currentDate,
                                              "Cust credit assess",
                                              origData.getCust_cred_assess(),
                                              rmcustPo.getCust_cred_assess(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              currentDate,
                                              rmcustPo.getUpdate_user(),
                                              dao);
            }
    
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmcustPo, returnMap);
            returnMap.put("resp_out", ERR_NORMAL);
            return returnMap;
        } else {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmcustPo, returnMap);
            returnMap.put("resp_out", ERR_CUSTOMER_UPDATE_FAIL);
            return returnMap;
        }
    }
    
    @Override
    public Map<String, Object> personalAccountNameInq(Map<String, Object> map) throws Exception {
        log.info("RM - 个人客户姓名查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("personalAccountMapper.queryCustomerByName_count", map);
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("personalAccountMapper.queryCustomerByName", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeDataForCLB(list, total, null);
        System.out.println("resultMap::::::::::"+resultMap);
        if (total != 0) {
            resultMap.put("nxt_cust", list.get(list.size() - 1).get("number"));
        } else {
            resultMap.put("nxt_cust", "");
        }
        return resultMap;
    }
}
