package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.print.DocFlavor;

@Data
public class ProductInfoQuery {
    private Long productId;
    private String productName;
    private Long number;
}
