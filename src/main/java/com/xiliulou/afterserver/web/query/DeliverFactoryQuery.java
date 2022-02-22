package com.xiliulou.afterserver.web.query;

import lombok.Data;

import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/18 17:17
 * @mood
 */
@Data
public class DeliverFactoryQuery {
    /**
     * 发货编号
     */
    private String sn;
    /**
     * 发货内容
     */
    private List<DeliverFactoryProductQuery> productContent;
}
