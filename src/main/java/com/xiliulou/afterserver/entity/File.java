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

    public static final Integer TYPE_POINT = 1;


}
