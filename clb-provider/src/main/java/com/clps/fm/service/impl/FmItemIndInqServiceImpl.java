package com.clps.fm.service.impl;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.fm.pojo.FmInfoPo;
import com.clps.fm.service.FmItemInfInqService;


@Component
@Transactional
@Service(version="1.0.0")
public class FmItemIndInqServiceImpl extends BaseService implements FmItemInfInqService{
	 /**
     * FM - 科目信息查询
     * @param map 科目号  科目级别
     * @return map 科目信息查询
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public FmInfoPo ItemInformationInq(FmInfoPo fminfo) throws Exception {
        log.info("FM - 科目信息查询");
        return  (FmInfoPo) dao.selectOneObject("FmItemInfInqMapper.selectItemInformation", fminfo);
               
        
    }
	

}
