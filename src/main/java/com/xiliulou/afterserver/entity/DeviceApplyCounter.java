package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_device_apply_counter")
public class DeviceApplyCounter {
    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 日期 格式为yyMMdd
     */
    private String date;
    /**
     * H有屏 N无屏
     */
    private String type;
    /**
     * 次数
     */
    private Long count;
}
