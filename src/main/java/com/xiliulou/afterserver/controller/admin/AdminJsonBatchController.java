package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * (Batch)表控制层
 *
 * @author Hardy
 * @since 2021-08-16 15:52:08
 */
@RestController
public class AdminJsonBatchController {
    /**
     * 服务对象
     */
    @Resource
    private BatchService batchService;
    @Autowired
    private ProductFileMapper productFileMapper;
    @Autowired
    SupplierService supplierService;
    @Autowired
    ProductNewMapper productNewMapper;
    @Autowired
    ProductService productService;


    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/admin/batch/selectOne")
    public Batch selectOne(Long id) {
        return this.batchService.queryByIdFromDB(id);
    }

    /**
     * 保存
     */
    @PostMapping("/admin/batch")
    public R saveBatch(@RequestBody Batch batch){
        return batchService.saveBatch(batch);
    }

    /**
     * 修改
     */
    @PutMapping("/admin/batch")
    public R updateBatch(@RequestBody Batch batch){
        if(Objects.isNull(batch.getType())){
            return R.fail("请输入批次类型");
        }
        this.batchService.update(batch);

        if(Objects.nonNull(batch.getFileId())){
            ProductFile productFile = new ProductFile();
            productFile.setId(batch.getFileId());
            productFile.setFileStr(batch.getFileStr());
            productFile.setProductFileName(batch.getProductFileName());
            productFileMapper.updateById(productFile);
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @GetMapping("/admin/batch/list")
    public R selectOne(@RequestParam(value = "batchNo",required = false) String batchNo,
                       @RequestParam(value = "modelId",required = false)Long modelId,
                       @RequestParam(value = "supplierId",required = false)Long supplierId,
                       @RequestParam(value = "offset") int offset,
                       @RequestParam(value = "limit") int limit) {

        List<Batch> batches = this.batchService.queryAllByLimit(batchNo, offset, limit, modelId, supplierId);
        if (Objects.nonNull(batches)){
            batches.forEach(item -> {
                List<ProductFile> productFiles = productFileMapper.selectList(new LambdaQueryWrapper<ProductFile>().eq(ProductFile::getProductId, item.getId()));
                item.setProductFileList(productFiles);

                Product product = productService.getById(item.getModelId());
                if(Objects.nonNull(product)){
                    item.setModelName(product.getName());
                }

                Supplier supplier = supplierService.getById(item.getSupplierId());
                if(Objects.nonNull(supplier)){
                    item.setSupplierName(supplier.getName());
                }
            });
        }


        Long count = this.batchService.count(batchNo, modelId, supplierId);

        HashMap<String, Object> stringObjectHashMap = new HashMap<>(2);
        stringObjectHashMap.put("data",batches);
        stringObjectHashMap.put("count",count);

        return R.ok(stringObjectHashMap);
    }

    @DeleteMapping("/admin/batch/{id}")
    public R delOne(@PathVariable("id") Long id){
        return batchService.delOne(id);
    }

    @GetMapping("/admin/batch/factory")
    public R queryByfactory(){
        if(!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)){
            return R.fail("登陆用户非工厂类型");
        }

        return batchService.queryByfactory();
    }
}
