package com.clps.rm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.rm.pojo.RmrealPo;
import com.clps.rm.service.CustomerRelationshipService;
import com.clps.rm.service.util.RmHelperUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RM - 客户关系相关服务
 *
 * @author Boris Zhao
 * @version v1.0
 * @since 2017-04-17 下午6:09:54
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CustomerRelationshipServiceImpl extends BaseService implements CustomerRelationshipService {
    
    // 错误码定义
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_CUSTOMER_NOT_EXIST = "0001";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0002";
    private static final String ERR_RELATION_ALREADY_EXIST = "0003";
    private static final String ERR_INVALID_OPERATION = "0004";
    private static final String ERR_NOTHING_UPDATED = "0005";
    private static final String ERR_RELATION_CREATE_FAIL = "0006";
    private static final String ERR_RELATION_NOT_EXIST = "0007";
    private static final String ERR_RELATION_UPDATE_FAIL = "0009";
    private static final String ERR_NOT_IMPLEMENTED_YET = "9998";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @Override
    public Map<String, Object> relationshipAdd(RmrealPo rmrealPo) throws Exception {
        log.info("RM - 增加客户关系");
        
        String currentDate = DateTimeUtils.changeToDate();
        String currentDateTime = DateTimeUtils.nowToSystem();
        
        Map<String, Object> map = new HashMap<>();
        
        try {
            int re = dao.insertOneByObject("custRelationMapper.insertRelationship", rmrealPo);
            if (re == 1) {
                // 插入了一条数据,返回插入数据
                // 写入历史
                RmHelperUtil.writeHistory(rmrealPo.getCreal_cus_num(),
                        currentDateTime,
                        "New cust relation",
                        rmrealPo.getCreal_maker(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                // 逐条写入历史详细
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDateTime,
                        "Relation cust number",
                        "",
                        rmrealPo.getCreal_cus_num(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDateTime,
                        "Relation type",
                        "",
                        rmrealPo.getCreal_retype(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDateTime,
                        "Related cust number",
                        "",
                        rmrealPo.getCreal_rel_cus(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDateTime,
                        "Relation mark",
                        "",
                        rmrealPo.getCreal_mark(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDateTime,
                        "Relation maint date",
                        "",
                        rmrealPo.getCreal_main_date(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDateTime,
                        "Relation maker",
                        "",
                        rmrealPo.getCreal_maker(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                map = MapAndObjectUtils.copyPropertiesToMap(rmrealPo, map);
                map.put("crealo_resp", ERR_NORMAL);
                return map;
            } else {
                map.put("crealo_resp", ERR_RELATION_CREATE_FAIL);
                return map;
            }
        } catch (DuplicateKeyException dupkeyException) {
            map.put("crealo_resp", ERR_RELATION_ALREADY_EXIST);
            return map;
        }
    }
    
    @Override
    public RmrealPo relationshipInq(RmrealPo rmrealPo) throws Exception {
        return (RmrealPo) dao.selectOneObject("custRelationMapper.selectRelationship", rmrealPo);
    }
    
    @Override
    public Map<String, Object> relationshipUpd(RmrealPo rmrealPo) throws Exception {
        Map<String, Object> map = new HashMap<>();
        
        RmrealPo origData = relationshipInq(rmrealPo);
        
        if (null == origData) {
            map.put("crealo_resp", ERR_RELATION_NOT_EXIST);
            return map;
        }
        
        // 对比新数据与原数据，无变化直接返回，不修改
        if (origData.getCreal_cus_num().equals(rmrealPo.getCreal_cus_num())
                && origData.getCreal_retype().equals(rmrealPo.getCreal_retype())
                && origData.getCreal_rel_cus().equals(rmrealPo.getCreal_rel_cus())
                && origData.getCreal_mark().equals(rmrealPo.getCreal_mark())) {
            map = MapAndObjectUtils.copyPropertiesToMap(rmrealPo, map);
            map.put("crealo_resp", ERR_NOTHING_UPDATED);
            return map;
        }
        
        int re = dao.updateByObject("custRelationMapper.updateRelationship", rmrealPo);
        
        if (re == 1) {
            // 插入了一条数据,返回插入数据
            
            String currentDate = DateTimeUtils.nowToSystem();
            
            // 写入历史
            RmHelperUtil.writeHistory(rmrealPo.getCreal_cus_num(),
                    currentDate,
                    "Upd cust relation",
                    rmrealPo.getCreal_maker(),
                    "",
                    currentDate,
                    rmrealPo.getCreate_user(),
                    currentDate,
                    rmrealPo.getCreate_user(),
                    dao);
            
            // 逐条写入历史详细
            if (!StringUtils.isBlank(rmrealPo.getCreal_mark())
                    && !origData.getCreal_mark().equals(rmrealPo.getCreal_mark())) {
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation mark",
                        origData.getCreal_mark(),
                        rmrealPo.getCreal_mark(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmrealPo.getCreal_main_date())
                    && !origData.getCreal_main_date().equals(rmrealPo.getCreal_main_date())) {
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation maint date",
                        origData.getCreal_main_date(),
                        rmrealPo.getCreal_main_date(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmrealPo.getCreal_maker())
                    && !origData.getCreal_maker().equals(rmrealPo.getCreal_maker())) {
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation maker",
                        origData.getCreal_maker(),
                        rmrealPo.getCreal_maker(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
            }
    
            map = MapAndObjectUtils.copyPropertiesToMap(rmrealPo, map);
            map.put("crealo_resp", ERR_NORMAL);
            return map;
        } else {
            map = MapAndObjectUtils.copyPropertiesToMap(rmrealPo, map);
            map.put("crealo_resp", ERR_RELATION_UPDATE_FAIL);
            return map;
        }
    }
    
    @Override
    public Map<String, Object> relationshipDel(RmrealPo rmrealPo) throws Exception {
        
        Map<String, Object> map = new HashMap<>();
        
        RmrealPo origData = relationshipInq(rmrealPo);
        
        if (null != origData) {
            
            int re = dao.updateByObject("custRelationMapper.deleteRelationship", rmrealPo);
            
            if (re == 1) {
                String currentDate = DateTimeUtils.nowToSystem();
                
                // 写入历史
                RmHelperUtil.writeHistory(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Del cust relation",
                        rmrealPo.getCreal_maker(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                // 逐条写入历史详细
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation cust number",
                        origData.getCreal_cus_num(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation type",
                        origData.getCreal_retype(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Related cust number",
                        origData.getCreal_rel_cus(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation mark",
                        origData.getCreal_mark(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation maint date",
                        origData.getCreal_main_date(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmrealPo.getCreal_cus_num(),
                        currentDate,
                        "Relation maker",
                        origData.getCreal_maker(),
                        "",
                        currentDate,
                        rmrealPo.getCreate_user(),
                        currentDate,
                        rmrealPo.getCreate_user(),
                        dao);
                
                map.put("crealo_resp", ERR_NORMAL);
            } else {
                map.put("crealo_resp", ERR_RELATION_NOT_EXIST);
            }
            return map;
        } else {
            map.put("crealo_resp", ERR_RELATION_NOT_EXIST);
            return map;
        }
    }
    
    @Override
    public Map<String, Object> relationshipList(Map<String, Object> map) throws Exception {
        log.info("RM - 客户关系列表查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("custRelationMapper.getRelationList_count", map);
        
        map = QueryListUtils.changeInputData(map, total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("custRelationMapper.getRelationList", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        if (total != 0) {
            resultMap.put("nt_cus", list.get(list.size() - 1).get("crealqo_cus_num"));
            resultMap.put("nt_type", list.get(list.size() - 1).get("crealqo_retype"));
            resultMap.put("nt_relcus", list.get(list.size() - 1).get("crealqo_rel_cus"));
        } else {
            resultMap.put("nt_cus", "");
            resultMap.put("nt_type", "");
            resultMap.put("nt_relcus", "");
        }
        resultMap.put("all_count", total);
        return resultMap;
    }
}
