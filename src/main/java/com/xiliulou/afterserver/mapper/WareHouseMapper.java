package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import com.xiliulou.afterserver.web.vo.WarehousePullVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WareHouseMapper extends BaseMapper<WareHouse> {
    @Select("select id, ware_houses from ware_house")
    WarehousePullVo queryWarehousePull();

    @Select("select id, ware_houses from ware_house where ware_houses like concat('%', #{name}, '%') limit 0, 20;")
    List<WarehousePullVo> queryWarehouseLikeNamePull(String name);

    List<PageSearchVo> warehouseSearch(@Param("offset") Long offset, @Param("size")Long size, @Param("name")String name);
}
