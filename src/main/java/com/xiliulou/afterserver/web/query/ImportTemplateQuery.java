package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author Hardy
 * @date 2022/2/14 16:54
 * @mood
 */
@Data
public class ImportTemplateQuery {
    private  Long id;
    @NotNull(message = "请填写文件名称")
    private String fileName;
    @NotNull(message = "请填写文件类型")
    private String type;
    private Long createTime;
    private Long updateTime;
}
