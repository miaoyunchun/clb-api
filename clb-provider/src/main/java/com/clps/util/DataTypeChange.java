package com.clps.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.clps.core.sys.util.DateTimeUtils;

/**
 * 
 * ClassName: DecimalCalculate. Function: TODO ADD FUNCTION. Reason: TODO ADD
 * REASON(可选). date: 2017年1月10日 下午3:08:53
 *
 * @author terry.zhang
 * @version
 */
public class DataTypeChange {

	/**
	 * 判断是否为null
	 * 
	 * @param obj
	 * @return true||false
	 */
	public static boolean checkNULL(Object obj) {
		if (obj == null) {
			return true;
		} else if (ObjToStr(obj).trim().equals("")) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获得GFLAG
	 * 
	 * @param i
	 * @return String
	 */
	public static String getGFlag(Integer i) {
		return String.valueOf(i);
	}

	/**
	 * Object类型转换成String
	 * 
	 * @param obj
	 * @return String
	 */
	public static String ObjToStr(Object obj) {
		return String.valueOf(obj);
	}

	/**
	 * String 转换成 Integer类型
	 * 
	 * @param str
	 * @return Long
	 */
	public static Long StrToLong(String str) {
		return Long.parseLong(str);
	}

	/**
	 * 对PKG_NUM的SEQ进行字符串处理
	 * 
	 * @param obj
	 * @return String
	 */
	public static String LongToString(Long l) {
		String str = String.valueOf(l);
		int len = str.length();
		if ((5 - len) > 0) {
			if ((5 - len) == 4) {
				str = "0000" + str;
			} else if ((5 - len) == 3) {
				str = "000" + str;
			} else if ((5 - len) == 2) {
				str = "00" + str;
			} else if ((5 - len) == 1) {
				str = "0" + str;
			}
			return str;
		} else {
			return str;
		}
	}

	/**
	 * 截取字符串 （支持正向、反向截取）
	 * 
	 * @param length
	 *            长度 ，>=0时，从头开始向后截取length长度的字符串；<0时，从尾开始向前截取length长度的字符串
	 * @return 返回截取的字符串
	 */
	public static String subStr(String str, int length) throws RuntimeException {
		if (str == null) {
			throw new NullPointerException("字符串为null");
		}
		int len = str.length();
		// 取length的绝对值
		if (len < Math.abs(length)) {
			throw new StringIndexOutOfBoundsException("最大长度为" + len + "，索引超出范围为:" + (len - Math.abs(length)));
		}
		if (length >= 0) {
			return subStr(str, 0, length);
		} else {
			return subStr(str, len - Math.abs(length), len);
		}
	}

	/**
	 * 截取字符串 （支持正向、反向选择）
	 * 
	 * @param str
	 *            待截取的字符串
	 * @param start
	 *            起始索引 ，>=0时，从start开始截取；<0时，从length-|start|开始截取
	 * @param end
	 *            结束索引 ，>=0时，从end结束截取；<0时，从length-|end|结束截取
	 * @return 返回截取的字符串
	 * @throws RuntimeException
	 */
	public static String subStr(String str, int start, int end) throws RuntimeException {
		if (str == null) {
			throw new NullPointerException("");
		}
		int len = str.length();
		int s = 0;// 记录起始索引
		int e = 0;// 记录结尾索引
		if (len < Math.abs(start)) {
			throw new StringIndexOutOfBoundsException("最大长度为" + len + "，索引超出范围为:" + (len - Math.abs(start)));
		} else if (start < 0) {
			s = len - Math.abs(start);
		} else if (start < 0) {
			s = 0;
		} else {// >=0
			s = start;
		}
		if (len < Math.abs(end)) {
			throw new StringIndexOutOfBoundsException("最大长度为" + len + "，索引超出范围为:" + (len - Math.abs(end)));
		} else if (end < 0) {
			e = len - Math.abs(end);
		} else if (end == 0) {
			e = len;
		} else {// >=0
			e = end;
		}
		if (e < s) {
			throw new StringIndexOutOfBoundsException("截至索引小于起始索引:" + (e - s));
		}
		return str.substring(s, e);
	}

	/**
	 * 删除字符串中的符号
	 * 
	 * @param olds
	 *            str
	 * @return String
	 */
	public static String deleteChar(String olds, String str) {
		return str.replaceAll(olds, "");
	}


}
