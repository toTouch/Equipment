package com.xiliulou.afterserver.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.entity.ProductSerialNumber;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import com.xiliulou.afterserver.web.vo.ProductSerialNumberVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-02 14:16
 **/

public interface ProductSerialNumberMapper extends BaseMapper<ProductSerialNumber> {

    IPage<ProductSerialNumberVo> getSerialNumberPage(Page page, @Param("query") ProductSerialNumber productSerialNumber);

    List<ProductSerialNumberVo> getSerialNumberList(@Param("query") ProductSerialNumberQuery productSerialNumberQuery);
}
