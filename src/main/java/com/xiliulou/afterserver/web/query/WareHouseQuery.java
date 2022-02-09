package com.xiliulou.afterserver.web.query;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Map;

/**
 * @author Hardy
 * @date 2022/2/9 9:36
 * @mood
 */
@Data
public class WareHouseQuery {
    private Integer id;
    /**
     * 仓库名
     */
    private String wareHouses;
    /**
     * 创库地址
     */
    private String address;
    /**
     * 负责人
     */
    private String head;
    /**
     * 联系电话
     */
    private String phone;
    /**
     * 创建时间
     */
    private Long createTime;
    /**
     * 服务范围
     */
    private String scope;
    /**
     * 备注
     */
    private String remark;

    private Map<Long, Integer> productSerialNumberIdAndSetNoMap;

    private Long createTimeStart;
    private Long createTimeEnd;
}
