package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zgw
 * @date 2022/6/23 15:36
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_group_version")
public class GroupVersion {
    private Long id;
    private Long groupId;
    private String groupName;
    private BigDecimal version;
}
