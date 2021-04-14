package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("ware_house")
public class WareHouse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Integer id;
    /**
     * 仓库名
     */
    private String wareHouses;
    /**
     * 创库地址
     */
    private String address;
    /**
     * 负责人
     */
    private String head;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 服务范围
     */
    private String scope;
    /**
     * 备注
     */
    private String remark;

}
