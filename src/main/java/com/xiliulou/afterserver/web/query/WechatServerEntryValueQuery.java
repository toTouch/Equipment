package com.xiliulou.afterserver.web.query;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zgw
 * @date 2022/6/21 11:01
 * @mood
 */
@Data
public class WechatServerEntryValueQuery {
    @NotNull(message = "组件id不能为空")
    private Long entryId;
    private String value;
}
