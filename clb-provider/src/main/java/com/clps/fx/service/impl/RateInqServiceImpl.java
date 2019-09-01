package com.clps.fx.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.clps.core.sys.util.DateTimeUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.QueryListUtils;
import com.clps.fx.pojo.FxRatePo;
import com.clps.fx.service.RateInqService;

/**
 * 分布式服务接口实现
 *
 * @author snow
 */
@Component // 注解 2017年2月8日添加
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class RateInqServiceImpl extends BaseService implements RateInqService{
	
	private Double 	fx_cash_rate;
	private Double 	fx_acct_rate;
	 Map<String, Object> reslutMap = new HashMap<String, Object>();
    @Transactional(readOnly = true)//只读事务
    @Override
    public Map<String, Object> RateListInq(Map<String, Object> map) throws Exception {

        // 记录日志
        log.info("汇率列表查询");

        // 处理日期范围
        QueryListUtils.changeTimeSearch(map, "update_time", "create_time");
        // 查询总条数
        Long total = (Long) dao.selectOneObject("RateInqMapper.RateListCount", map);
        // 查询总数据
        List<Map<String, Object>> list = dao.selectListMap("RateInqMapper.RateListInq", map);

        // 处理返回
        Map<String, Object> reMap = QueryListUtils.changeReturnDate(list, total, QueryListUtils.createPageDataMap(map,null,total));
        // 返回数据
        return reMap;
    }

    @Transactional(readOnly = true)//只读事务
    @Override
    public Map<String, Object> RateInq(FxRatePo fxrate) throws Exception {

        // 记录日志
        log.info("汇率单条查询");
        //套汇
        if (!fxrate.getFx_buy_ccy().equals("CNY") && !fxrate.getFx_sell_ccy().equals("CNY")){
            Map<String,Object> buyRateMap= (Map<String, Object>) dao.selectOneObject("RateInqMapper.buyRateInq", fxrate);
            Map<String,Object> sellRateMap= (Map<String, Object>) dao.selectOneObject("RateInqMapper.sellRateInq", fxrate);
             fx_cash_rate = Double.parseDouble(buyRateMap.get("cash_buying_rate").toString()) / Double.parseDouble(sellRateMap.get("selling_rate").toString());
             fx_acct_rate = Double.parseDouble(buyRateMap.get("buying_rate").toString()) / Double.parseDouble(sellRateMap.get("selling_rate").toString());
             reslutMap.put("fx_cash_rate",fx_cash_rate);
             reslutMap.put("fx_acct_rate",fx_acct_rate);

        }

        //购汇
        if (fxrate.getFx_buy_ccy().equals("CNY") && !fxrate.getFx_sell_ccy().equals("CNY")){
            Map<String,Object> sellRateMap= (Map<String, Object>) dao.selectOneObject("RateInqMapper.sellRateInq", fxrate);
            reslutMap.put("fx_cash_rate",sellRateMap.get("selling_rate"));
            reslutMap.put("fx_acct_rate",sellRateMap.get("selling_rate"));

        }

        //结汇
        if (!fxrate.getFx_buy_ccy().equals("CNY") && fxrate.getFx_sell_ccy().equals("CNY")){
            Map<String,Object> buyRateMap= (Map<String, Object>) dao.selectOneObject("RateInqMapper.buyRateInq", fxrate);
            reslutMap.put("fx_cash_rate",buyRateMap.get("selling_rate"));
            reslutMap.put("fx_acct_rate",buyRateMap.get("selling_rate"));

        }
        reslutMap.put("FX_RATE_PUB_DATE", fxrate.getRate_pub_date());
        reslutMap.put("FX_RATE_PUB_TIME", fxrate.getRate_pub_time());
        reslutMap.put("FX_BUY_CCY", fxrate.getFx_buy_ccy());
        reslutMap.put("FX_SELL_CCY", fxrate.getFx_sell_ccy());

        // 返回数据
        return reslutMap;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//非只读事务，支持当前事务(常见)，遇到异常Rollback
    @Override
    public Map<String, Object> RateAdd(Map<String, Object> map) throws Exception {

        // 记录日志
        log.info("汇率更新");
        map.put("create_time", DateTimeUtils.nowToSystem());
        map.put("update_time", DateTimeUtils.nowToSystem());
        List<Map<?,?>> list = new ArrayList<Map<?,?>>();
        list=(List<Map<?,?>>)map.get("listData");
        @SuppressWarnings("unused")
        int re1 = dao.insertOneByObject("RateInqMapper.rateAdd", list);
        if (re1 == 10) {
            // 插入了10条数据,返回插入数据
            return map;
        } else {
            // 没有插入,返回空
            return null;
        }
    }


}
