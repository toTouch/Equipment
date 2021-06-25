package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author Hardy
 * @date 2021/6/15 0015 15:57
 * @Description:
 */
@Data
public class AfterCountListVo {
    private String date;
    private String dateTime;
    private Integer count;
    private Long reasonId;
    private String reasonName;
}
