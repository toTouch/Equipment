package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.ServiceInfo;
import com.xiliulou.afterserver.listener.ClientListener;
import com.xiliulou.afterserver.listener.ServiceListener;
import com.xiliulou.afterserver.service.ServerService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description: 服务商
 * @author: Mr.YG
 * @create: 2021-02-04 15:53
 **/
@RestController
@Slf4j
public class AdminJsonServerController {


    @Autowired
    ServerService serverService;

    @GetMapping("admin/server/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Server server) {
        return R.ok(serverService.getPage(offset, size, server));
    }


    @PostMapping("admin/server")
    public R insert(@RequestBody Server server) {
        BaseMapper<Server> baseMapper = serverService.getBaseMapper();
        Server serverOld = baseMapper.selectOne(new QueryWrapper<Server>().eq("name",server.getName()));
        if(Objects.nonNull(serverOld) ){
            return R.fail("服务商列表已存在【" + server.getName() + "】");
        }

        server.setCreateUid(SecurityUtils.getUid());
        server.setCreateTime(System.currentTimeMillis());
        return R.ok(serverService.save(server));
    }

    @PutMapping("admin/server")
    public R update(@RequestBody Server server) {
        BaseMapper<Server> baseMapper = serverService.getBaseMapper();
        Server serverOld = baseMapper.selectOne(new QueryWrapper<Server>().eq("name",server.getName()));
        if(Objects.nonNull(serverOld) && !Objects.equals(server.getId(), serverOld.getId())){
            return R.fail("服务商列表已存在【" + server.getName() + "】");
        }

        return R.ok(serverService.updateById(server));
    }

    @DeleteMapping("admin/server")
    public R delete(@RequestParam("id") Long id) {
        this.serverService.removeById(id);
        return R.ok();
    }

    @GetMapping("admin/server/list")
    public R list() {
        return R.ok(serverService.list(Wrappers.<Server>lambdaQuery().orderByDesc(Server::getCreateTime)));
    }

    /**
     * 导入
     */
    @PostMapping("admin/server/upload")
    public R upload(MultipartFile file) throws IOException {

        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), ServiceInfo.class,new ServiceListener(serverService)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }
}
