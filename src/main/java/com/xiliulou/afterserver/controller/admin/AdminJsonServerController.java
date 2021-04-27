package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.entity.Server;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.ServiceInfo;
import com.xiliulou.afterserver.listener.ClientListener;
import com.xiliulou.afterserver.listener.ServiceListener;
import com.xiliulou.afterserver.service.ServerService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
        server.setCreateTime(System.currentTimeMillis());
        return R.ok(serverService.save(server));
    }

    @PutMapping("admin/server")
    public R update(@RequestBody Server server) {
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
