package com.xiliulou.afterserver.web.vo;

import lombok.Data;

/**
 * @author zgw
 * @date 2022/6/7 10:17
 * @mood
 */
@Data
public class AuditProcessVo {
    private Long id;
    /**
     * 页面名
     */
    private String name;
    /**
     * 区分页面的type
     */
    private String type;
    /**
     * status 0未完成 1 当前 2已完成
     */
    private Integer status;

    public static final  Integer STATUS_UNFINISHED = 0;
    public static final  Integer STATUS_EXECUTING = 1;
    public static final  Integer STATUS_FINISHED = 2;

}
