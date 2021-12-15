package com.xiliulou.afterserver.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/***权限表*/

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_permission_resource")
public class PermissionResource {

    /**编号*/
    private Long id;
    /**名字*/
    private String name;
    /**权限类型*/
    private Integer type;
    /**权限路径*/
    private String uri;
    /**权限方法*/
    private String method;
    /**权限顺序*/
    private Double sort;
    /**父菜单id*/
    private Long parent;
    /**描述*/
    private String desc;
    /**创建时间*/
    private Long createTime;
    /**修改时间*/
    private Long updateTime;

    private Integer delFag;

    public static final long MENU_ROOT = 0;

    public static final Integer DEL_NORMAL =0 ;

    public static final Object TYPE_URL =2 ;

    public static final Integer TYPE_PAGE = 1;


}
