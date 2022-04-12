package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
    public R uploadFile(MultipartFile file, Integer fileType) {
        String resultFileName = "";
        String bucketName = "";
        String dirName = "";
        if(Objects.equals(StorageConfig.IS_USE_OSS, storageConfig.getIsUseOSS())){
            if(fileType.equals(0)){
                bucketName = storageConfig.getBucketName();
                dirName = storageConfig.getDir();
            }

            if(fileType.equals(1)){
                bucketName = storageConfig.getOssVidioBucketName();
                dirName = storageConfig.getVidioDir();
            }
            String fileDirName = dirName.replaceAll("/","-");
            String fileName =  fileDirName + IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());

            try {
                aliyunOssService.uploadFile(bucketName, dirName + fileName, file.getInputStream());

                resultFileName = fileName;
            }catch (IOException e){
                log.error("aliyunOss upload File Error!", e);
                return R.failMsg(e.getLocalizedMessage());
            }

        }else if(Objects.equals(StorageConfig.IS_USE_MINIO, storageConfig.getIsUseOSS())){
            String fileName = IdUtil.simpleUUID() + StrUtil.DOT + FileUtil.extName(file.getOriginalFilename());

            try {
                minioUtil.putObject(storageConfig.getMinioBucketName(), storageConfig.getMinioBucketName() + StrUtil.DASHED + fileName, file.getInputStream());

                resultFileName = storageConfig.getMinioBucketName() + StrUtil.DASHED + fileName;
                bucketName = storageConfig.getMinioBucketName();
            } catch (Exception e) {
                log.error("上传失败", e);
                return R.failMsg(e.getLocalizedMessage());
            }

        }

        Map<String, String> resultMap = new HashMap<>(2);
        resultMap.put("bucketName", bucketName);
        resultMap.put("fileName", resultFileName);
        return R.ok(resultMap);

    }

    @Override
    public R downLoadFile(String fileName,Integer fileType, HttpServletResponse response) {
        String url = "";
        String dirName = "";

        if(Objects.equals(StorageConfig.IS_USE_OSS, storageConfig.getIsUseOSS())){
            Long expiration = Optional.ofNullable(storageConfig.getExpiration()).orElse(1000L * 60L * 3L) + System.currentTimeMillis();
            String bucketName = "";

            if(fileType.equals(0)){
                bucketName = storageConfig.getBucketName();
                dirName = storageConfig.getDir();
            }

            if(fileType.equals(1)){
                bucketName = storageConfig.getOssVidioBucketName();
                dirName = storageConfig.getVidioDir();
            }

            try{

                url = aliyunOssService.getOssFileUrl(bucketName,
                        dirName + fileName, expiration);
            }catch (Exception e){
                log.error("aliyunOss down File Error!", e);
                return R.fail("oss获取url失败，请联系管理员");
            }
            File file = this.getBaseMapper().selectOne(new QueryWrapper<File>().eq("file_name", fileName));

            Map<String, Object> result = new HashMap<>(2);
            result.put("url", url);
            result.put("id", file == null ? null : file.getId());
            return R.ok(result);
        } else if(Objects.equals(StorageConfig.IS_USE_MINIO, storageConfig.getIsUseOSS())){

            int separator = fileName.lastIndexOf(StrUtil.DASHED);
            String bucketName = fileName.substring(0, separator);

            try (InputStream inputStream = minioUtil.getObject(bucketName, fileName)) {
                response.setContentType("application/octet-stream; charset=UTF-8");
                IoUtil.copy(inputStream, response.getOutputStream());
            } catch (Exception e) {
                log.error("文件读取异常", e);
            }
        }

        return null;
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
    public R removeFile(Long fileId, Integer fileType) {
        File file = this.baseMapper.selectOne(new LambdaQueryWrapper<File>().eq(File::getId, fileId));
        String bucketName = "";
        String dirName = "";
        if(Objects.nonNull(file)){
            if(Objects.equals(StorageConfig.IS_USE_OSS, storageConfig.getIsUseOSS())){
                if(fileType.equals(0)){
                    bucketName = storageConfig.getBucketName();
                    dirName = storageConfig.getDir();
                }

                if(fileType.equals(1)){
                    bucketName = storageConfig.getOssVidioBucketName();
                    dirName = storageConfig.getVidioDir();
                }

                try{
                    aliyunOssService.removeOssFile(bucketName, dirName + file.getFileName());
                }catch (Exception e){
                    log.error("aliyunOss delete File Error!", e);
                    return R.fail("oss获取url失败，请联系管理员");
                }


            }else if(Objects.equals(StorageConfig.IS_USE_MINIO, storageConfig.getIsUseOSS())) {
                try{
                    minioUtil.removeObject(FileConstant.BUCKET_NAME,file.getFileName());
                    return R.ok();
                }catch (Exception e){
                    log.error("文件删除异常", e);
                }
            }

            this.removeById(fileId);
        }
        return R.fail("文件删除失败");
    }
}
