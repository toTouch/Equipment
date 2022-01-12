package com.xiliulou.afterserver.export;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * @author Hardy
 * @date 2021/10/19 8:54
 * @mood
 */
@Data
public class WorkOrderInfo {
    /**
     * 工单类型
     */
    @ExcelProperty(index = 0)
    private Integer type;
    /**
     * 點位
     */
    @ExcelProperty(index = 1)
    private String pointName;
    /**
     * 費用
     */
    @ExcelProperty(index = 2)
    private Double fee;
    /**
     * 结算方式
     */
    @ExcelProperty(index = 3)
    private String paymentMethod;
    /**
     * 公司类别
     */
    @ExcelProperty(index = 4)
    private String thirdCompanyType ;
    /**
     * 第三方公司
     */
    @ExcelProperty(index = 5)
    private String thirdCompanyName;
    /**
     * 第三方承擔費用
     */
    @ExcelProperty(index = 6)
    private Double thirdCompanyPay;
    /**
     * 第三方结算状态
     */
    @ExcelProperty(index = 7)
    private String thirdPaymentStatus;
    /**
     * 第三方责任对接人
     */
    @ExcelProperty(index = 8)
    private String thirdResponsiblePerson;
    /**
     * 工單原因
     */
    @ExcelProperty(index = 9)
    private Integer workOrderReasonId;
    /**
     * 服務商
     */
    @ExcelProperty(index = 10)
    private String serverName;
    /**
     * 處理時間
     */
    @ExcelProperty(index = 11)
    private String processTime;
    /**
     * 狀態
     */
    @ExcelProperty(index = 12)
    private Integer status;
    /**
     * 備註
     */
    @ExcelProperty(index = 13)
    private String info;
    /**
     * 描述
     */
    @ExcelProperty(index = 14)
    private String describeinfo;
}
