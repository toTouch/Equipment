package com.xiliulou.afterserver.controller.user;

import com.alibaba.excel.util.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2021/8/17 11:46
 * @mood
 */
@RestController
@RequestMapping("/admin/wechat")
public class JsonUserPointProductController {
    @Autowired
    private PointNewService pointNewService;
    @Autowired
    private BatchService batchService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductNewService productNewService;
    @Autowired
    private FileService fileService;
    @Autowired
    private StorageConfig storageConfig;
    @Autowired
    private AliyunOssService aliyunOssService;




    @PostMapping("/user/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "fileType", required = false, defaultValue = "0") Integer fileType) {
        return fileService.uploadFile(file, fileType);
    }

    /**
     * 获取oss签名
     * @param moduleName
     * @return
     */
    @GetMapping("/oss/getPolicy")
    public Map<String, String> policy(String moduleName) {
        String name = moduleName + "/";
        Map<String, String> ossUploadSign = aliyunOssService.getOssUploadSign(name);
        return ossUploadSign;
    }


    @DeleteMapping("/user/removeFile")
    public R removeFile(Long fileId){
        return fileService.removeFile(fileId);
    }

    @PostMapping("/user/file")
    public R adminPrductFile(@RequestBody File file){

        if(Objects.equals(File.TYPE_POINTNEW, file.getFileType())){
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

            if(!(file.getFileType() % 100 == 0)){
                fileService.getBaseMapper().delete(wrapper);
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
                });
            }
        }


        file.setCreateTime(System.currentTimeMillis());
        int insert = fileService.getBaseMapper().insert(file);
        if (insert == 0){
            return R.fail("文件保存失败");
        }
        return R.ok(file);
    }


    @GetMapping("/user/pointNew/list")
    public R pointList( @RequestParam(value = "name",required = false) String name){
        return R.ok(pointNewService.queryAllByLimit(0,10,name,null,null,null,null,null,null,null, null, null));
    }

    /**
     * 产品型号
     */
    @GetMapping("/user/product/page")
    public R getPage(String name) {
        return R.ok(productService.getAllByName(name));
    }


    /**
     * 产品批次列表
     */
    @GetMapping("/user/batch/list")
    public R selectOne(@RequestParam(value = "name",required = false) String name) {

        return R.ok(this.batchService.queryAllByLimit(name,0,20, null, null));
    }

    /**
     * 产品
     */
    @PutMapping("/user/update/product")
    public R userUpdateProduct(@RequestBody List<ProductNew> productNewList){
       return productNewService.updateList(productNewList);
    }

    /**
     * 获取产品
     */
    @GetMapping("/user/product")
    public R prdouctInfoByNo(@RequestParam(value = "name",required = false) String name) {
        return R.ok(productNewService.prdouctInfoByNo(name));
    }

    /**
     * 点位
     */
    @PutMapping("/user/update/point")
    public R userUpdatePoint(@RequestBody PointNew pointNew){
        return R.ok(pointNewService.putAdminPointNew(pointNew));
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/user/file/{id}")
    public R delFile(@PathVariable("id") Long id){
        return R.ok(fileService.removeById(id));
    }

    @GetMapping("/user/downLoad")
    public void getFile(@RequestParam("fileName") String fileName, @RequestParam(value = "fileType", required = false, defaultValue = "0")Integer fileType, HttpServletResponse response) {
        fileService.downLoadFile(fileName,fileType, response);
    }

}
