package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.vo.PointNewInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import lombok.extern.slf4j.Slf4j;

/**
 * (PointNew)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-17 10:28:43
 */
@Service("pointNewService")
@Slf4j
public class PointNewServiceImpl extends ServiceImpl<PointNewMapper, PointNew> implements PointNewService {
    @Resource
    private PointNewMapper pointNewMapper;
    @Autowired
    private PointProductBindService pointProductBindService;
    @Autowired
    private ProductNewService productNewService;
    @Autowired
    private CityService cityService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BatchService batchService;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public PointNew queryByIdFromDB(Long id) {
        return this.pointNewMapper.selectOne(new LambdaQueryWrapper<PointNew>().eq(PointNew::getId, id).eq(PointNew::getDelFlag, PointNew.DEL_NORMAL));
    }

    /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public PointNew queryByIdFromCache(Long id) {
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
    public List<PointNew> queryAllByLimit(int offset, int limit, String name,Integer cid,Integer status, Long customerId,Long startTime,Long endTime) {
        return this.pointNewMapper.queryAllByLimit(offset, limit, name,cid,status,customerId,startTime,endTime);
    }

    /**
     * 新增数据
     *
     * @param pointNew 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PointNew insert(PointNew pointNew) {
        this.pointNewMapper.insertOne(pointNew);
        return pointNew;
    }

    /**
     * 修改数据
     *
     * @param pointNew 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(PointNew pointNew) {
        return this.pointNewMapper.update(pointNew);

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
        return this.pointNewMapper.deleteById(id) > 0;
    }

    @Override
    public R saveAdminPointNew(PointNew pointNew) {
        pointNew.setDelFlag(PointNew.DEL_NORMAL);
        this.insert(pointNew);
        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R putAdminPointNew(PointNew pointNew) {
        PointNew queryById = pointNewMapper.queryById(pointNew.getId());
        if (Objects.isNull(queryById)){
            return R.fail("请传入点位id");
        }

        Integer row = this.update(pointNew);
        if (row == 0) {
            return R.fail("数据库错误");
        }

        if (pointNew.getProductIds().isEmpty()) {
            return R.fail("请传入点位id");
        }


        pointNew.getProductIds().forEach(item -> {
            PointProductBind pointProductBind = new PointProductBind();
            pointProductBind.setPointId(pointNew.getId());
            pointProductBind.setProductId(item);
            pointProductBindService.insert(pointProductBind);

            ProductNew productNew = productNewService.queryByIdFromDB(item);
            productNew.setExpirationStartTime(queryById.getCreateTime());

            String date = DateUtils.stampToDate(queryById.getCreateTime().toString());
            String[] split = date.split("-");
            String s = split[0];
            long l = Long.parseLong(s);
            long l1 = l + productNew.getYears();
            String s1 = l1 + split[1] + split[2];
            long l2 = 0;
            try {
                l2 = DateUtils.dateToStamp(s1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            productNew.setExpirationEndTime(l2);
            Integer update = productNewService.update(productNew);
            if (update == 0){
                log.error("WX ERROR!   update ProductNew error data:{}",productNew.toString());
                throw new NullPointerException("数据库异常，请联系管理员");
            }

        });

        return R.ok();
    }

    @Override
    public R delAdminPointNew(Long id) {
        PointNew pointNew = this.queryByIdFromDB(id);

        if (Objects.isNull(pointNew)) {
            return R.fail("未查询到数据");
        }
        pointNew.setDelFlag(PointNew.DEL_DEL);

        Integer row = this.update(pointNew);
        if (row > 0) {
            return R.ok();
        }

        return R.fail("数据库错误");
    }

    @Override
    public R putAdminPoint(PointNew pointNew) {
        int update = this.pointNewMapper.update(pointNew);
        if (update>0){
            return R.ok();
        }
        return R.fail("修改失败");
    }

    @Override
    public R pointInfo(Long pid) {
        LambdaQueryWrapper<PointNew> queryWrapper = new LambdaQueryWrapper<PointNew>().eq(PointNew::getId, pid).eq(PointNew::getDelFlag, PointNew.DEL_NORMAL);
        PointNew pointNew = this.pointNewMapper.selectOne(queryWrapper);

        if (Objects.isNull(pointNew)){
            return R.fail("未查询到相关数据");
        }

        PointNewInfoVo pointNewInfoVo = new PointNewInfoVo();

        if (Objects.nonNull(pointNew.getCityId())){
            City byId = cityService.getById(pointNew.getCityId());
            pointNew.setCityName(byId.getName());
            Province province = provinceService.queryByIdFromDB(byId.getPid());
            pointNew.setProvince(province.getName());
        }

        if (Objects.nonNull(pointNew.getCustomerId())){
            Customer byId = customerService.getById(pointNew.getCustomerId());
            if (Objects.nonNull(byId)){
                pointNew.setCustomerName(byId.getName());
            }
        }

        pointNewInfoVo.setPointNew(pointNew);
        List<File> pointFileList = fileService.queryByPointId(pid);
        pointNewInfoVo.setPointFileList(pointFileList);

        List<PointProductBind> pointProductBindList = pointProductBindService.queryByPointNewId(pid);
        if (Objects.nonNull(pointProductBindList)){
            pointProductBindList.forEach(item -> {
                ProductNew productNew = productNewService.queryByIdFromDB(item.getProductId());
                if (Objects.isNull(productNew)){
                    return;
                }

                Product product = productService.getBaseMapper().selectById(productNew.getModelId());
                if (Objects.nonNull(product)){
                    productNew.setModelName(product.getName());
                }

                Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
                if (Objects.nonNull(batch)){
                    productNew.setBatchName(batch.getBatchNo());
                }

                List<File> productFileList = fileService.queryByProductNewId(productNew.getId());
                productNew.setFileList(productFileList);
            });

        }
        return R.ok(pointNewInfoVo);
    }

    @Override
    public Integer countPoint(String name, Integer cid, Integer status, Long customerId, Long startTime, Long endTime) {
        return this.pointNewMapper.countPoint(name,cid,status,customerId,startTime,endTime);
    }
}
