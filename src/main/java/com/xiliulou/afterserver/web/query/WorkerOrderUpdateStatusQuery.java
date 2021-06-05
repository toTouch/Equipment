package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author Hardy
 * @date 2021/6/4 0004 14:30
 * @Description:
 */
@Data
public class WorkerOrderUpdateStatusQuery {
    private Long id;
    private Integer status;
    private Long workOrderReasonId;
}
