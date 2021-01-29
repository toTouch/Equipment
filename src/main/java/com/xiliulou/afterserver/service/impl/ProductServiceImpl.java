package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.mapper.ProductMapper;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:08
 **/
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {


    @Override
    public IPage getPage(Long offset, Long size, Product product) {

        Page page = PageUtil.getPage(offset, size);
        return baseMapper.
    }
}
