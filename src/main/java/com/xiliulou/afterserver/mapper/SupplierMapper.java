package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import com.xiliulou.afterserver.web.vo.SupplierPullVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SupplierMapper extends BaseMapper<Supplier> {
    @Select("select id, name from supplier where name like concat('%', #{name}, '%') limit 0, 20")
    List<SupplierPullVo> querySupplierPull(String name);

    List<PageSearchVo> supplierSearch(Long offset, Long size, String name);
}
