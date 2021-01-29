package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.mapper.FileMapper;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.util.MinioUtil;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 11:23
 **/
@Service
@Slf4j
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {
    @Autowired
    MinioUtil minioUtil;


    @Override
    public R uploadFile(MultipartFile file) {
        String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());
        Map<String, String> resultMap = new HashMap<>(4);
        resultMap.put("bucketName", "test");
        resultMap.put("fileName", fileName);

        try {
            minioUtil.putObject("test", fileName, file.getInputStream());
            //文件管理数据记录,收集管理追踪文件

        } catch (Exception e) {
            log.error("上传失败", e);
            return R.failMsg(e.getLocalizedMessage());
        }
        return R.ok(resultMap);

    }

    @Override
    public void downLoadFile(String fileName, HttpServerResponse response) {

        int separator = fileName.lastIndexOf(StrUtil.DASHED);
        String bucketName = fileName.substring(0, separator);

        try (InputStream inputStream = minioUtil.getObject(bucketName, fileName)) {
            response.setContentType("application/octet-stream; charset=UTF-8");
            IoUtil.copy(inputStream, response.getOut());
        } catch (Exception e) {
            log.error("文件读取异常", e);
        }
    }
}
