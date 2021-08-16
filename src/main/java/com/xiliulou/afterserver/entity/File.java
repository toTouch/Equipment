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
     *    安装类型：
     *    1:室外、
     *    2:半室外、
     *    3:室内
     *    4: SN码照片（链接到点位管理中）
     *    5:工控机物联网卡照片（链接到点位管理中）
     *    6:含柜机点位环境整体照片（偏远景）
     *    7: 点位柜机的整体正面照片一张（近景正面照）
     *    8: 点位柜机的整体侧面照片一张（室外机连同雨棚一起拍上）
     *    9:室外机膨胀螺丝照片（左侧、右侧、前侧、后侧）-备注将护脚盖打开后拍到膨胀螺丝
     *    10: 室内机护脚照片（左侧、右侧、前侧、后侧）
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

}
