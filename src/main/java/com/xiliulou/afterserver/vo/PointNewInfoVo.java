package com.xiliulou.afterserver.vo;

import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.ProductNew;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author Hardy
 * @date 2021/8/20 9:28
 * @mood
 */
@Data
public class PointNewInfoVo {
    private PointNew pointNew;
    private List<File> pointFileList;
    private List<ProductNew> productNew;
    private List<Map> productTypeAndNum;
}
