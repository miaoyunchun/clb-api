package com.clps.rm.service.util;

import com.clps.core.common.data.dao.Dao;
import com.clps.rm.pojo.RmhistPo;
import com.clps.rm.pojo.RmitemPo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * RM - 工具类 <br>
 * 工具：<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;(1) 检查Map中指定key是否存在对应value <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;(2) 写入交易历史 <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;(3) 写入交易历史明细 <br>
 * 
 */
public class RmHelperUtil {

	private static int COMPARE_NOT_SAME = 0;
	private static int COMPARE_SAME = 1;

	/**
	 * 写入操作历史
	 * 
	 * @param cust
	 *            客户号
	 * @param time
	 *            操作日期时间
	 * @param trans
	 *            执行的交易
	 * @param maker
	 *            执行人
	 * @param mark
	 *            备注
	 * @param createTime
	 *            条目创建时间
	 * @param createUser
	 *            条目创建人
	 * @param updateTime
	 *            更新时间
	 * @param updateUser
	 *            更新人
	 * @param dao
	 *            DAO引用
	 * @return true = 执行成功，false = 执行失败
	 * @throws Exception
	 */
	public static boolean writeHistory(String cust, String time, String trans, String maker, String mark,
			String createTime, String createUser, String updateTime, String updateUser, Dao dao) throws Exception {
		RmhistPo rmhistPo = new RmhistPo(cust, time, trans, maker, mark, "N", createTime, createUser, updateTime,
				updateUser);

		// re为受影响的行数，若添加成功则为1
		int re = dao.insertOneByObject("maintHistoryMapper.insertHistory", rmhistPo);
		// re为1说明添加成功
		return re == 1;
	}

	/**
	 * 写入操作明细
	 * 
	 * @param cust
	 *            客户号
	 * @param time
	 *            操作日期时间
	 * @param maint
	 *            被修改条目描述
	 * @param origin
	 *            原值
	 * @param revise
	 *            新值
	 * @param createTime
	 *            创建时间
	 * @param createUser
	 *            创建人
	 * @param updateTime
	 *            更新时间
	 * @param updateUser
	 *            更新人
	 * @param dao
	 *            DAO引用
	 * @return true = 执行成功，false = 执行失败
	 * @throws Exception
	 */
	public static boolean writeHistoryItem(String cust, String time, String maint, String origin, String revise,
			String createTime, String createUser, String updateTime, String updateUser, Dao dao) throws Exception {
		RmitemPo rmitemPo = new RmitemPo(cust, time, maint, origin, revise, "N", createUser, createTime, updateUser,
				updateTime);

		// re为受影响的行数，若添加成功则为1
		int re = dao.insertOneByObject("maintHistoryMapper.insertHistoryItem", rmitemPo);
		// re为1说明添加成功
		return re == 1;
	}

	/**
	 * 比较两个同POJO对象的属性值是否全部相同
	 * 
	 * @param <T> 参数所属的类
	 * 
	 * @param oldObj
	 *            包含旧数据的对象
	 * @param newObj
	 *            包含新数据的对象
	 * @return 0 = 不完全相同，1 = 完全相同
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public static <T> int compare(T oldObj, T newObj) {
		Class<? extends Object> oldCls = oldObj.getClass();
		Class<? extends Object> newCls = newObj.getClass();

		boolean isSame = true;
 
		// 若传入的两个对象不是来自同一个类，则返回
		if (!oldCls.getTypeName().equals(newCls.getTypeName())) {
			return 0;
		}

		Method[] methods = oldCls.getMethods();

		// 遍历oldMethods中所有方法，逐个挑出getter
		for (Method m : methods) {
			if (m.getName().startsWith("get")) {
				try {
					// 如果存在不一致的数据，则标记为不完全相同
					if (!m.invoke(oldObj).equals(m.invoke(newObj))) {
						isSame = false;
					}
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					// Eat it
				}
			}
		}
        
		if (isSame) {
			return COMPARE_SAME;
		} else {
			return COMPARE_NOT_SAME;
		}
	}
}
