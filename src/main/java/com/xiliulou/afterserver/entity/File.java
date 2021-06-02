package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:文件
 * @author: Mr.YG
 * @create: 2021-01-29 11:20
 **/
@Data
@TableName("file")
public class File {

    private Long id;
    private Integer type;
    private String fileName;
    private Long createTime;
    private Long bindId;
    private Integer fileType;

    /**
     * 现场勘察 1
     */
    public static final Integer FILE_TYPE_SPOT = 1;
    /**
     * 安装验收 2
     */
    public static final Integer FILE_TYPE_INSTALL = 2;
    /**
     * 售后问题 3
     */
    public static final Integer FILE_TYPE_AFTER = 3;

    public static final Integer TYPE_POINT = 1;
    public static final Integer TYPE_WORK_ORDER = 2;

}
