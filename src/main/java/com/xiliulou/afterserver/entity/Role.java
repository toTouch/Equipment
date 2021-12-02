package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**角色表*/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_role")
public class Role {
     /**
      * 编号
      * */
    private Long id;
    /**
     * 姓名
     * */
    private String name;
    /**
     * 创建时间
     * */
    private Long createTime;
    /**
     * 修改时间
     * */
    private Long updateTime;

}
