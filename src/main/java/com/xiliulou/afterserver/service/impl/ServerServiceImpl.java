package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.mapper.ServerMapper;
import com.xiliulou.afterserver.service.ServerService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-02-04 15:54
 **/
@Service
@Slf4j
public class ServerServiceImpl extends ServiceImpl<ServerMapper, Server> implements ServerService {


    @Override
    public IPage getPage(Long offset, Long size, Server server) {

        return baseMapper.selectPage(PageUtil.getPage(offset, size), Wrappers.<Server>lambdaQuery().orderByDesc(Server::getCreateTime));
    }
}
