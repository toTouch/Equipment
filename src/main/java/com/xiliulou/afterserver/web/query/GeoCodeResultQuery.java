package com.xiliulou.afterserver.web.query;

import lombok.Data;

import java.util.List;

/**
 * @author zgw
 * @date 2022/5/5 15:53
 * @mood
 */
@Data
public class GeoCodeResultQuery {
    private Integer status;
    private String info;
    private Integer infocode;
    private Integer count;
    private List<GeoCodeQuery> geocodes;

    public static final Integer STATUS_OK = 1;
    public static final Integer STATUS_FAIL = 0;
}
