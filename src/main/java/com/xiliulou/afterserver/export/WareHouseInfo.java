package com.xiliulou.afterserver.export;

import lombok.Data;

/**
 * @author Hardy
 * @date 2021/4/25 0025 15:49
 * @Description: 仓库
 */
@Data
public class WareHouseInfo {
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
}
