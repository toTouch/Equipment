package com.xiliulou.afterserver.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.xiliulou.afterserver.constant.FileConstant;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.FileQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

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


    @GetMapping("/admin/file/list")
    public R fileList(@RequestParam("pid") Long pid){
        return fileService.getFileList(pid);
    }

    @PostMapping("/admin/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file) {
//        if (file.getSize() > FileConstant.FILE_MAX_SIZE) {
//
//            return R.failMsg("上传文件不能大于5MB!");
//        }
        return fileService.uploadFile(file);
    }

    @GetMapping("/admin/downLoad")
    public void getFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {

        fileService.downLoadFile(fileName, response);
    }

    /**
     * @return
     */
    @PostMapping("admin/bindFile")
    public R updateFile(@RequestBody FileQuery file) {
        if (ObjectUtil.equal(FileQuery.FLAG_FALSE, file.getDelFlag())) {
            file.setCreateTime(System.currentTimeMillis());
            fileService.save(file);
        }
        if (ObjectUtil.equal(FileQuery.FLAG_TRUE, file.getDelFlag())) {
            fileService.removeById(file.getId());
        }
        return R.ok();
    }


}
