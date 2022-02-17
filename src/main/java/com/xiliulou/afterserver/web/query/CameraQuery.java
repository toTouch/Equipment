package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/8 16:48
 * @mood
 */
@Data
public class CameraQuery {
    private Long id;
    private String serialNum;
    private Long supplierId;
    private String iotCardId;
    private Long createTime;
    private Long updateTime;
    private Integer delFlag;

    private Long createTimeStart;
    private Long createTimeEnd;
}
