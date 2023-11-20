package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import com.xiliulou.afterserver.web.vo.SupplierPullVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Set;

public interface SupplierMapper extends BaseMapper<Supplier> {
    
    @Select("select id, name from supplier where name like concat('%', #{name}, '%') limit 0, 20")
    List<SupplierPullVo> querySupplierPull(String name);
    
    List<PageSearchVo> supplierSearch(@Param("offset") Long offset, @Param("size") Long size, @Param("name") String name);
    
    List<Supplier> selectListByIds(@Param("supplierIds") Set<Long> supplierIds);
}
