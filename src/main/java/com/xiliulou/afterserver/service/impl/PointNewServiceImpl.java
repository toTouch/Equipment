package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.*;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.vo.PointNewInfoVo;
import com.xiliulou.afterserver.web.query.CameraInfoQuery;
import com.xiliulou.afterserver.web.query.PointAuditStatusQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.query.ProductInfoQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

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
    @Autowired
    private PointProductBindMapper pointProductBindMapper;
    @Autowired
    private ProductSerialNumberMapper productSerialNumberMapper;
    //@Autowired
    //private ProductNewMapper productNewMapper;
    //@Autowired
    //private PointProductBindMapper pointProductBindMapper;
    //@Autowired
    //private FileMapper fileMapper;
    @Autowired
    private UserService userService;

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
    public List<PointNew> queryAllByLimit(int offset, int limit, String name,Integer cid,
                                          Integer status, Long customerId,Long startTime,Long endTime,Long createUid,String snNo,Integer productSeries, Integer auditStatus) {
        return this.pointNewMapper.queryAllByLimit(offset, limit, name,cid,status,customerId,startTime,endTime,createUid,snNo, productSeries, auditStatus);
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
        R r = checkPropertes(pointNew);
        if(Objects.nonNull(r)){
            return r;
        }
        if(Objects.nonNull(pointNew.getProductInfoList())) {
            Iterator<ProductInfoQuery> iterator = pointNew.getProductInfoList().iterator();
            while (iterator.hasNext()){
                ProductInfoQuery productInfoQuery = iterator.next();
                if(Objects.isNull(productInfoQuery.getProductId()) || Objects.isNull(productInfoQuery.getNumber())){
                    iterator.remove();
                }
            }
            String productInfo = JSON.toJSONString(pointNew.getProductInfoList());
            pointNew.setProductInfo(productInfo);
        }
        if(Objects.nonNull(pointNew.getCameraInfoList())) {
            Iterator<CameraInfoQuery> iterator = pointNew.getCameraInfoList().iterator();
            while(iterator.hasNext()){
                CameraInfoQuery cameraInfoQuery = iterator.next();
                if(StringUtils.isBlank(cameraInfoQuery.getCameraSupplier())
                        && StringUtils.isBlank(cameraInfoQuery.getCameraSn())
                        && StringUtils.isBlank(cameraInfoQuery.getCameraNumber())){
                    iterator.remove();
                }
            }
            String cameraInfo = JSON.toJSONString(pointNew.getCameraInfoList());
            pointNew.setCameraInfo(cameraInfo);
        }
        pointNew.setDelFlag(PointNew.DEL_NORMAL);
        pointNew.setCreateTime(System.currentTimeMillis());
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
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
            //ProductNew productNew = productNewService.queryByIdFromDB(item);
            /*List<PointProductBind> pointProductBindList = pointProductBindService.queryByPointNewIdAndProductId(pointNew.getId(),item);
            if (pointProductBindList.size() > 0){
                throw new NullPointerException("机柜"+productNew.getNo()+"已存在此点位了");
            }*/
            PointProductBind oldPointProductBind = pointProductBindMapper
                    .selectOne(new QueryWrapper<PointProductBind>()
                            .eq("product_id", item));

            if(ObjectUtils.isNotNull(oldPointProductBind)){
                pointProductBindMapper.deleteById(oldPointProductBind.getId());
            }

            PointProductBind pointProductBind = new PointProductBind();
            pointProductBind.setPointId(pointNew.getId());
            pointProductBind.setProductId(item);
            pointProductBindService.insert(pointProductBind);


            /** 产品质保时间不取点位时间了
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
            }*/

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
        R r = checkPropertes(pointNew);
        if(Objects.nonNull(r)){
            return r;
        }
        if(Objects.nonNull(pointNew.getProductInfoList())) {
            Iterator<ProductInfoQuery> iterator = pointNew.getProductInfoList().iterator();
            while (iterator.hasNext()){
                ProductInfoQuery productInfoQuery = iterator.next();
                if(Objects.isNull(productInfoQuery.getProductId()) || Objects.isNull(productInfoQuery.getNumber())){
                    iterator.remove();
                }
            }
            String productInfo = JSON.toJSONString(pointNew.getProductInfoList());
            pointNew.setProductInfo(productInfo);
        }
        if(Objects.nonNull(pointNew.getCameraInfoList())) {
            Iterator<CameraInfoQuery> iterator = pointNew.getCameraInfoList().iterator();
            while(iterator.hasNext()){
                CameraInfoQuery cameraInfoQuery = iterator.next();
                if(StringUtils.isBlank(cameraInfoQuery.getCameraSupplier())
                        && StringUtils.isBlank(cameraInfoQuery.getCameraSn())
                        && StringUtils.isBlank(cameraInfoQuery.getCameraNumber())){
                    iterator.remove();
                }
            }
            String cameraInfo = JSON.toJSONString(pointNew.getCameraInfoList());
            pointNew.setCameraInfo(cameraInfo);
        }
        int update = this.pointNewMapper.update(pointNew);
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
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
        ArrayList<ProductNew> productNews = new ArrayList<>();
        Map<String, Long> map = new HashMap<>();
        List<Map> productTypeAndNumList = new ArrayList<>();

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
                productNews.add(productNew);

                //productTypeAndNum
                map.put(product.getName(), map.containsKey(product.getName()) ? map.get(product.getName()) + 1 : 1);
            });

            pointNewInfoVo.setProductNew(productNews);

            for (Map.Entry entry : map.entrySet()){
                Map item = new HashMap();
                item.put("productType", entry.getKey());
                item.put("productNum", entry.getValue());
                productTypeAndNumList.add(item);
            }
            pointNewInfoVo.setProductTypeAndNum(productTypeAndNumList);
        }
        return R.ok(pointNewInfoVo);
    }

    @Override
    public Integer countPoint(String name, Integer cid, Integer status, Long customerId,
                              Long startTime, Long endTime, Long createUid,String snNo, Integer productSeries, Integer auditStatus) {
        return this.pointNewMapper.countPoint(name,cid,status,customerId,startTime,endTime,createUid,snNo,productSeries,auditStatus);
    }

    @Override
    public List<PointNew> queryAllByLimitExcel(String name, Integer cid, Integer status, Long customerId, Long startTime, Long endTime, Long createUid,String snNo,Integer productSeries, Integer auditStatus) {
        return this.pointNewMapper.queryAllByLimitExcel(name,cid,status,customerId,startTime,endTime,createUid,snNo,productSeries,auditStatus);
    }

    @Override
    public R putAdminPointNewCreateUser(Long id, Long createUid){
        if(Objects.isNull(id) || Objects.isNull(createUid)){
            return R.fail("参数非法，请检查");
        }

        User user = userService.getUserById(createUid);
        if(Objects.isNull(user)){
            return R.fail("没有查询到该用户");
        }

        Integer len = pointNewMapper.putAdminPointNewCreateUser(id, createUid);

        if(len != null && len > 0){
            return R.ok();
        }

        return R.fail("修改失败");
    }

    public R checkPropertes(PointNew pointNew){
        if(Objects.isNull(pointNew.getAuditStatus())){
            return R.fail("请填写审核状态");
        }
        if(Objects.isNull(pointNew.getProductSeries())){
            return R.fail("请填写产品系列");
        }
        if(Objects.isNull(pointNew.getCityId())){
            return R.fail("请填写城市信息");
        }
        if(Objects.isNull(pointNew.getCustomerId())){
            return R.fail("请填写客户信息");
        }
        if(StringUtils.isBlank(pointNew.getName())){
            return R.fail("请填写点位名称");
        }
        if(Objects.isNull(pointNew.getStatus())){
            return R.fail("请填写点位状态");
        }
        if(Objects.isNull(pointNew.getInstallType())){
            return R.fail("请填写安装类型");
        }
        return null;
    }

    /*@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public R saveCache(Long pointId, Long modelId, String no, Long batch) {
        if(modelId == null && no == null){
            log.warn("柜机型号和产品编号不能都为空，请填入至少一项");
            return R.fail("柜机型号和产品编号不能都为空，请填入至少一项");
        }
        if(no != null){
            if(modelId != null){
                log.warn("选择产品编号后，请不要选择柜机型号或批次号");
                return R.fail("选择产品编号后，请不要选择柜机型号或批次号");
            }
            QueryWrapper<ProductNew> wrapper = new QueryWrapper<>();
            wrapper.eq("no", no);
            ProductNew productNew = productNewMapper.selectOne(wrapper);
            if(ObjectUtils.isNotNull(productNew)){
                PointProductBind pointProductBind = new PointProductBind();
                pointProductBind.setPointId(pointId);
                pointProductBind.setProductId(productNew.getId());
                pointProductBindService.insert(pointProductBind);
            }else{
                log.info("查无此产品编号：no={}",no);
                return R.fail("查无此产品编号，请检查");
            }
        }else{
            ProductNew productNew = new ProductNew();
            productNew.setModelId(modelId);
            productNew.setBatchId(batch);
            productNew.setStatus(3);
            productNew.setCreateTime(System.currentTimeMillis());
            productNew.setDelFlag(0);
            productNew.setCache(1);
            ProductNew productNewId = productNewService.insert(productNew);

            PointProductBind pointProductBind = new PointProductBind();
            pointProductBind.setProductId(productNewId.getId());
            pointProductBind.setPointId(pointId);
            pointProductBindService.insert(pointProductBind);
        }
        return R.ok();
    }*/

    /*@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public R deleteProduct(Long pointId, Long producutId) {
        if(pointId == null){
            return R.fail("请输入点位");
        }
        if(producutId == null){
            return R.fail("请输入柜机类型");
        }

        ProductNew productNew = productNewService.queryByIdFromDB(producutId);
        if(ObjectUtils.isNotNull(productNew)){
            if(productNew.getCache() != null){
                productNewMapper.delete(new UpdateWrapper<ProductNew>().eq("id", producutId));
            }else{
                LambdaUpdateWrapper<File> wrapper = new LambdaUpdateWrapper<File>().eq(File::getType, File.TYPE_PRODUCT).eq(File::getBindId, producutId);
                fileMapper.delete(wrapper);
            }

            UpdateWrapper<PointProductBind> wrapper = new UpdateWrapper<>();
            wrapper.eq("product_id", producutId);
            wrapper.eq("point_id", pointId);
            pointProductBindMapper.delete(wrapper);
        }else{
            log.info("查无此产品：producutId={}",producutId);
            return R.fail("查无此产品，请检查");
        }

        return R.ok();
    }*/

    @Override
    public void updateMany(List<PointNew> pointNew){
        pointNewMapper.updateMany(pointNew);
    }

    @Override
    public R pointBindSerialNumber(PointQuery pointQuery) {
        PointNew pointNew = getById(pointQuery.getId());
        if (Objects.isNull(pointNew)) {
            return R.failMsg("未找到点位信息!");
        }
        if (ObjectUtil.isNotEmpty(pointQuery.getProductSerialNumberIdAndSetNoMap())) {
            pointQuery.getProductSerialNumberIdAndSetNoMap().forEach((k,v) -> {
                PointProductBind pointProductBind = pointProductBindMapper
                        .selectOne(new QueryWrapper<PointProductBind>()
                                .eq("product_id", k));

                if(ObjectUtils.isNotNull(pointProductBind)){
                    ProductNew productNew = productNewService.queryByIdFromDB(k);
                    R.fail("柜机 +【" + (productNew == null?"未知":productNew.getNo() )+ "】已绑定柜机");
                }
            });
        }



        if (ObjectUtil.isNotEmpty(pointQuery.getProductSerialNumberIdAndSetNoMap())) {
            pointQuery.getProductSerialNumberIdAndSetNoMap().forEach((k,v) -> {
                PointProductBind bind = new PointProductBind();
                bind.setPointId(pointQuery.getId());
                bind.setProductId(k);
                pointProductBindMapper.insert(bind);
            });
        }
        return R.ok();
    }

    @Override
    public R updateAuditStatus(PointAuditStatusQuery pointAuditStatusQuery) {
        if(Objects.isNull(pointAuditStatusQuery.getId()) || Objects.isNull(pointAuditStatusQuery.getAuditStatus())){
            return R.fail("参数不合法");
        }

        PointNew pointNew = this.getById(pointAuditStatusQuery.getId());
        if(Objects.isNull(pointNew)){
            return R.fail("未查询到相关点位");
        }
        PointNew pointNewUpdate = new PointNew();
        pointNewUpdate.setId(pointAuditStatusQuery.getId());
        pointNewUpdate.setAuditStatus(pointAuditStatusQuery.getAuditStatus());
        pointNewUpdate.setAuditRemarks(pointAuditStatusQuery.getAuditRemarks());
        this.update(pointNewUpdate);
        return R.ok();
    }
}
