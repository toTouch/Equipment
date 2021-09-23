package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author zgw
 * @date 2021/9/18 17:48
 * @mood
 */
@Data
public class ProductLendingApplicationQuery {
    private Long id;

    private String user;

    private Long createTime;

    private Long returnTime;

    private Long status;

    private Long wareHouseId;

    private Long createTimeStart;

    private Long createTimeEnd;
}
