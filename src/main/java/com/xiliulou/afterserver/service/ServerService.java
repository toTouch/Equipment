package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.util.R;

import java.util.List;

public interface ServerService extends IService<Server> {
    IPage getPage(Long offset, Long size, Server server);

    R queryServerPull(String name);

    List<Integer> getByIdsByName(String serverName);
}
