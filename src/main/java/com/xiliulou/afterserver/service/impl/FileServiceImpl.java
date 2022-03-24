package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.FileConstant;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.mapper.FileMapper;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.util.MinioUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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
    @Autowired
    AliyunOssService aliyunOssService;
    @Autowired
    StorageConfig storageConfig;


    @Override
    public R uploadFile(MultipartFile file) {
        String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());
        Map<String, String> resultMap = new HashMap<>(2);
        resultMap.put("bucketName", FileConstant.BUCKET_NAME);

        resultMap.put("fileName", FileConstant.BUCKET_NAME + StrUtil.DASHED + fileName);

        try {
            minioUtil.putObject(FileConstant.BUCKET_NAME, FileConstant.BUCKET_NAME + StrUtil.DASHED + fileName, file.getInputStream());
            //文件管理数据记录,收集管理追踪文件

        } catch (Exception e) {
            log.error("上传失败", e);
            return R.failMsg(e.getLocalizedMessage());
        }
        return R.ok(resultMap);

    }

    @Override
    public void downLoadFile(String fileName, HttpServletResponse response) {

        int separator = fileName.lastIndexOf(StrUtil.DASHED);
        String bucketName = fileName.substring(0, separator);

        try (InputStream inputStream = minioUtil.getObject(bucketName, fileName)) {
            response.setContentType("application/octet-stream; charset=UTF-8");
            IoUtil.copy(inputStream, response.getOutputStream());
        } catch (Exception e) {
            log.error("文件读取异常", e);
        }
    }

    @Override
    public R getFileList(Long pid) {
        LambdaQueryWrapper<File> fileLambdaQueryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<File> eq = fileLambdaQueryWrapper.eq(File::getBindId, pid);
        List<File> files = this.baseMapper.selectList(eq);
        return R.ok(files);
    }

    @Override
    public List<File> queryByPointId(Long pid) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>().eq(File::getType, File.TYPE_POINTNEW).eq(File::getBindId, pid);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public List<File> queryByProductNewId(Long productId) {
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<File>().eq(File::getType, File.TYPE_PRODUCT).eq(File::getBindId, productId);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    public R removeFile(Long fileId) {
        File file = this.baseMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getId, fileId));
        if(Objects.nonNull(file)){
            try{
                minioUtil.removeObject(FileConstant.BUCKET_NAME,file.getFileName());
                this.baseMapper.delete(new LambdaUpdateWrapper<File>().eq(File::getId, fileId));
                return R.ok();
            }catch (Exception e){
                log.error("文件删除异常", e);
            }
        }
        return R.fail("文件删除失败");
    }

    /**
     * 阿里云 oss 上传文件
     * @param file
     * @return
     */
    @Override
    public R uploadFileToOss(MultipartFile file) {
        String fileName = storageConfig.getDir().replaceAll("/","") + StrUtil.DASHED
                + IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());

        try {
            aliyunOssService.uploadFile("liubiqi",
                    fileName,
                                file.getInputStream());
        }catch (IOException e){
            log.error("aliyunOss upload File Error!", e);
            return R.failMsg(e.getLocalizedMessage());
        }

        Map<String, Object> result = new HashMap<>(2);
        result.put("fileName", fileName);
        return R.fail(result);
    }

    @Override
    public R downLoadFileToOss(String fileName) {
        Long expiration = Optional.ofNullable(storageConfig.getExpiration()).orElse(1000L * 60L * 3L);
        String url = null;
        try{
            url = aliyunOssService.getOssFileUrl(storageConfig.getBucketName(),
                    storageConfig.getDir().replaceAll("/", "") + StrUtil.SLASH + fileName, System.currentTimeMillis() + expiration);
        }catch (Exception e){
            log.error("aliyunOss down File Error!", e);
        }

        if(StringUtils.isNotBlank(url)) {
            return R.ok(url);
        }

        return R.fail("oss获取url失败，请联系管理员");
    }
}
