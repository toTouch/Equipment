package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/18 10:23
 * @mood
 */
@Data
public class OrderDeliverVo {
    private String no;
    private List<OrderDeliverContentVo> content;
    /**
     * 客户名称
     */
    private String customerName;
    private String remark;
    private String createTime;
}
