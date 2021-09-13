package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.util.DataUtil;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
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
    @Autowired
    private FileService fileService;
    @Autowired
    private BatchService batchService;
    @Autowired
    private PointProductBindMapper pointProductBindMapper;
    @Autowired
    private PointNewMapper pointNewMapper;

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

    @Override
    public R getProductFile(Long id) {
        List<File> fileList = fileService.queryByProductNewId(id);
        return R.ok(fileList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateStatusFromBatch(List<Long> ids, Integer status) {
        int row = this.productNewMapper.updateStatusFromBatch(ids,status);
        if(row == 0){
            return R.fail("未修改数据");
        }
        return R.ok();
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

    @Override
    public R queryProductInfo(String no) {
        Map<String, Object> map = new HashMap<>();

        QueryWrapper<ProductNew> wrapper = new QueryWrapper<>();
        wrapper.eq("no", no);
        ProductNew productNew = productNewMapper.selectOne(wrapper);

        if(ObjectUtils.isNotNull(productNew)){
            Product product = productService.getById(productNew.getModelId());
            if(ObjectUtils.isNotNull(product)){
                map.put("name", product.getName());
            }
            Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
            if(ObjectUtils.isNotNull(batch)){
                map.put("batchNo", batch.getBatchNo());
            }
            map.put("no", no);
        }

        if(map.size() < 3 && map.size() > 1){
            log.error("查询结果有误，请重新录入: name={}, batchNo={}, no={}", map.get("name"), map.get("batchNo"), map.get("no"));
            return R.fail("查询结果有误，请重新录入");
        }

        if(map.isEmpty()){
            log.info("查无此产品");
            return R.fail("查无此产品");
        }

        return R.fail(map);
    }

    @Override
    public R queryLikeProductByNo(String no){
        QueryWrapper<ProductNew> wrapper = null;
        if(!StringUtils.checkValNull(no)){
            wrapper = new QueryWrapper<>();
            wrapper.like("no", no);
        }
        List<ProductNew> list = productNewMapper.selectList(wrapper);
        return  R.fail(list);
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public R bindPoint(Long productId, Long pointId) {
        /*PointProductBind pointProductBind = pointProductBindMapper
                .selectOne(new QueryWrapper<PointProductBind>().eq("product_id", productId));

        if(ObjectUtils.isNotNull(pointProductBind)){
            PointNew pointNew = pointNewMapper.selectById(pointProductBind.getPointId());
            if(ObjectUtils.isNotNull(pointNew)){
                return R.fail("您选择的产品已绑定到【" + pointNew.getName() + "】点位,请解绑！");
            }
        }*/

        PointProductBind pointProductBind = pointProductBindMapper
                    .selectOne(new QueryWrapper<PointProductBind>()
                        .eq("product_id", productId));

        if(ObjectUtils.isNotNull(pointProductBind)){
            pointProductBindMapper.deleteById(pointProductBind.getId());
        }

        PointProductBind bind = new PointProductBind();
        bind.setPointId(pointId);
        bind.setProductId(productId);
        pointProductBindMapper.insert(bind);

        return R.ok();
    }
}
