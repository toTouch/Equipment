package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiliulou.afterserver.entity.Product;
import org.apache.ibatis.annotations.Param;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:58
 **/
public interface ProductMapper extends BaseMapper<Product> {

    Integer getByDateQuery(@Param("years") String years, @Param("mouths") String mouths);

    Integer getMouth(@Param("years") String years, @Param("mouths") String mouths);

    Integer getGeneral(@Param("years") String years, @Param("mouths") String mouths);

    Integer getTotal(@Param("years") String years, @Param("mouths") String mouths);

    Integer getRepairCount(@Param("years") String years, @Param("mouths") String mouths);
}
