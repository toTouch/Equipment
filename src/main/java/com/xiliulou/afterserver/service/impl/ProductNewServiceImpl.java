package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductFile;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.ProductSerialNumber;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.DataUtil;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

/**
 * (ProductNew)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-17 10:29:14
 */
@Service("productNewService")
@Slf4j
public class ProductNewServiceImpl implements ProductNewService {
    @Resource
    private ProductNewMapper productNewMapper;
    @Autowired
    private ProductService productService;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ProductNew queryByIdFromDB(Long id) {
        return this.productNewMapper.queryById(id);
    }

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ProductNew queryByIdFromCache(Long id) {
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
    public List<ProductNew> queryAllByLimit(int offset, int limit,String no,Long modelId,Long startTime,Long endTime) {
        return this.productNewMapper.queryAllByLimit(offset, limit,no,modelId,startTime,endTime);
    }

    /**
     * 新增数据
     *
     * @param productNew 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductNew insert(ProductNew productNew) {
        this.productNewMapper.insertOne(productNew);
        return productNew;
    }

    /**
     * 修改数据
     *
     * @param productNew 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(ProductNew productNew) {
        return this.productNewMapper.update(productNew);

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
        return this.productNewMapper.deleteById(id) > 0;
    }

    @Override
    public R saveAdminProductNew(ProductNew productNew) {
        Product product = productService.getBaseMapper().selectById(productNew.getModelId());
        if (Objects.isNull(product)) {
            return R.fail("产品型号有误，请检查");
        }

        if(Objects.isNull(productNew.getProductCount()) || productNew.getProductCount() <= 0){
           return R.fail("请传入正确的产品数量");
        }

        for (int i = 0; i < productNew.getProductCount(); i++) {
            productNew.setNo(DataUtil.getNo());
            productNew.setCreateTime(System.currentTimeMillis());
            productNew.setDelFlag(ProductNew.DEL_NORMAL);
            this.insert(productNew);
        }

        return R.ok();
    }




    @Override
    public R putAdminProductNew(ProductNew productNew) {
        Integer update = this.update(productNew);
        if (update > 0){
            return R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    public R delAdminProductNew(Long id) {
        Boolean aBoolean = this.deleteById(id);
        if (aBoolean){
            return R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateList(List<ProductNew> productNewList) {

        List<Long> list = new ArrayList();
        productNewList.forEach(e -> {
            list.add(e.getId());
        });

        long count = list.stream().distinct().count();
        boolean isRepeat = count < list.size();
        if (isRepeat){
            return R.fail("有重复数据");
        }

        productNewList.forEach(item -> {
            item.setCreateTime(System.currentTimeMillis());
            int update = productNewMapper.update(item);
            if (update == 0){
                log.error("WX ERROR!   update ProductNew error data:{}",item.toString());
                throw new NullPointerException("数据库异常，请联系管理员");
            }
        });
        return R.ok();
    }

    @Override
    public ProductNew prdouctInfoByNo(String no) {
        LambdaQueryWrapper<ProductNew> eq = new LambdaQueryWrapper<ProductNew>().eq(ProductNew::getNo, no).eq(ProductNew::getDelFlag, ProductNew.DEL_NORMAL);
        return this.productNewMapper.selectOne(eq);
    }

    @Override
    public Integer count(String no,Long modelId,Long startTime,Long endTime) {
        return this.productNewMapper.countProduct(no,modelId,startTime,endTime);
    }

    public static void main(String[] args) {
        List<Integer> list = new ArrayList() {
            {
                add(1);
                add(2);
                add(1);
            }
        };
        long count = list.stream().distinct().count();
        boolean isRepeat = count < list.size();
        System.out.println(count);//输出2
        System.out.println(isRepeat);//输出true


    }
}
