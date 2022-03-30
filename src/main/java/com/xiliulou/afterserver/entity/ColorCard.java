package com.xiliulou.afterserver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zgw
 * @date 2022/3/30 19:15
 * @mood
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_color_card")
public class ColorCard {
    private Long id;
    private Integer ral;
    private String name;
}
