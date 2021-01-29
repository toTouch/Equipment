package com.xiliulou.afterserver.service;

import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.util.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface FileService extends IService<File> {

    R uploadFile(MultipartFile file);

    void downLoadFile(String fileName, HttpServletResponse response);
}
