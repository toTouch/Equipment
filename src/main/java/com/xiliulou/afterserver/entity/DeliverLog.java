package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author Hardy
 * @date 2022/2/21 9:04
 * @mood
 */
@Data
@TableName("t_deliver_log")
public class DeliverLog {
    private Long id;
    private Long deliverId;
    private Long productId;
    private String insertTime;
    private Long createTime;
    private Long updateTime;
}
