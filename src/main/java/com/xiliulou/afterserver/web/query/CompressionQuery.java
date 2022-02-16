package com.xiliulou.afterserver.web.query;

import lombok.Data;

import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/16 14:19
 * @mood
 */
@Data
public class CompressionQuery {
    private String no;
    private String iotCard;
    private String compressionFile;
    private List<String> noList;
}
