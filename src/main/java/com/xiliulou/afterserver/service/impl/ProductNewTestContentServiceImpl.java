package com.xiliulou.afterserver.service.impl;

import com.xiliulou.afterserver.entity.ProductNewTestContent;
import com.xiliulou.afterserver.mapper.ProductNewTestContentMapper;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductNewTestContentService;
import com.xiliulou.afterserver.web.vo.CabinetCompressionContentVo;
import com.xiliulou.core.web.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
/**
 * (ProductNewTestContent)表服务实现类
 *
 * @author Hardy
 * @since 2023-04-26 17:22:45
 */
@Service("productNewTestContentService")
@Slf4j
public class ProductNewTestContentServiceImpl implements ProductNewTestContentService {
    @Resource
    private ProductNewTestContentMapper productNewTestContentMapper;
    @Autowired
    private ProductNewService productNewService;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public ProductNewTestContent queryByIdFromDB(Long id) {
        return this.productNewTestContentMapper.queryById(id);
    }
    
        /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public  ProductNewTestContent queryByIdFromCache(Long id) {
        return null;
    }


    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<ProductNewTestContent> queryAllByLimit(int offset, int limit) {
        return this.productNewTestContentMapper.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param productNewTestContent 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductNewTestContent insert(ProductNewTestContent productNewTestContent) {
        this.productNewTestContentMapper.insertOne(productNewTestContent);
        return productNewTestContent;
    }

    /**
     * 修改数据
     *
     * @param productNewTestContent 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(ProductNewTestContent productNewTestContent) {
       return this.productNewTestContentMapper.update(productNewTestContent);
         
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
        return this.productNewTestContentMapper.deleteById(id) > 0;
    }

    @Override
    public ProductNewTestContent queryByPid(Long pid) {
        return productNewTestContentMapper.queryByPid(pid);
    }

    @Override
    public R queryInfoByPid(Long pid) {
        CabinetCompressionContentVo vo = productNewService.queryProductTestInfo(pid);
        return R.ok(vo);
    }
}
