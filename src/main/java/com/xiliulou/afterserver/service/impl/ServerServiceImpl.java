package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.ServerMapper;
import com.xiliulou.afterserver.service.ServerService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.service.WorkOrderServerService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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

    @Autowired
    UserService userService;
    @Autowired
    WorkOrderServerService workOrderServerService;

    @Override
    public IPage getPage(Long offset, Long size, Server server) {

        LambdaQueryWrapper<Server> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Objects.nonNull(server.getName()),Server::getName,server.getName())
                .like(Objects.nonNull(server.getManager()),Server::getManager,server.getManager())
                .like(Objects.nonNull(server.getPhone()),Server::getPhone,server.getPhone())
                .like(Objects.nonNull(server.getArea()),Server::getArea,server.getArea())
                .eq(Objects.nonNull(server.getCreateUid()),Server::getCreateUid,server.getCreateUid())
                .eq(Objects.nonNull(server.getStatus()),Server::getStatus,server.getStatus())
                .orderByDesc(Server::getCreateTime);

        IPage<Server> page = baseMapper.selectPage(PageUtil.getPage(offset, size), wrapper);
        if(!CollectionUtils.isEmpty(page.getRecords())) {
            page.getRecords().forEach(item -> {
                User user = userService.getUserById(item.getCreateUid());
                if(Objects.nonNull(user)){
                    item.setCreateUname(user.getUserName());
                }
                Long curtMonthTime = DateUtils.getMonthStartTime(System.currentTimeMillis());
                //workOrderServerService.queryPrescriptionAvg(curtMonthTime);
            });
        }
        return page;
    }
}
