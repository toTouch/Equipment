package com.xiliulou.afterserver.web.vo;

import com.xiliulou.afterserver.entity.*;
import lombok.Data;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 17:32
 **/
@Data
public class PointVo extends Point {
    private List<File> fileList;
    private List<Product> productList;
    private List<SettleAccounts> settleAccountsList;
    private String customerName;
    private List<ProductSerialNumberVo> productSerialNumberVoList;
    private List<PointBindSettleAccounts> pointBindSettleAccountsList;
    private List<WorkOrderVo> workOrderVoList;
}
