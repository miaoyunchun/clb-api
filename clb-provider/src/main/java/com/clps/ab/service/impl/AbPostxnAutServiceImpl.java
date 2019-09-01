package com.clps.ab.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.ab.service.AbPostxnAutService;
import com.clps.core.common.service.BaseService;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Project Name: clb-master
 * Package Name: com.clps.ab.service.impl
 * Description: TODO
 * Create Time: 2017-02-16 10:57
 * Author: Jamie.Chen
 */
@Component
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class AbPostxnAutServiceImpl extends BaseService implements AbPostxnAutService {
    @Override
    public Map<String, Object> postxnAut(Map<String, Object> map) throws Exception {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if(!map.get("card_no").toString().substring(0,6).equals("666666")){
            int radom = radomCreate(0,99);
            if(radom>0 && radom<75){
                resultMap.put("au_resp_code","000");
                resultMap.put("resp_msg","AUTHORIZE SUCCESSFUL");
            }else if(radom>=75&&radom<80){
                resultMap.put("au_resp_code","001");
                resultMap.put("resp_msg","AUTHORIZE FAIL,CARD NOT ACTIVE");
            }else if(radom>=80&&radom<85){
                resultMap.put("au_resp_code","002");
                resultMap.put("resp_msg","AUTHORIZE FAIL, CVV2 NOT CORRECT");
            }else if(radom>=85&&radom<90){
                resultMap.put("au_resp_code","003");
                resultMap.put("resp_msg","AUTHORIZE FAIL, EXPIRY DATE NOT CORECT");
            }else if(radom>=90&&radom<95){
                resultMap.put("au_resp_code","004");
                resultMap.put("resp_msg","AUTHORIZE FAIL, NO AVALIABLE LIMIT");
            }else if(radom>=95&&radom<98){
                resultMap.put("au_resp_code","005");
                resultMap.put("resp_msg","AUTHORIZE FAIL, NO AVALIABLE CASH LIMIT");
            }else{
                resultMap.put("au_resp_code","006");
                resultMap.put("resp_msg","UNKNOWN TRANSACTION CODE");
            }
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String timestamp = dateFormat.format(now);
            resultMap.put("card_no",map.get("card_no"));
            String auth_code = new DecimalFormat("000000").format(radomCreate(0,999999));
            resultMap.put("auth_code",auth_code);
            resultMap.put("timestamp",timestamp);
            String tran_code = map.get("tran_code").toString();
            resultMap.put("tran_ref_num",timestamp+tran_code+auth_code);
        } else {
            String rxpiry_date = "20" + map.get("expiry_date").toString().substring(0,2) + "-" +
                    map.get("expiry_date").toString().substring(2,4) + "-" + "01";
            Map<String, Object> queryMap = new HashMap<String, Object>();
            queryMap.put("card_no",map.get("card_no"));
            queryMap.put("rxpiry_date",rxpiry_date);
            queryMap.put("tran_code",map.get("tran_code"));
            queryMap.put("txn_type",map.get("tran_type"));
            queryMap.put("txn_amount",map.get("pay_amount"));
            queryMap.put("cvv_no",map.get("cvv_no"));
            queryMap.put("tran_desc","申请信用卡发卡行授权");
            queryMap.put("auth_id","0000");
            resultMap = null;//调用 VBS-CI-MANUTRAN-AUT 服务 信用卡发卡行授权(未开发)
        }
        return resultMap;
    }
    //生成随机数
    public int radomCreate(int min, int max) {
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        return s;
    }
}
