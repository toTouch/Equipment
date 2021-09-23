package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2021/9/17 18:06
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("ware_house_product_details")
public class WareHouseProductDetails {

    private Long id;

    private Long productId;

    private Long stockNum;

    private Long wareHouseId;

}
