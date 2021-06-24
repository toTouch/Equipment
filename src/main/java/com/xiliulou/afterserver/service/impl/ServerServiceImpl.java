package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.mapper.ServerMapper;
import com.xiliulou.afterserver.service.ServerService;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

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

        LambdaQueryWrapper<Server> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(server.getName()),Server::getName,server.getName())
                .like(Objects.nonNull(server.getManager()),Server::getManager,server.getManager())
                .like(Objects.nonNull(server.getPhone()),Server::getPhone,server.getPhone())
                .like(Objects.nonNull(server.getArea()),Server::getArea,server.getArea())
                .orderByDesc(Server::getCreateTime);

        return baseMapper.selectPage(PageUtil.getPage(offset, size), wrapper);
    }
}
