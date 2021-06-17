package com.xiliulou.afterserver.entity;

import com.xiliulou.afterserver.web.vo.AfterOrderVo;
import lombok.Data;

import java.util.List;

/**
 * @author Hardy
 * @date 2021/6/17 0017 19:20
 * @Description:
 */
@Data
public class InstallSumCountVo {
    private Long pointId;
    private String pointName;
    private Double sum;
    private List<AfterOrderVo> list;
}
