package com.xiliulou.afterserver.constant;

/**
 * @ClassName : CommonConstants
 * @Description : 公用常量
 * @Author : YG
 * @Date: 2020-04-23 10:23
 */
public interface CommonConstants {
	/**
	 * 成功标记
	 */
	Integer SUCCESS = 0;
	/**
	 * 失败标记
	 */
	Integer FAIL = 1;

	String GEOCODE_URL = "http://restapi.amap.com/v3/geocode/geo?key=%s&address=%s";

}
