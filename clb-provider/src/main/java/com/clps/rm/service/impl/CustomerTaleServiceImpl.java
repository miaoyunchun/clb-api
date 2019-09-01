package com.clps.rm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.rm.pojo.RmtalePo;
import com.clps.rm.service.CustomerTaleService;
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
 * 客户大事件相关服务
 * Created by boris on 2016/10/28.
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class CustomerTaleServiceImpl extends BaseService implements CustomerTaleService {
    
    // 错误码定义
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_ITEM_EXIST = "0001";
    private static final String ERR_TALE_NOT_FOUND = "0003";
    private static final String ERR_NOTHING_UPDATED = "0005";
    private static final String ERR_TALE_UPDATE_FAIL = "0006";
    private static final String ERR_TALE_CREATE_FAIL = "0007";
    
    private static int COMPARE_NOT_SAME = 0;
    private static int COMPARE_SAME = 1;
    
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @Override
    public Map<String, Object> taleAdd(RmtalePo rmtalePo) throws Exception {
        log.info("RM - 增加客户大事件");

        Map<String, Object> map = new HashMap<>();
        
        try {
            int re = dao.insertOneByObject("customerTalesMapper.insertCustomerTale", rmtalePo);
            if (re == 1) {
                // 插入了一条数据,返回插入数据
                String currentDate = DateTimeUtils.nowToSystem();
                
                // 写入历史
                RmHelperUtil.writeHistory(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "New customer tale",
                        rmtalePo.getCtale_maker(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                // 逐条写入历史详细
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale cust number",
                        "",
                        rmtalePo.getCtale_cus_num(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale title",
                        "",
                        rmtalePo.getCtale_title(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale happen date",
                        "",
                        rmtalePo.getCtale_hpn_date(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale mark",
                        "",
                        rmtalePo.getCtale_mark(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale maint date",
                        "",
                        rmtalePo.getCtale_main_date(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale maker",
                        "",
                        rmtalePo.getCtale_maker(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
    
                map = MapAndObjectUtils.copyPropertiesToMap(rmtalePo, map);
                map.put("ctale_resp", ERR_NORMAL);
                return map;
            } else {
                map = MapAndObjectUtils.copyPropertiesToMap(rmtalePo, map);
                map.put("ctale_resp", ERR_TALE_CREATE_FAIL);
                return map;
            }
        } catch (DuplicateKeyException dupkeyException) {
            map = MapAndObjectUtils.copyPropertiesToMap(rmtalePo, map);
            map.put("ctale_resp", ERR_ITEM_EXIST);
            return map;
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public RmtalePo taleInq(RmtalePo rmtalePo) throws Exception {
        return (RmtalePo) dao.selectOneObject("customerTalesMapper.selectCustomerTale", rmtalePo);
    }
    
    @Override
    public Map<String, Object> taleUpd(RmtalePo rmtalePo) throws Exception {
        Map<String, Object> map = new HashMap<>();
        
        RmtalePo origData = taleInq(rmtalePo);
        
        if (null == origData) {
            map.put("ctale_resp", ERR_TALE_NOT_FOUND);
            return map;
        }
        
        // 对比新数据与原数据，无变化直接返回，不修改
        if (RmHelperUtil.compare(origData, rmtalePo) == COMPARE_SAME) {
            map = MapAndObjectUtils.copyPropertiesToMap(rmtalePo, map);
            map.put("ctale_resp", ERR_NOTHING_UPDATED);
            return map;
        }

        int re = dao.updateByObject("customerTalesMapper.updateCustomerTale", rmtalePo);
        
        if (re == 1) {
            // 插入了一条数据,返回插入数据
            
            String currentDate = DateTimeUtils.nowToSystem();
            
            // 写入历史
            RmHelperUtil.writeHistory(rmtalePo.getCtale_cus_num(),
                    currentDate,
                    "Upd customer tale",
                    rmtalePo.getCtale_maker(),
                    "",
                    currentDate,
                    rmtalePo.getCreate_user(),
                    currentDate,
                    rmtalePo.getCreate_user(),
                    dao);
            
            // 逐条写入历史详细
            if (!StringUtils.isBlank(rmtalePo.getCtale_title()) &&
                    origData.getCtale_title().equals(rmtalePo.getCtale_title())) {
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale title",
                        origData.getCtale_title(),
                        rmtalePo.getCtale_title(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmtalePo.getCtale_hpn_date()) &&
                    origData.getCtale_hpn_date().equals(rmtalePo.getCtale_hpn_date())) {
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale happen date",
                        origData.getCtale_hpn_date(),
                        rmtalePo.getCtale_hpn_date(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
            }
            
            
            if (!StringUtils.isBlank(rmtalePo.getCtale_mark()) &&
                    origData.getCtale_mark().equals(rmtalePo.getCtale_mark())) {
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale mark",
                        origData.getCtale_mark(),
                        rmtalePo.getCtale_mark(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmtalePo.getCtale_main_date()) &&
                    origData.getCtale_main_date().equals(rmtalePo.getCtale_main_date())) {
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale maint date",
                        origData.getCtale_main_date(),
                        rmtalePo.getCtale_main_date(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmtalePo.getCtale_maker()) &&
                    origData.getCtale_maker().equals(rmtalePo.getCtale_maker())) {
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale maker",
                        origData.getCtale_maker(),
                        rmtalePo.getCtale_maker(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
            }
    
            map = MapAndObjectUtils.copyPropertiesToMap(rmtalePo, map);
            map.put("ctale_resp", ERR_NORMAL);
            return map;
        } else {
            map = MapAndObjectUtils.copyPropertiesToMap(rmtalePo, map);
            map.put("ctale_resp", ERR_TALE_UPDATE_FAIL);
            return map;
        }
    }
    
    @Override
    public Map<String, Object> taleDel(RmtalePo rmtalePo) throws Exception {
        RmtalePo origData = taleInq(rmtalePo);
        
        Map<String, Object> resultMap = new HashMap<>();
        
        if (null != origData) {
            
            int re = dao.updateByObject("customerTalesMapper.deleteCustomerTale", rmtalePo);
            
            if (re == 1) {
                String currentDate = DateTimeUtils.nowToSystem();
                
                // 写入历史
                RmHelperUtil.writeHistory(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Del customer tale",
                        rmtalePo.getCtale_maker(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                // 逐条写入历史详细
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale cust number",
                        origData.getCtale_cus_num(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale title",
                        origData.getCtale_title(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale happen date",
                        origData.getCtale_hpn_date(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale mark",
                        origData.getCtale_mark(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale maint date",
                        origData.getCtale_main_date(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                
                RmHelperUtil.writeHistoryItem(rmtalePo.getCtale_cus_num(),
                        currentDate,
                        "Tale maker",
                        origData.getCtale_maker(),
                        "",
                        currentDate,
                        rmtalePo.getCreate_user(),
                        currentDate,
                        rmtalePo.getCreate_user(),
                        dao);
                resultMap.put("ctale_resp", ERR_NORMAL);
            } else {
                resultMap.put("ctale_resp", ERR_TALE_NOT_FOUND);
            }
            return resultMap;
        } else {
            resultMap.put("ctale_resp", ERR_TALE_NOT_FOUND);
            return resultMap;
        }
    }
    
    /**
     * 大事件列表查询
     *
     * @param map 查询条件
     * @return map 查询结果
     * @throws Exception
     */
    @Override
    public Map<String, Object> taleList(Map<String, Object> map) throws Exception {
        log.info("RM - 大事件列表查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("customerTalesMapper.queryCustomerTale_count", map);
        
        map = QueryListUtils.changeInputData(map, total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("customerTalesMapper.queryCustomerTale", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        if (total != 0) {
            resultMap.put("st_title", list.get(list.size() - 1).get("ctaleqo_title"));
            resultMap.put("st_hpdate", list.get(list.size() - 1).get("hpn_date"));
            
        } else {
            resultMap.put("st_title", "");
            resultMap.put("st_hpdate", "");
        }
        resultMap.put("all_count", total);
        return resultMap;
    }
}
