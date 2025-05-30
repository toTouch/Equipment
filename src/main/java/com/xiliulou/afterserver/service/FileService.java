package com.xiliulou.afterserver.service;

import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.util.R;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface FileService extends IService<File> {

    R uploadFile(MultipartFile file, Integer fileType);

    R downLoadFile(String fileName,Integer fileType, HttpServletResponse response);

    R getFileList(Long pid);

    List<File> queryByPointId(Long pid);

    Integer queryCountByPointId(Long pid);

    List<File> queryByProductNewId(Long productId, Integer fileType);

    R removeFile(Long fileId, Integer fileType);

    R removeOssOrMinio(String name, Integer fileType);

    void saveBatchFile(List<File> files);
}
