package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.mapper.DeliverMapper;
import com.xiliulou.afterserver.service.DeliverService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:05
 **/
@Service
@Slf4j
public class DeliverServiceImpl extends ServiceImpl<DeliverMapper, Deliver> implements DeliverService {


    @Override
    public IPage getPage(Long offset, Long size, DeliverQuery deliver) {

        Page page = PageUtil.getPage(offset, size);

        return baseMapper.getDeliverPage(page, deliver);
    }
}
