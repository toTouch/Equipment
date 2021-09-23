package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author Hardy
 * @date 2021/9/22 10:05
 * @mood
 */
@Data
public class ProductIendingApplicationVo {
    private Long id;

    private String user;

    private Long createTime;

    private Long returnTime;

    private Long status;

    private Long wareHouseId;

    private String createTimeStr;

    private String returnTimeStr;

    private String statusStr;
}
