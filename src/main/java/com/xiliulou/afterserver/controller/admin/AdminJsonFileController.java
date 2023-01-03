package com.xiliulou.afterserver.controller.admin;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.server.HttpServerResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.constant.FileConstant;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.ProductFile;
import com.xiliulou.afterserver.entity.WorkOrder;
import com.xiliulou.afterserver.entity.WorkOrderServer;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.PointNewService;
import com.xiliulou.afterserver.service.WorkOrderService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    AliyunOssService aliyunOssService;
    @Autowired
    PointNewService pointNewService;
    @Autowired
    WorkOrderService workOrderService;


    @GetMapping("/admin/file/info/{id}")
    public R fileInfo(@PathVariable("id") Long id ){
        return R.ok(fileService.getById(id));
    }

    @PostMapping("/admin/file")
    public R saveFile(@RequestBody File file){
        if(Objects.equals(File.TYPE_POINTNEW, file.getType())){
            QueryWrapper<File> wrapper = new QueryWrapper<>();
            wrapper.eq("type", File.TYPE_POINTNEW);
            wrapper.eq("file_type", file.getFileType());
            wrapper.eq("bind_id", file.getBindId());
            if(file.getFileType() % 100 == 0){
                Integer count = fileService.getBaseMapper().selectCount(wrapper);
                if(count >= 2){
                    return R.fail("该类其他图片已达上限，请删除图片后继续上传！");
                }
            }

            if(!(Objects.nonNull(file.getFileType()) && file.getFileType() % 100 == 0)){
                fileService.getBaseMapper().delete(wrapper);
            }

            PointNew pointNew = pointNewService.getById(file.getBindId());
            if(Objects.nonNull(pointNew)) {
                PointNew update = new PointNew();
                update.setId(pointNew.getId());
                update.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
                pointNewService.updateById(update);
            }
        }

        if(Objects.equals(File.TYPE_WORK_ORDER, file.getType())){
            QueryWrapper<File> wrapper = new QueryWrapper<>();
            wrapper.eq("type", File.TYPE_WORK_ORDER);
            wrapper.eq("bind_id", file.getBindId());
            if(Objects.equals(0, file.getFileType())){
                wrapper.eq("file_type", 0);
            }else if(file.getFileType() < 90000){
                wrapper.ge("file_type", 1).lt("file_type", 90000).eq("server_id", file.getServerId());
            }else if(file.getFileType() == 90000){
                wrapper.eq("file_type", 90000);
            }

            List<File> list = fileService.getBaseMapper().selectList(wrapper);
            int count = CollectionUtils.isEmpty(list) ? 0 : list.size();

            if(Objects.equals(0, file.getFileType()) && count >= 6){
                return R.fail("该类其他图片已达上限，请删除图片后继续上传！");
            }else if (file.getFileType() < 90000 && count >= 10){
                return R.fail("该类其他图片已达上限，请删除图片后继续上传！");
            }else if(file.getFileType() == 90000 && count >= 1){
                list.stream().forEach(item -> {
                    fileService.getBaseMapper().deleteById(item.getId());
                    this.delFile(item.getId(), 1);//1为视频
                });
            }

            WorkOrder update = new WorkOrder();
            update.setId(file.getBindId());
            update.setAuditStatus(WorkOrder.AUDIT_STATUS_WAIT);
            workOrderService.updateById(update);
        }

        file.setCreateTime(System.currentTimeMillis());
        return R.ok(fileService.save(file));
    }

    @PostMapping("/admin/batch/file")
    public R saveFile(@RequestParam("bindId")Long bindId, @RequestParam("fileNames")String fileName, @RequestParam("type")Integer type){
        if(StringUtils.isBlank(fileName)) {
            return R.failMsg("上传图片为空");
        }

        final int maxSize = 20;

        Integer pointFileCount = fileService.queryCountByPointId(bindId);
        if((pointFileCount) >= maxSize) {
            return R.failMsg("超出图片上限，最大可上传20张图片");
        }

        File file = new File();
        file.setFileName(fileName);
        file.setBindId(bindId);
        file.setType(type);
        file.setCreateTime(System.currentTimeMillis());
        fileService.save(file);
        return R.ok();
    }

    /**
     * 获取oss签名
     * @param moduleName
     * @return
     */
    @GetMapping("/admin/oss/getPolicy")
    public Map<String, String> policy(@RequestParam("moduleName")String moduleName) {
        String name = moduleName + "/";
        Map<String, String> ossUploadSign = aliyunOssService.getOssUploadSign(name);
        return ossUploadSign;
    }

    @DeleteMapping("/admin/file/{id}")
    public R delFile(@PathVariable("id") Long id, @RequestParam(value = "fileType", required = false, defaultValue = "0")Integer fileType){
        return R.ok(fileService.removeFile(id, fileType));
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
    public R getFile(@RequestParam("fileName") String fileName,@RequestParam(value = "fileType", required = false, defaultValue = "0") Integer fileType, HttpServletResponse response) {
        return fileService.downLoadFile(fileName,fileType, response);
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
