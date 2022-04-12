package com.xiliulou.afterserver.controller.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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




    @PostMapping("/user/upload")
    public R uploadFile(@RequestParam("file") MultipartFile file) {
        return fileService.uploadFile(file);
    }

    @DeleteMapping("/user/removeFile")
    public R removeFile(@RequestParam("fileId")Long fileId){
        return fileService.removeFile(fileId);
    }

    @PostMapping("/user/file")
    public R adminPrductFile(@RequestBody File file){

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

        return R.ok(this.batchService.queryAllByLimit(name,0,20));
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
        return fileService.removeFile(id);
    }

    @GetMapping("/user/downLoad")
    public R getFile(@RequestParam("fileName") String fileName, HttpServletResponse response) {
        return fileService.downLoadFile(fileName, response);
    }

}
