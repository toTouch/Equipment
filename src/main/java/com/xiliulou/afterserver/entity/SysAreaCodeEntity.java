package com.xiliulou.afterserver.entity;



import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Date;
import java.util.List;



/**
 * 地区表
 * 
 * @author HardyRUI
 * @email ${email}
 * @date 2021-02-20 11:49:15
 */
@Data
@TableName("sys_area_code")
public class SysAreaCodeEntity implements Serializable {

	/**
	 * 地区代码
	 */

	private String code;
	/**
	 * 地区名称
	 */
	private String name;
	/**
	 * 地区简称
	 */
	private String shortName;
	/**
	 * 上级地区代码
	 */
	private String superiorCode;
	/**
	 * 地区省级代码
	 */
	private String provincialCode;
	/**
	 * 地区省级名称
	 */
	private String provincialName;
	/**
	 * 地区市级代码
	 */
	private String cityCode;
	/**
	 * 地区市级名称
	 */
	private String cityName;
	/**
	 * 地区县级代码
	 */
	private String countyCode;
	/**
	 * 地区县级名称
	 */
	private String countyName;
	/**
	 * 状态：1：省，2：市，3：区县
	 */
	private String state;
	/**
	 * 地区编码 保留字段
	 */
	private String remark;
	/**
	 * 有效标志1：有效，0：无效
	 */
	private String isValid;


	@TableField(exist = false)
	private List<SysAreaCodeEntity> children;

}
