package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.ImportTemplate;
import com.xiliulou.afterserver.mapper.ImportTemplateMapper;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.ImportTemplateService;
import com.xiliulou.afterserver.util.MinioUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.ImportTemplateQuery;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2022/2/14 16:46
 * @mood
 */
public class ImportTemplateServiceImpl extends ServiceImpl<ImportTemplateMapper, ImportTemplate> implements ImportTemplateService {
    @Autowired
    ImportTemplateMapper importTemplateMapper;
    @Autowired
    MinioUtil minioUtil;
    @Autowired
    FileService fileService;

    @Override
    public R upload(ImportTemplateQuery importTemplateQuery) {
        ImportTemplate importTemplateOld = importTemplateMapper.selectOne(
                new QueryWrapper<ImportTemplate>().eq("type", importTemplateQuery.getType()));
        int len = 0;
        if(Objects.isNull(importTemplateOld)){
            ImportTemplate importTemplate = new ImportTemplate();
            importTemplate.setFileName(importTemplateQuery.getFileName());
            importTemplate.setType(importTemplate.getType());
            importTemplate.setCreateTime(System.currentTimeMillis());
            importTemplate.setUpdateTime(System.currentTimeMillis());
            len = importTemplateMapper.insert(importTemplate);
        }else{
            ImportTemplate importTemplate = new ImportTemplate();
            importTemplate.setId(importTemplateOld.getId());
            importTemplate.setFileName(importTemplateQuery.getFileName());
            importTemplate.setUpdateTime(System.currentTimeMillis());
            len = importTemplateMapper.updateById(importTemplate);

            if(len > 0){
                String fileName = importTemplateOld.getFileName();
                int separator = fileName.lastIndexOf(StrUtil.DASHED);
                String bucketName = fileName.substring(0, separator);

                minioUtil.removeObject(bucketName, fileName);
            }
        }

        if(len > 0){
            return R.ok();
        }

        return R.fail("保存失败");
    }

    @Override
    public R infoByType(String type, HttpServletResponse response) {
        SecurityUtils.getUserInfo();
        ImportTemplate importTemplateOld = importTemplateMapper.selectOne(
                new QueryWrapper<ImportTemplate>().eq("type", type));

        if(Objects.nonNull(importTemplateOld)){
            fileService.downLoadFile(importTemplateOld.getFileName(), response);
            return R.ok();
        }

       return R.fail("请联系管理员上传模板");
    }
}
