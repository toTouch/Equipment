package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.WareHouseProductDetails;
import lombok.Data;

/**
 * @author Hardy
 * @date 2021/9/18 9:20
 * @mood
 */
@Data
public class WareHouseProductDetailsVo extends WareHouseProductDetails {
    private String productName;
    private String productCode;
}
