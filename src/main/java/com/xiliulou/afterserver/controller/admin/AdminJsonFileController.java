package com.xiliulou.afterserver.controller.admin;

import cn.hutool.http.server.HttpServerResponse;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 13:32
 **/
@RestController
@Slf4j
public class AdminJsonFileController {

    @Autowired
    FileService fileService;

    @PostMapping("/admin/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file) {

        return fileService.uploadFile(file);
    }

    @GetMapping("/admin/downLoad")
    public void getFile(@RequestParam("fileName") String fileName, HttpServerResponse response) {

        fileService.downLoadFile(fileName, response);
    }

}
