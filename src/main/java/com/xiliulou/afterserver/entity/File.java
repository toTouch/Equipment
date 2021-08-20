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
    /**
     * 1.柜子  2.点位
     */
    private Integer type;
    private String fileName;
    private Long createTime;
    private Long bindId;
    private Integer fileType;


    /**
     *    安装类型：
     *    1:机柜、
     *    2:序列通讯 201 SN码 202 工控机物联网卡、
     *    3:机柜照片 301 远景   302 近景正面照   303 整体侧面
     *    4: 室外  401正面   402左侧    403右侧   404 背面
     *    5:室内  501正面  502左侧  503右侧  504背面
     */


    /**
     * 现场勘察 1 废弃
     */
    public static final Integer FILE_TYPE_SPOT = 1;
    /**
     * 安装验收 2 废弃
     */
    public static final Integer FILE_TYPE_INSTALL = 2;
    /**
     * 售后问题 3 废弃
     */
    public static final Integer FILE_TYPE_AFTER = 3;

    public static final Integer TYPE_POINT = 1;
    public static final Integer TYPE_WORK_ORDER = 2;


    /**
     * 新改版的备注
     */
    public static final Integer TYPE_PRODUCT = 1;
    public static final Integer TYPE_POINTNEW = 2;

}
