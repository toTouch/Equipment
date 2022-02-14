package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/14 16:26
 * @mood
 */
@Data
@TableName("t_import_template")
public class ImportTemplate {
    private  Long id;
    private String fileName;
    private String type;
    private Long createTime;
    private Long updateTime;
}
