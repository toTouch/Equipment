package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import com.xiliulou.afterserver.web.vo.ServerPullVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
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
        wrapper.like(StringUtils.isNotBlank(server.getName()),Server::getName,server.getName())
                .like(StringUtils.isNotBlank(server.getManager()),Server::getManager,server.getManager())
                .like(StringUtils.isNotBlank(server.getPhone()),Server::getPhone,server.getPhone())
                .like(StringUtils.isNotBlank(server.getArea()),Server::getArea,server.getArea())
                .like(StringUtils.isNotBlank(server.getScope()),Server::getScope,server.getScope())
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
                Long prescriptionAvg = workOrderServerService.queryPrescriptionAvgByServerId(curtMonthTime, item.getId());
                item.setPrescriptionAvg(prescriptionAvg);
            });
        }
        return page;
    }

    @Override
    public R queryServerPull(String name) {
        return R.ok(baseMapper.queryServerPull(name));
    }

    @Override
    public List<Integer> getByIdsByName(String serverName) {
        return baseMapper.getByIdsByName(serverName);
    }

    @Override
    public R serverSearch(Long offset, Long size, String name) {
        List<PageSearchVo> voList =  baseMapper.serverSearch(offset, size, name);
        return R.ok(voList);
    }
}
