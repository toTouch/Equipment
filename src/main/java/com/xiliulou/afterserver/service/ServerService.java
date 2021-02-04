package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Server;

import java.util.List;

public interface ServerService extends IService<Server> {
    IPage getPage(Long offset, Long size, Server server);
}
