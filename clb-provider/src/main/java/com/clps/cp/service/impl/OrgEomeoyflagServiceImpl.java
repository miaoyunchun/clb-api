package com.clps.cp.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.cp.service.OrgEomeoyflagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class OrgEomeoyflagServiceImpl extends BaseService implements OrgEomeoyflagService {
    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    /**
     * 查询机构部分执行日期标志下拉框的标志位和标志位表达的含义
     * @return
     * @throws Exception
     */
    @Override
    public Map<String, Object> queryAllEomeoyFlag() throws Exception {
        List<Map<String, Object>> list = dao.selectListMap("cpEomEoyFlagMapper.queryEomEoyFlag",null);
        System.out.println(list);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list",list);
        return data;
    }

//    public static void main(String[] args) {
//        OrgEomeoyflagServiceImpl oi = new OrgEomeoyflagServiceImpl();
//        try {
//            System.out.println(oi.queryAllEomeoyFlag());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
