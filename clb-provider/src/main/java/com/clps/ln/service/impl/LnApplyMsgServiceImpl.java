package com.clps.ln.service.impl;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;

import com.clps.core.sys.util.DateTimeUtils;
import com.clps.ln.pojo.LnApplyPo;
import com.clps.ln.service.LnApplyMsgService;
@Component
@Service(version="1.0.0")
public class LnApplyMsgServiceImpl extends BaseService implements LnApplyMsgService{
    
	// 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());
    
    /**
     * LN - 贷款客户申请书申请
     * @param "申请书信息字段" map
     * @return "申请书信息字段" map
     * @throws Exception
     */
	@Override
	public LnApplyPo insertService(LnApplyPo lnapplypo) throws Exception {
			// 记录日志
					log.info("调用插入服务实现");
					//map.put("create_time", DateTimeUtils.nowToSystem());
					//map.put("update_time", DateTimeUtils.nowToSystem());
					int re = dao.insertOneByObject("lnApplyMsgMapper.insertApply", lnapplypo);
					if (re == 1) {
						// 插入了一条数据,返回插入数据
						return lnapplypo;
					} else {
						// 没有插入,返回空
						return null;
					}
		}
}



