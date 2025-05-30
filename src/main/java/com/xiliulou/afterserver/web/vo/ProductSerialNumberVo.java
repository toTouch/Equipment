package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.ProductFile;
import com.xiliulou.afterserver.entity.ProductSerialNumber;
import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-02 14:33
 **/
@Data
public class ProductSerialNumberVo extends ProductSerialNumber {
    private String productName;
    private String name;
    private Integer type;
    private String code;
    private Integer boxNumber;

    private String info;

    //位置
    private Object addr;

    private List<ProductFile> productFileList;


}
