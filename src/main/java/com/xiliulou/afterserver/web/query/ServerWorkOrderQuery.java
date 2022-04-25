package com.xiliulou.afterserver.web.query;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author zgw
 * @date 2022/4/13 10:38
 * @mood
 */
@Data
public class ServerWorkOrderQuery {
    private String solution;
    private Long workOrderId;
}
