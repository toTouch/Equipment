package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/14 16:54
 * @mood
 */
@Data
public class ImportTemplateQuery {
    private  Long id;
    private String fileName;
    private String type;
    private Long createTime;
    private Long updateTime;
}
