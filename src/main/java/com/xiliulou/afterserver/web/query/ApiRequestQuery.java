package com.xiliulou.afterserver.web.query;

import lombok.Data;

/**
 * @author zgw
 * @date 2022/2/25 15:35
 * @mood
 */
@Data
public class ApiRequestQuery {
    /**
     * 请求时间
     */
    private Long requestTime;
    /**
     * appId
     */
    private String appId;
    /**
     * 生成的签名
     */
    private String sign;
    /**
     * 数据
     */
    private String data;

}
