package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.File;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-01 16:14
 **/
@Data
public class DeliverQuery extends Deliver {
    private List<File> fileList;
    private Map<Long, Integer> productSerialNumberIdAndSetNoMap;

    private Long deliverTimeStart;
    private Long deliverTimeEnd;

    private Long createTimeStart;
    private Long createTimeEnd;


}
