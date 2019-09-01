package com.clps.rm.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.MapAndObjectUtils;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.rm.pojo.RmbcusPo;
import com.clps.rm.service.BusinessCustomerService;
import com.clps.rm.service.util.RmHelperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RM - 对公客户相关服务
 *
 * @author Boris Zhao
 * @version v1.0
 * @since 2017-04-07 下午2:23:22
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class BusinessCustomerServiceImpl extends BaseService implements BusinessCustomerService {
    
    // 错误码
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_CUSTOMER_EXIST = "0001";
    private static final String ERR_NECESSARY_FIELD_EMPTY = "0002";
    private static final String ERR_CUST_UPDATE_FAIL = "0003";
    private static final String ERR_NOTHING_UPDATED = "0005";
    private static final String ERR_CUSTOMER_NOT_FOUND = "0006";
    private static final String ERR_UNHANDLED_EXCEPTION = "9999";
    
    // 新旧数据对比结果
    private static final int CMP_RES_SAME = 1;
    
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    // The returning map
    Map<String, Object> returnMap;
    
    @Override
    public Map<String, Object> businessAccountAdd(RmbcusPo rmbcusPo) throws Exception {
        log.info("RM - 增加对公客户");
        
        String currentDate = DateTimeUtils.changeToDate();
        String currentDateTime = DateTimeUtils.nowToSystem();
        
        returnMap = new HashMap<>();
        
        // Response code
        int re;
        
        try {
            re = dao.insertOneByObject("businessAccountMapper.insertAccount", rmbcusPo);
        } catch (DuplicateKeyException ex) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmbcusPo, returnMap);
            returnMap.put("resp_code", ERR_CUSTOMER_EXIST);
            return returnMap;
        }
        
        if (re == 1) {
            // 插入了一条数据,返回插入数据
            // 写入历史
            RmHelperUtil.writeHistory(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "New business account",
                    rmbcusPo.getBcust_maker(),
                    "",
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            // 逐条写入历史详细
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust number",
                    "",
                    rmbcusPo.getBcust_id(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust type",
                    "",
                    rmbcusPo.getBcust_type(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust org ID",
                    "",
                    rmbcusPo.getBcust_org_id(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust name",
                    "",
                    rmbcusPo.getBcust_name(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust English name",
                    "",
                    rmbcusPo.getBcust_ename(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust org number",
                    "",
                    rmbcusPo.getBcust_org_number(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust address",
                    "",
                    rmbcusPo.getBcust_address(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            if (null != rmbcusPo.getBcust_address2()) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust address2",
                        "",
                        rmbcusPo.getBcust_address2(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_address3()) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust address3",
                        "",
                        rmbcusPo.getBcust_address3(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        dao);
            }
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust zip code",
                    "",
                    rmbcusPo.getBcust_zip_code(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust tel number",
                    "",
                    rmbcusPo.getBcust_tel_nbr(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust fax number",
                    "",
                    rmbcusPo.getBcust_fax_nbr(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust email",
                    "",
                    rmbcusPo.getBcust_email(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust region",
                    "",
                    rmbcusPo.getBcust_region_lvl(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Industry type",
                    "",
                    rmbcusPo.getBcust_indust_type(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Ownership type",
                    "",
                    rmbcusPo.getBcust_ownshp_type(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Cust location",
                    "",
                    rmbcusPo.getBcust_area(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Law representer",
                    "",
                    rmbcusPo.getBcust_law_repter(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Auth representer",
                    "",
                    rmbcusPo.getBcust_auth_repter(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Fin representer",
                    "",
                    rmbcusPo.getBcust_fin_repter(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Buss representer",
                    "",
                    rmbcusPo.getBcust_buss_repter(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Business license",
                    "",
                    rmbcusPo.getBcust_license(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Expiration date",
                    "",
                    rmbcusPo.getBcust_lcs_expdate(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            if (null != rmbcusPo.getBcust_fx_nbr()) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Forex number",
                        "",
                        rmbcusPo.getBcust_fx_nbr(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_loan_nbr()) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Loan number",
                        "",
                        rmbcusPo.getBcust_loan_nbr(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_loan_card()) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Loan card",
                        "",
                        rmbcusPo.getBcust_loan_card(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_bus_scope()) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Business scope",
                        "",
                        rmbcusPo.getBcust_bus_scope(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        dao);
            }
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Reg capital",
                    "",
                    rmbcusPo.getBcust_regi_cap().toString(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Reg currency",
                    "",
                    rmbcusPo.getBcust_regi_ccy(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Approver",
                    "",
                    rmbcusPo.getBcust_approve_name(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Stk holder indicator",
                    "",
                    rmbcusPo.getBcust_stk_hldr_ind(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Rank",
                    "",
                    rmbcusPo.getBcust_rank(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Password indicator",
                    "",
                    rmbcusPo.getBcust_psw_ind(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            if (null != rmbcusPo.getBcust_cred_assess()) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Credit assess",
                        "",
                        rmbcusPo.getBcust_cred_assess(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        currentDateTime,
                        rmbcusPo.getCreate_user(),
                        dao);
            }
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Last maint date",
                    "",
                    rmbcusPo.getBcust_lst_mn_date(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Maker",
                    "",
                    rmbcusPo.getBcust_maker(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    currentDateTime,
                    rmbcusPo.getCreate_user(),
                    dao);
            
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmbcusPo, returnMap);
            returnMap.put("resp_code", ERR_NORMAL);
            return returnMap;
        } else {
            // 没有插入,返回数据为空，返回值为ERR_UNHANDLED_EXCEPTION
            returnMap = new HashMap<>();
            returnMap.put("resp_code", ERR_UNHANDLED_EXCEPTION);
            return returnMap;
        }
        
    }
    
    @Override
    public RmbcusPo businessAccountInq(RmbcusPo rmbcusPo) throws Exception {
        rmbcusPo = (RmbcusPo) dao.selectOneObject("businessAccountMapper.selectAccount", rmbcusPo);
        return rmbcusPo;
    }
    
    @Override
    public Map<String, Object> businessAccountUpd(RmbcusPo rmbcusPo) throws Exception {
        
        String currentYyyyMmDd = DateTimeUtils.changeToDate();
        String currentDateTime = DateTimeUtils.nowToSystem();
        
        returnMap = new HashMap<>();
        
        RmbcusPo origData = businessAccountInq(rmbcusPo);
        
        // 如果不存在对应旧数据，返回客户不存在错误
        if (null == origData) {
            returnMap.put("resp_code", ERR_CUSTOMER_NOT_FOUND);
            return returnMap;
        }
        
        // 对比新数据与原数据，无变化直接返回，不修改
        if (RmHelperUtil.compare(origData, rmbcusPo) == CMP_RES_SAME) {
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmbcusPo, returnMap);
            returnMap.put("resp_code", ERR_NOTHING_UPDATED);
            return returnMap;
        }
        
        rmbcusPo.setBcust_lst_mn_date(currentYyyyMmDd);
        
        
        // 否则根据新的数据执行更新操作，re为变更成功的条目数
        // 因为是单条更新，所以成功时re为1，不成功时re为0
        int re = dao.updateByObject("businessAccountMapper.updateAccount", rmbcusPo);
        
        if (re == 1) {
            // 写入历史
            RmHelperUtil.writeHistory(rmbcusPo.getBcust_id(),
                    currentDateTime,
                    "Upd customer info",
                    rmbcusPo.getBcust_maker(),
                    "",
                    currentDateTime,
                    rmbcusPo.getUpdate_user(),
                    currentDateTime,
                    rmbcusPo.getUpdate_user(),
                    dao);
            
            // 逐条写入历史详细
            // I know this part sucks. And I really feel sorry about this.
            // Honestly.
            // Hope this shit can be optimized by some genius someday.
            
            if (null != rmbcusPo.getBcust_org_id() && !origData.getBcust_org_id().equals(rmbcusPo.getBcust_org_id())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust org id",
                        origData.getBcust_org_id(),
                        rmbcusPo.getBcust_org_id(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_name() && !origData.getBcust_name().equals(rmbcusPo.getBcust_name())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust name",
                        origData.getBcust_name(),
                        rmbcusPo.getBcust_name(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_ename() && !origData.getBcust_ename().equals(rmbcusPo.getBcust_ename())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust English name",
                        origData.getBcust_ename(),
                        rmbcusPo.getBcust_ename(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_org_number()
                    && !origData.getBcust_org_number().equals(rmbcusPo.getBcust_org_number())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust org number",
                        origData.getBcust_org_number(),
                        rmbcusPo.getBcust_org_number(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_address()
                    && !origData.getBcust_address().equals(rmbcusPo.getBcust_address())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust address",
                        origData.getBcust_address(),
                        rmbcusPo.getBcust_address(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_address2()
                    && !origData.getBcust_address2().equals(rmbcusPo.getBcust_address2())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust address2",
                        origData.getBcust_address2(),
                        rmbcusPo.getBcust_address2(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_address3()
                    && !origData.getBcust_address3().equals(rmbcusPo.getBcust_address3())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust address3",
                        origData.getBcust_address3(),
                        rmbcusPo.getBcust_address3(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_zip_code()
                    && !origData.getBcust_zip_code().equals(rmbcusPo.getBcust_zip_code())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust zip code",
                        origData.getBcust_zip_code(),
                        rmbcusPo.getBcust_zip_code(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_tel_nbr()
                    && !origData.getBcust_tel_nbr().equals(rmbcusPo.getBcust_tel_nbr())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust tel nbr",
                        origData.getBcust_tel_nbr(),
                        rmbcusPo.getBcust_tel_nbr(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_fax_nbr()
                    && !origData.getBcust_fax_nbr().equals(rmbcusPo.getBcust_fax_nbr())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust fax nbr",
                        origData.getBcust_fax_nbr(),
                        rmbcusPo.getBcust_fax_nbr(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_email() && !origData.getBcust_email().equals(rmbcusPo.getBcust_email())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Cust email",
                        origData.getBcust_email(),
                        rmbcusPo.getBcust_email(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_region_lvl()
                    && !origData.getBcust_region_lvl().equals(rmbcusPo.getBcust_region_lvl())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Region level",
                        origData.getBcust_region_lvl(),
                        rmbcusPo.getBcust_region_lvl(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_indust_type()
                    && !origData.getBcust_indust_type().equals(rmbcusPo.getBcust_indust_type())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Industry type",
                        origData.getBcust_indust_type(),
                        rmbcusPo.getBcust_indust_type(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_ownshp_type()
                    && !origData.getBcust_ownshp_type().equals(rmbcusPo.getBcust_ownshp_type())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Ownership type",
                        origData.getBcust_ownshp_type(),
                        rmbcusPo.getBcust_ownshp_type(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_area() && !origData.getBcust_area().equals(rmbcusPo.getBcust_area())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Customer location",
                        origData.getBcust_area(),
                        rmbcusPo.getBcust_area(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_law_repter()
                    && !origData.getBcust_law_repter().equals(rmbcusPo.getBcust_law_repter())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Law rptr",
                        origData.getBcust_law_repter(),
                        rmbcusPo.getBcust_law_repter(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_auth_repter()
                    && !origData.getBcust_auth_repter().equals(rmbcusPo.getBcust_auth_repter())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Auth rptr",
                        origData.getBcust_auth_repter(),
                        rmbcusPo.getBcust_auth_repter(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_fin_repter()
                    && !origData.getBcust_fin_repter().equals(rmbcusPo.getBcust_fin_repter())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Financial rptr",
                        origData.getBcust_fin_repter(),
                        rmbcusPo.getBcust_fin_repter(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_buss_repter()
                    && !origData.getBcust_buss_repter().equals(rmbcusPo.getBcust_buss_repter())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Business rptr",
                        origData.getBcust_buss_repter(),
                        rmbcusPo.getBcust_buss_repter(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_license()
                    && !origData.getBcust_license().equals(rmbcusPo.getBcust_license())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "License number",
                        origData.getBcust_license(),
                        rmbcusPo.getBcust_license(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_lcs_expdate()
                    && !origData.getBcust_lcs_expdate().equals(rmbcusPo.getBcust_lcs_expdate())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "License exp. date",
                        origData.getBcust_lcs_expdate(),
                        rmbcusPo.getBcust_lcs_expdate(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_fx_nbr() && !origData.getBcust_fx_nbr().equals(rmbcusPo.getBcust_fx_nbr())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "FOREX number",
                        origData.getBcust_fx_nbr(),
                        rmbcusPo.getBcust_fx_nbr(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_loan_nbr()
                    && !origData.getBcust_loan_nbr().equals(rmbcusPo.getBcust_loan_nbr())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Loan number",
                        origData.getBcust_loan_nbr(),
                        rmbcusPo.getBcust_loan_nbr(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_loan_card()
                    && !origData.getBcust_loan_card().equals(rmbcusPo.getBcust_loan_card())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Loan card",
                        origData.getBcust_loan_card(),
                        rmbcusPo.getBcust_loan_card(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_bus_scope()
                    && !origData.getBcust_bus_scope().equals(rmbcusPo.getBcust_bus_scope())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Business scope",
                        origData.getBcust_bus_scope(),
                        rmbcusPo.getBcust_bus_scope(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_regi_cap()
                    && !origData.getBcust_regi_cap().equals(rmbcusPo.getBcust_regi_cap())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Reg. capital",
                        String.valueOf(origData.getBcust_regi_cap()),
                        String.valueOf(rmbcusPo.getBcust_regi_cap()),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_regi_ccy()
                    && !origData.getBcust_regi_ccy().equals(rmbcusPo.getBcust_regi_ccy())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Reg. currency",
                        origData.getBcust_regi_ccy(),
                        rmbcusPo.getBcust_regi_ccy(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_approve_name()
                    && !origData.getBcust_approve_name().equals(rmbcusPo.getBcust_approve_name())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Approval",
                        origData.getBcust_approve_name(),
                        rmbcusPo.getBcust_approve_name(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_stk_hldr_ind()
                    && !origData.getBcust_stk_hldr_ind().equals(rmbcusPo.getBcust_stk_hldr_ind())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Stk. holder ind.",
                        origData.getBcust_stk_hldr_ind(),
                        rmbcusPo.getBcust_stk_hldr_ind(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_rank() && !origData.getBcust_rank().equals(rmbcusPo.getBcust_rank())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Rank",
                        origData.getBcust_rank(),
                        rmbcusPo.getBcust_rank(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_psw_ind()
                    && !origData.getBcust_psw_ind().equals(rmbcusPo.getBcust_psw_ind())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Password ind.",
                        origData.getBcust_psw_ind(),
                        rmbcusPo.getBcust_psw_ind(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_cred_assess()
                    && !origData.getBcust_cred_assess().equals(rmbcusPo.getBcust_cred_assess())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Credit assess",
                        origData.getBcust_cred_assess(),
                        rmbcusPo.getBcust_cred_assess(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_lst_mn_date()
                    && !origData.getBcust_lst_mn_date().equals(rmbcusPo.getBcust_lst_mn_date())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Last maint date",
                        origData.getBcust_lst_mn_date(),
                        rmbcusPo.getBcust_lst_mn_date(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
            
            if (null != rmbcusPo.getBcust_maker() && !origData.getBcust_maker().equals(rmbcusPo.getBcust_maker())) {
                RmHelperUtil.writeHistoryItem(rmbcusPo.getBcust_id(),
                        currentDateTime,
                        "Maker",
                        origData.getBcust_maker(),
                        rmbcusPo.getBcust_maker(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        currentDateTime,
                        rmbcusPo.getUpdate_user(),
                        dao);
            }
    
            returnMap = MapAndObjectUtils.copyPropertiesToMap(rmbcusPo, returnMap);
            returnMap.put("resp_code", ERR_NORMAL);
            return returnMap;
        } else {
            returnMap.put("resp_code", ERR_CUST_UPDATE_FAIL);
            return returnMap;
        }
    }
    
    @Override
    public Map<String, Object> businessAccountNameInq(Map<String, Object> map) throws Exception {
        log.info("RM - 对公客户姓名查询");
        
        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("businessAccountMapper.queryCustomerByName_count", map);
        
        map = QueryListUtils.changeInputData(map, total);
        
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("businessAccountMapper.queryCustomerByName", map);
        
        // 返回map
        Map<String, Object> resultMap = QueryListUtils.changeDataForCLB(list, total, null);
        
        if (total != 0) {
            resultMap.put("nxt_cust", list.get(list.size() - 1).get("number"));
        } else {
            resultMap.put("nxt_cust", "");
        }
        
        return resultMap;
    }
}
