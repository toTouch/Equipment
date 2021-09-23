package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2021/9/18 17:40
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("product_lending_application_item")
public class ProductLendingApplicationItem {
    private Long id;

    private Long applyNum;

    private Long takeNum;

    private Long returnNum;

    private Long productId;

    private Long productLendingAppId;
}
