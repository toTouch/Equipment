package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.ProductSerialNumber;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-02 14:33
 **/
@Data
public class ProductSerialNumberVo extends ProductSerialNumber {
    private String productName;
}
