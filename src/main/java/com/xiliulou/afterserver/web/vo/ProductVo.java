package com.xiliulou.afterserver.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductFile;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


@Data
public class ProductVo extends Product {

  private List<ProductFile> productFileList;

}
