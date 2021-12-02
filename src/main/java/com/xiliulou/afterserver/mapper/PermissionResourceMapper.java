package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.PermissionResource;
import org.apache.ibatis.annotations.Select;

import java.util.List;


public interface PermissionResourceMapper extends BaseMapper<PermissionResource> {

        /**查询一条数据*/
       List<PermissionResource> findByIds(List<Long> pid);

    List<PermissionResource> queryAll();

    @Select("SELECT id,name,type,uri,method,sort,parent,create_time,update_time,del_flag FROM t_permission_resource WHERE id=#{id}")
    PermissionResource queryById(Long id);

    Integer insertOne(PermissionResource permissionResource);

    Integer update(PermissionResource permissionResource);
}
