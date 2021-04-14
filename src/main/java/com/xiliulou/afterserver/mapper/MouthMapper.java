package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.WorkOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MouthMapper extends BaseMapper<WorkOrder> {
    List<Product> selectList(@Param("wrapper") QueryWrapper<Product> wrapper);
}
