package com.xiliulou.afterserver.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author zgw
 * @date 2022/5/23 16:32
 * @mood
 */
@Data
public class DeliveryQualifiedFileVo {
    private Long id;
    private String no;
    private List<OssUrlVo> accessoryPackagingFileUrl;
    private List<OssUrlVo> outerPackagingFileUrl;
    private List<OssUrlVo> qualityInspectionFileUrl;
}
