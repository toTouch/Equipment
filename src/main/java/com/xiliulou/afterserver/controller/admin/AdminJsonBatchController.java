package com.xiliulou.afterserver.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.mapper.ProductMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.mapper.SupplierMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    SupplierMapper supplierMapper;
    @Autowired
    ProductMapper productMapper;
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
     * 修改批次信息
     */
    @PutMapping("/admin/batch")
    public R updateBatch(@RequestBody Batch batch){
        if(Objects.isNull(batch.getType())){
            return R.fail("请输入批次类型");
        }
//        旧数据拥有相同批次 这里不再限制
//        List<Batch> batchOlds = batchService.queryByName(batch.getBatchNo());
//        if(Objects.nonNull(batchOlds) && !Objects.equals(batchOlds.get(0).getId(), batch.getId())){
//            return R.fail("批次号已存在");
//        }

        this.batchService.update(batch);
        //

        if(Objects.nonNull(batch.getFileId())
                && StringUtils.isNotBlank(batch.getFileStr())
                && StringUtils.isNotBlank(batch.getProductFileName())){
            ProductFile productFile = new ProductFile();
            productFile.setId(batch.getFileId());
            productFile.setFileStr(batch.getFileStr());
            productFile.setProductFileName(batch.getProductFileName());
            productFileMapper.updateById(productFile);
        }
        return R.ok();
    }

    /**
     * 产品管理_批次列表
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @param limit  查询条数
     *
     */
    @GetMapping("/admin/batch/list")
    public R selectOne(@RequestParam(value = "batchNo",required = false) String batchNo,
                       @RequestParam(value = "modelId",required = false)Long modelId,
                       @RequestParam(value = "supplierId",required = false)Long supplierId,
                       @RequestParam(value = "notShipped",required = false)Integer notShipped,
                       @RequestParam(value = "offset") int offset,
                       @RequestParam(value = "limit") int limit) {

        List<Batch> batches = this.batchService.queryAllByLimit(batchNo, offset, limit, modelId, supplierId, notShipped);
        if (Objects.nonNull(batches)){
            
            // 批次id 查询附件信息
            List<Long> collectIds = batches.stream().map(Batch::getId).collect(Collectors.toList());
            List<ProductFile> productFileList = productFileMapper.selectList(new LambdaQueryWrapper<ProductFile>().in(ProductFile::getProductId, collectIds));
            // productFileList分组操作
            Map<Long, List<ProductFile>> longProductFileListMap = productFileList.stream().collect(Collectors.groupingBy(ProductFile::getProductId));
            
            // 产品型号ids
            List<Long> collectModelIds = batches.stream().map(Batch::getModelId).collect(Collectors.toList());
            List<Product> productList = productMapper.selectList(new LambdaQueryWrapper<Product>().in(Product::getId, collectModelIds));
            Map<Long, Product> longProductNewMap = productList.stream().collect(Collectors.toMap(Product::getId, Function.identity(), (oldValue, newValue) -> newValue));
            
            // 供应商/工厂id
            List<Long> collectSupplierIds = batches.stream().map(Batch::getSupplierId).collect(Collectors.toList());
            List<Supplier> supplierList = supplierMapper.selectList(new LambdaQueryWrapper<Supplier>().in(Supplier::getId, collectSupplierIds));
            Map<Long, Supplier> longSupplierMap = supplierList.stream().collect(Collectors.toMap(Supplier::getId, Function.identity(), (oldValue, newValue) -> newValue));
            
            batches.forEach(item -> {
                // 查询附件信息
                List<ProductFile> productFiles = longProductFileListMap.get(item.getId());
                
                item.setProductFileList(productFiles);
                // 产品型号
                Product product = longProductNewMap.get(item.getModelId());
                if(Objects.nonNull(product)){
                    item.setModelName(product.getName());
                }
                // 工厂名
                Supplier supplier = longSupplierMap.get(item.getSupplierId());
                if(Objects.nonNull(supplier)){
                    item.setSupplierName(supplier.getName());
                }
            });
        }

        // 条数
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

}
