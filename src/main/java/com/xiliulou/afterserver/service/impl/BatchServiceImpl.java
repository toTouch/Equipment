package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.BatchMapper;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * (Batch)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-16 15:50:17
 */
@Service("batchService")
@Slf4j
public class BatchServiceImpl implements BatchService {
    @Resource
    private BatchMapper batchMapper;
    @Autowired
    private ProductNewService productNewService;
    @Autowired
    private ProductService productService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ProductFileMapper productFileMapper;
    @Autowired
    private ProductNewMapper productNewMapper;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Batch queryByIdFromDB(Long id) {
        return this.batchMapper.queryById(id);
    }

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Batch queryByIdFromCache(Long id) {
        return null;
    }


    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Batch> queryAllByLimit(String batchNo,int offset, int limit, Long modelId, Long supplierId) {
        return this.batchMapper.queryAllByLimit(batchNo,offset, limit,modelId , supplierId);
    }

    /**
     * 新增数据
     *
     * @param batch 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Batch insert(Batch batch) {
        this.batchMapper.insertOne(batch);
        return batch;
    }

    /**
     * 修改数据
     *
     * @param batch 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(Batch batch) {
        return this.batchMapper.update(batch);
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        return this.batchMapper.deleteById(id) > 0;
    }

    @Override
    public Long count(String batchNo, Long modelId, Long supplierId) {
        return this.batchMapper.count(batchNo, modelId, supplierId);
    }

    @Override
    public Batch queryByName(String batchName) {
        return batchMapper.selectOne(new QueryWrapper<Batch>().eq("batch_no", batchName));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveBatch(Batch batch) {
        Product product = productService.getBaseMapper().selectById(batch.getModelId());
        if (Objects.isNull(product)) {
            return R.fail("产品型号有误，请检查");
        }
        if(Objects.isNull(product.getCode())){
            return R.fail("产品型号编码为空,请重新选择");
        }

        if(Objects.isNull(batch.getProductNum()) || batch.getProductNum() <= 0){
            return R.fail("请传入正确的产品数量");
        }

        Supplier supplier = supplierService.getById(batch.getSupplierId());
        if (Objects.isNull(supplier)) {
            return R.fail("供应商选择有误，请检查");
        }

        if(Objects.isNull(supplier.getCode())){
            return R.fail("供应商编码为空,请重新选择");
        }

        if(Objects.isNull(batch.getType())){
            return R.fail("请输入批次类型");
        }

        batch.setCreateTime(System.currentTimeMillis());
        batch.setUpdateTime(System.currentTimeMillis());
        Batch insert = this.insert(batch);

        ProductFile productFile = new ProductFile();
        productFile.setProductId(insert.getId());
        productFile.setFileStr(batch.getFileStr());
        productFile.setProductFileName(batch.getProductFileName());
        productFileMapper.insert(productFile);


        ProductNew productNew = new ProductNew();
        productNew.setModelId(batch.getModelId());
        productNew.setBatchId(batch.getId());
        productNew.setStatus(0);
        productNew.setType(batch.getProductType());
        productNew.setSupplierId(batch.getSupplierId());
        productNew.setProductCount(batch.getProductNum());

        StringBuilder codeStr = new StringBuilder();
        codeStr.append(product.getCode()).append("-");
        codeStr.append(supplier.getCode()).append(batch.getBatchNo());
        if(Objects.nonNull(productNew.getType())){
            codeStr.append(productNew.getType());
        }
        productNew.setCode(codeStr.toString());

        Integer serialNum = productNewMapper.queryMaxSerialNum(codeStr.toString());
        if(Objects.isNull(serialNum)){
            serialNum = 0;
        }

        for (int i = 0; i < productNew.getProductCount(); i++) {
            serialNum++;
            String serialNumStr = String.format("%04d", serialNum);
            StringBuilder sb = new StringBuilder();
            sb.append(product.getCode()).append("-");
            sb.append(supplier.getCode()).append(batch.getBatchNo())
                    .append(serialNumStr);
            if(Objects.nonNull(productNew.getType())){
                sb.append(productNew.getType());
            }

            productNew.setSerialNum(serialNumStr);
            productNew.setNo(sb.toString());
            productNew.setCreateTime(System.currentTimeMillis());
            productNew.setDelFlag(ProductNew.DEL_NORMAL);
            productNewMapper.insertOne(productNew);
        }


        return R.ok();
    }

    @Override
    public R delOne(Long id) {
        List<ProductNew> list = productNewService.queryByBatch(id);
        if(CollectionUtils.isNotEmpty(list)){
            return R.fail("删除失败,请删除产品列表中关联的数据");
        }

        this.deleteById(id);
        return R.ok();
    }
}
