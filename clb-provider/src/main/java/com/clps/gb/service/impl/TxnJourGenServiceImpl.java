package com.clps.gb.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.clps.core.common.service.BaseService;
import com.clps.core.sys.util.DateTimeUtils;
import com.clps.core.sys.util.StringUtils;
import com.clps.gb.service.TxnJourGenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * GB - 流水号生成相关服务
 *
 * Be careful, adventurer. Here be dragons.
 */
@Component
@Transactional // spring事务注解
@Service(version = "1.0.0") // 分布式服务注解和版本号
public class TxnJourGenServiceImpl extends BaseService implements TxnJourGenService {

    // 日志对象
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    private static final int TOTAL_LENGTH = 19;
    private static final int RESERVE_LENGTH = 1;

    // 错误码定义
    private static final String ERR_NORMAL = "0000";
    private static final String ERR_LENGTH_TOO_LONG = "0001";
    private static final String ERR_HEADER_TOO_LONG = "0002";
    private static final String ERR_DB_UPDATE_FAIL = "0003";
    private static final String ERR_CREATE_RULE_FAIL = "0004";

    /**
     * GB - 生成流水号
     *
     * @param map 流水号生成条件
     *      <br> 包括
     *      <br> (1) initial 前缀
     *      <br> (2) length 总长度(前缀+号码)
     * @return map 流水号生成结果
     *      <br>包括：
     *      <br>(1) jourNbr 流水号(仅当生成成功时才存在)
     *      <br>(2) successful 成功状态
     *      <br>(3) resp_code 返回码
     *      <br>(4) resp_msg 错误信息(仅当生成失败时才存在)
     *      <br>
     *      <br>错误码：
     *      <br>(1) 0000 = 正常
     *      <br>(2) 0001 = 指定长度超出接受范围
     *      <br>(3) 0002 = 指定前缀长度超出范围
     *      <br>(4) 0003 = 数据库更新失败
     *      <br>(5) 0004 = 规则新建失败
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> txnJourGen(Map<String, Object> map) throws Exception {
        log.info("GB - 生成流水号");

        map.put("create_time", DateTimeUtils.nowToSystem());
        map.put("update_time", DateTimeUtils.nowToSystem());

        // 检查输入合法性，流水号最长接受19位
        if (map.get("length") != null && !map.get("length").equals("")) {
            if ((Integer.parseInt(map.get("length").toString()) > TOTAL_LENGTH ||
                    Integer.parseInt(map.get("length").toString()) <= 0)) {
                map.put("successful", "false");
                map.put("resp_code", ERR_LENGTH_TOO_LONG);
                map.put("resp_msg", "流水号长度超过限制，最多接受19位，最少需要1位");
                return map;
            }
        }

        // 首先假定待查号码无前缀
        map.put("header_length", "0");

        // 检查前缀
        // 前缀最大长度 = 总长度 - 数字位预留长度
        if (map.get("initial") != null) {
            if (map.get("initial").toString().length() > (TOTAL_LENGTH - RESERVE_LENGTH)) {
                // 前缀超长，报错结束
                map.put("successful", "false");
                map.put("resp_code", ERR_HEADER_TOO_LONG);
                map.put("resp_msg", "流水号前缀超过限制，最多接受18位");
                return map;
            } else if (map.get("initial").toString().length() > 0) {
                // 若存在前缀且符合条件，则插入查询条件
                map.put("header_length", map.get("initial").toString().length());
            }
        } else {
            // 确实没前缀的话，就这么指定搜索条件
            map.put("initial", "");
            map.put("header_length", "0");
        }

        // 库操作返回码
        int re = -1;

        // 先查询是否存在符合要求的规则
        Map<String, Object> jourNbrRule = (Map<String, Object>) dao.selectOneMap("journalNumberMapper.selectJourNbr", map);

        if (jourNbrRule != null) {
            // map非空，则存在符合要求的规则条目

            // 取出本次用到的流水号，放进输出用的map
            map.put("jour_nbr", jourNbrRule.get("current_number"));

            // 计算下一个流水号
            int headerLength = 0;
            String initial = "";

            // 得到前缀，计算前缀长度
            if (map.get("initial") != null && !map.get("initial").equals("")) {
                initial = map.get("initial").toString().trim();
                headerLength = initial.length();
            }

            // 取得流水号有效数字
            Integer intNextNumber = Integer.parseInt(jourNbrRule.get("next_number").toString().substring(headerLength));
            Integer intCurrNbr = intNextNumber++;

            // 拼接当前流水号数字位
            String strCurrentNumber = StringUtils.completeString(initial, Integer.parseInt(map.get("length").toString()) - intCurrNbr.toString().length(), '0', false);
            StringBuilder sb = new StringBuilder(TOTAL_LENGTH);
            sb.append(strCurrentNumber).append(intCurrNbr.toString());
            jourNbrRule.put("current_number", sb.toString());


            // 清空StringBuilder
            sb.delete(0, sb.length());

            // 拼接下一流水号数字位
            String strNextNumber = StringUtils.completeString(initial, Integer.parseInt(map.get("length").toString()) - intNextNumber.toString().length(), '0', false);
            sb.append(strNextNumber).append(intNextNumber.toString());
            jourNbrRule.put("next_number", sb.toString());

            re = dao.updateByMap("journalNumberMapper.genNewJourNbr", jourNbrRule);

            // 仅当更新成功后才返回结果，避免因更新失败导致流水号重复
            if (re == 1) {
                map.put("successful", "true");
                map.put("resp_code", ERR_NORMAL);
                return map;
            } else {
                // 若更新失败则删除map中的流水号
                map.remove("jour_nbr");
                map.put("successful", "false");
                map.put("resp_code", ERR_DB_UPDATE_FAIL);
                map.put("resp_msg", "流水号记录更新失败");
                return map;
            }
        } else {
            // map空，无符合条件的生成规则，那么创建新规则
            Map<String, Object> newJourRule = new HashMap<String, Object>();

            String initial = "";

            if (map.get("initial") != null && !map.get("initial").equals("")) {
                initial = map.get("initial").toString();
            }

            StringBuilder sb = new StringBuilder(TOTAL_LENGTH);

            // 计算补齐零的数量，留出最后一位填1
            int jourLength = Integer.parseInt(map.get("length").toString()) - initial.length() - RESERVE_LENGTH;
            // 拼接流水号
            String initialRule = StringUtils.completeString(initial, Integer.parseInt(map.get("length").toString()) - 1, '0', false);
            sb.append(initialRule);

            // 这个是本次要输出的流水号
            String jourNbr = sb.toString() + "1";

            // 然后这俩是要记录到库里的两个流水号
            String current_number = sb.toString() + "2";
            String next_number = sb.toString() + "3";

            // 构建新规则条目
            newJourRule.put("length", map.get("length"));
            newJourRule.put("header_length", initial.length());
            newJourRule.put("current_number", current_number);
            newJourRule.put("next_number", next_number);
            newJourRule.put("create_user", map.get("create_user"));
            newJourRule.put("update_user", map.get("update_user"));
            newJourRule.put("create_time", map.get("create_time"));
            newJourRule.put("update_time", map.get("update_time"));
            // 并插入数据库
            re = dao.insertOneByMap("journalNumberMapper.genNewJourNbrRule", newJourRule);

            if (re == 1) {
                map.put("jour_nbr", jourNbr);
                map.put("successful", "true");
                map.put("resp_code", ERR_NORMAL);
                return map;
            } else {
                map.put("successful", "false");
                map.put("resp_code", ERR_CREATE_RULE_FAIL);
                map.put("resp_msg", "流水号规则生成失败，未生成流水号");
                return map;
            }
        }
    }
}
