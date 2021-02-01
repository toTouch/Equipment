package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.nio.file.LinkOption;

/**
 * @program: XILIULOU
 * @description: 采购
 * @author: Mr.YG
 * @create: 2021-01-28 17:59
 **/
@Data
@TableName("purchase")
public class Purchase {
    private Long id;
    private Long customerId;
    private BigDecimal fee;
    private String city;
    private String billsFees;
    private Long settingTime;
    private Long createTime;
    private Long pointId;

}
