package com.clps.rm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.rm.pojo.RmrdmnPo;
import com.clps.rm.service.CustomizedInformationService;
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
 * RM - 客户自定义信息服务
 *
 * @author Boris Zhao
 * @version v1.0
 * @since 2017-04-19 15:48:56
 */
@Component
@Transactional // spring事务注解
@Service(version="1.0.0") // 分布式服务注解和版本号
public class CustomizedInformationServiceImpl extends BaseService implements CustomizedInformationService {
    
    // 错误码定义
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_INFO_CREATE_FAIL = "0001";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0002";
    private static final String ERR_ITEM_EXIST = "0003";
    private static final String ERR_INVALID_OPERATION = "0004";
    private static final String ERR_NOTHING_UPDATED = "0005";
    private static final String ERR_INFO_NOT_FOUND = "0006";
    private static final String ERR_INFO_UPDATE_FAIL = "0007";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    @Override
    public Map<String, Object> infoAdd(RmrdmnPo rmrdmnPo) throws Exception {
        log.info("RM - 增加客户自定义信息");
        
        Map<String, Object> returnMap = new HashMap<>();
        
        int re;
        
        try {
            re = dao.insertOneByObject("customizedInformationMapper.insertInfo", rmrdmnPo);
        } catch (DuplicateKeyException dupkeyException) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmrdmnPo, returnMap);
            returnMap.put("resp_code", ERR_ITEM_EXIST);
            return returnMap;
        }
        
        if (re == 1) {
            // 插入了一条数据,返回插入数据
            String currentDate = DateTimeUtils.nowToSystem();
            
            // 写入历史
            RmHelperUtil.writeHistory(rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    "New customer info",
                    rmrdmnPo.getCrdmn_maker(),
                    "",
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    dao);
            
            // 逐条写入历史详细
            RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    "Info cust number",
                    "",
                    rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    "Info title",
                    "",
                    rmrdmnPo.getCrdmn_defined_titl(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    "Info description",
                    "",
                    rmrdmnPo.getCrdmn_defined_desc(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    dao);
            
            if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_defined_content())) {
                RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                        currentDate,
                        "Info content",
                        "",
                        rmrdmnPo.getCrdmn_defined_content(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        dao);
            }
            
            RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    "Info maint date",
                    "",
                    rmrdmnPo.getCrdmn_last_main_date(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    "Info maker",
                    "",
                    rmrdmnPo.getCrdmn_maker(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    dao);
            
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmrdmnPo, returnMap);
            returnMap.put("resp_code", ERR_NORMAL);
        } else {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmrdmnPo, returnMap);
            returnMap.put("resp_code", ERR_INFO_CREATE_FAIL);
        }
        return returnMap;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public RmrdmnPo infoInq(RmrdmnPo rmrdmnPo) throws Exception {
        return (RmrdmnPo) dao.selectOneObject("customizedInformationMapper.selectInfo", rmrdmnPo);
    }
    
    @Override
    public Map<String, Object> infoUpd(RmrdmnPo rmrdmnPo) throws Exception {
        RmrdmnPo origData = infoInq(rmrdmnPo);
        
        Map<String, Object> returnMap = new HashMap<>();
        
        if (null == origData) {
            returnMap.put("resp_code", ERR_INFO_NOT_FOUND);
            return returnMap;
        }
        
        // 对比新数据与原数据，无变化直接返回，不修改
        if (RmHelperUtil.compare(origData, rmrdmnPo) == 1) {
            returnMap.put("resp_code", ERR_NOTHING_UPDATED);
            return returnMap;
        }
        
        int re = dao.updateByObject("customizedInformationMapper.updateInfo", rmrdmnPo);
        
        if (re == 1) {
            // 插入了一条数据,返回插入数据
            
            String currentDate = DateTimeUtils.nowToSystem();
            
            // 写入历史
            RmHelperUtil.writeHistory(rmrdmnPo.getCrdmn_number(),
                    currentDate,
                    "Upd customer info",
                    rmrdmnPo.getCrdmn_maker(),
                    "",
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    currentDate,
                    rmrdmnPo.getCreate_user(),
                    dao);
            
            // 逐条写入历史详细
            if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_defined_titl()) &&
                    !origData.getCrdmn_defined_titl().equals(rmrdmnPo.getCrdmn_defined_titl())) {
                RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                        currentDate,
                        "Info title",
                        origData.getCrdmn_defined_titl(),
                        rmrdmnPo.getCrdmn_defined_titl(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_defined_desc()) &&
                    !origData.getCrdmn_defined_desc().equals(rmrdmnPo.getCrdmn_defined_desc())) {
                RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                        currentDate,
                        "Info description",
                        origData.getCrdmn_defined_desc(),
                        rmrdmnPo.getCrdmn_defined_desc(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        dao);
            }
            
            
            if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_defined_content()) &&
                    !origData.getCrdmn_defined_content().equals(rmrdmnPo.getCrdmn_defined_content())) {
                RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                        currentDate,
                        "Info content",
                        origData.getCrdmn_defined_content(),
                        rmrdmnPo.getCrdmn_defined_content(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_last_main_date()) &&
                    !origData.getCrdmn_last_main_date().equals(rmrdmnPo.getCrdmn_last_main_date())) {
                RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                        currentDate,
                        "Info maint date",
                        origData.getCrdmn_last_main_date(),
                        rmrdmnPo.getCrdmn_last_main_date(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        dao);
            }
            
            if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_maker()) &&
                    !origData.getCrdmn_maker().equals(rmrdmnPo.getCrdmn_maker())) {
                RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                        currentDate,
                        "Info maker",
                        origData.getCrdmn_maker(),
                        rmrdmnPo.getCrdmn_maker(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        dao);
            }
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmrdmnPo, returnMap);
            returnMap.put("resp_code", ERR_NORMAL);
            return returnMap;
        } else {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmrdmnPo, returnMap);
            returnMap.put("resp_code", ERR_INFO_UPDATE_FAIL);
            return returnMap;
        }
    }
    
    @Override
    public Map<String, Object> infoDel(RmrdmnPo rmrdmnPo) throws Exception {
        RmrdmnPo origData = infoInq(rmrdmnPo);
        
        Map<String, Object> returnMap = new HashMap<>();
        
        if (null != origData) {
            
            int re = dao.updateByObject("customizedInformationMapper.deleteInfo", rmrdmnPo);
            
            if (re == 1) {
                // 插入了一条数据,返回插入数据
                
                String currentDate = DateTimeUtils.nowToSystem();
                
                // 写入历史
                RmHelperUtil.writeHistory(rmrdmnPo.getCrdmn_number(),
                        currentDate,
                        "Del customer info",
                        rmrdmnPo.getCrdmn_maker(),
                        "",
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        currentDate,
                        rmrdmnPo.getCreate_user(),
                        dao);
                
                // 逐条写入历史详细
                if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_defined_titl()) &&
                        origData.getCrdmn_defined_titl().equals(rmrdmnPo.getCrdmn_defined_titl())) {
                    RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                            currentDate,
                            "Info title",
                            origData.getCrdmn_defined_titl(),
                            "",
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            dao);
                }
                
                if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_defined_desc()) &&
                        origData.getCrdmn_defined_desc().equals(rmrdmnPo.getCrdmn_defined_desc())) {
                    RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                            currentDate,
                            "Info description",
                            origData.getCrdmn_defined_desc(),
                            "",
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            dao);
                }
                
                
                if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_defined_content()) &&
                        origData.getCrdmn_defined_content().equals(rmrdmnPo.getCrdmn_defined_content())) {
                    RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                            currentDate,
                            "Info content",
                            origData.getCrdmn_defined_content(),
                            "",
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            dao);
                }
                
                if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_last_main_date()) &&
                        origData.getCrdmn_last_main_date().equals(rmrdmnPo.getCrdmn_last_main_date())) {
                    RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                            currentDate,
                            "Info maint date",
                            origData.getCrdmn_last_main_date(),
                            "",
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            dao);
                }
                
                if (!StringUtils.isBlank(rmrdmnPo.getCrdmn_maker()) &&
                        origData.getCrdmn_maker().equals(rmrdmnPo.getCrdmn_maker())) {
                    RmHelperUtil.writeHistoryItem(rmrdmnPo.getCrdmn_number(),
                            currentDate,
                            "Info maker",
                            origData.getCrdmn_maker(),
                            "",
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            currentDate,
                            rmrdmnPo.getCreate_user(),
                            dao);
                }
                
                returnMap.put("resp_code", ERR_NORMAL);
                return returnMap;
            } else {
                returnMap.put("resp_code", ERR_INFO_NOT_FOUND);
            }
            return returnMap;
        } else {
            returnMap.put("resp_code", ERR_INFO_NOT_FOUND);
            return returnMap;
        }
    }
    
    @Override
    public Map<String, Object> infoList(Map<String, Object> map) throws Exception {
        log.info("RM - 自定义信息列表查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("customizedInformationMapper.queryInfo_count", map);
        
        map = QueryListUtils.changeInputData(map, total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("customizedInformationMapper.queryInfo", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map, null, total));
        if (0 != total && 0 != list.size()) {
            resultMap.put("nxt_titl", list.get(list.size() - 1).get("crdmn_defined_titl"));
        } else {
            resultMap.put("nxt_titl", "");
        }
        resultMap.put("all_count", total);
        return resultMap;
    }
}
