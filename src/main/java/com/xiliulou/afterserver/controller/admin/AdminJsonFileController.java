package com.xiliulou.afterserver.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.xiliulou.afterserver.constant.FileConstant;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.ProductFile;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
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
    @Autowired
    ProductFileMapper productFileMapper;


    @GetMapping("/admin/file/info/{id}")
    public R fileInfo(@PathVariable("id") Long id ){
        return R.ok(fileService.getById(id));
    }

    @PostMapping("/admin/file")
    public R saveFile(@RequestBody File file){
        file.setCreateTime(System.currentTimeMillis());
        return R.ok(fileService.save(file));
    }

    @DeleteMapping("/admin/file/{id}")
    public R delFile(@PathVariable("id") Long id){
        return R.ok(fileService.removeById(id));
    }

    /**
     * 上传文件
     * @param file
     * @return
     */
    @PostMapping("/admin/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "fileType", required = false, defaultValue = "0")Integer fileType) {
        return fileService.uploadFile(file, fileType);
    }


    /**
     *下载文件
     * @param fileName
     * @return
     */
    @GetMapping("/admin/downLoad")
    public R getFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        return fileService.downLoadFile(fileName, response);
    }

    /**
     * 产品批次文件
     * @param file
     * @return
     */
    @PostMapping("/admin/product/file")
    public R adminPrductFile(@RequestBody ProductFile file){
        productFileMapper.insert(file);
        return R.ok(file);
    }

    /**
     * 删除产品批次文件
     */
    @DeleteMapping("/admin/product/{id}")
    public R delProductFile(@PathVariable("id") Long id){
        int i = productFileMapper.deleteById(id);
        if (i == 0){
            return R.fail("数据库错误，删除失败");
        }
        return R.ok();
    }

}
