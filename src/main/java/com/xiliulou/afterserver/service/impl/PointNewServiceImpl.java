package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.xiliulou.afterserver.constant.MqConstant;
import com.xiliulou.afterserver.constant.cache.WorkOrderConstant;
import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.ExportMaterialConfig;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.MaintenanceUserNotifyConfig;
import com.xiliulou.afterserver.entity.Material;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.PointNewAuditRecord;
import com.xiliulou.afterserver.entity.PointProductBind;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.Province;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.mq.notify.MqNotifyCommon;
import com.xiliulou.afterserver.entity.mq.notify.MqPointNewAuditNotify;
import com.xiliulou.afterserver.mapper.ExportMaterialConfigMapper;
import com.xiliulou.afterserver.mapper.MaterialTraceabilityMapper;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.mapper.SupplierMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.MaintenanceUserNotifyConfigService;
import com.xiliulou.afterserver.service.PointNewAuditRecordService;
import com.xiliulou.afterserver.service.PointNewService;
import com.xiliulou.afterserver.service.PointProductBindService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.ProvinceService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.vo.PointNewInfoVo;
import com.xiliulou.afterserver.web.query.CameraInfoQuery;
import com.xiliulou.afterserver.web.query.PointAuditStatusQuery;
import com.xiliulou.afterserver.web.query.PointQuery;
import com.xiliulou.afterserver.web.query.ProductInfoQuery;
import com.xiliulou.afterserver.web.vo.FileVo;
import com.xiliulou.afterserver.web.vo.MaterialCellVo;
import com.xiliulou.afterserver.web.vo.MaterialHistoryVo;
import com.xiliulou.afterserver.web.vo.PointNewMapStatisticsVo;
import com.xiliulou.afterserver.web.vo.PointNewPullVo;
import com.xiliulou.afterserver.web.vo.ProductNewDeliverVo;
import com.xiliulou.cache.redis.RedisService;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.mq.service.RocketMqService;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.xiliulou.afterserver.entity.ExportMaterialConfig.ATMEL;
import static com.xiliulou.afterserver.entity.ExportMaterialConfig.IMEL;

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
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private StorageConfig storageConfig;
    
    @Autowired
    private AliyunOssService aliyunOssService;
    
    @Autowired
    private PointNewAuditRecordService pointNewAuditRecordService;
    
    @Autowired
    private RocketMqService rocketMqService;
    
    @Autowired
    private MaintenanceUserNotifyConfigService maintenanceUserNotifyConfigService;
    
    @Autowired
    private MaterialTraceabilityMapper materialTraceabilityMapper;
    
    @Autowired
    private SupplierMapper supplierMapper;
    
    @Autowired
    private ExportMaterialConfigMapper exportMaterialConfigMapper;
    
    
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
        PointNew serviceWithHash = redisService.getWithHash(WorkOrderConstant.POINT_NEW + id, PointNew.class);
        if (Objects.nonNull(serviceWithHash)) {
            return serviceWithHash;
        }
        
        PointNew pointNew = this.getById(id);
        if (Objects.isNull(pointNew)) {
            pointNew = this.getById(id);
            return pointNew;
        }
        
        redisService.saveWithHash(WorkOrderConstant.POINT_NEW, pointNew);
        return pointNew;
    }
    
    
    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<PointNew> queryAllByLimit(int offset, int limit, String name, Integer cid, Integer status, Long customerId, Long startTime, Long endTime, Long createUid,
            String snNo, Integer productSeries, Integer auditStatus) {
        return this.pointNewMapper.queryAllByLimit(offset, limit, name, cid, status, customerId, startTime, endTime, createUid, snNo, productSeries, auditStatus);
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
    
    /**
     * 插入点位
     *
     * @param pointNew
     * @return
     */
    @Override
    public R saveAdminPointNew(PointNew pointNew) {
        R r = checkPropertes(pointNew);
        if (Objects.nonNull(r)) {
            return r;
        }
        if (Objects.nonNull(pointNew.getProductInfoList())) {
            Iterator<ProductInfoQuery> iterator = pointNew.getProductInfoList().iterator();
            while (iterator.hasNext()) {
                ProductInfoQuery productInfoQuery = iterator.next();
                if (Objects.isNull(productInfoQuery.getProductId()) || Objects.isNull(productInfoQuery.getNumber())) {
                    iterator.remove();
                }
            }
            String productInfo = JSON.toJSONString(pointNew.getProductInfoList());
            pointNew.setProductInfo(productInfo);
        }
        if (Objects.nonNull(pointNew.getCameraInfoList())) {
            Iterator<CameraInfoQuery> iterator = pointNew.getCameraInfoList().iterator();
            while (iterator.hasNext()) {
                CameraInfoQuery cameraInfoQuery = iterator.next();
                if (StringUtils.isBlank(cameraInfoQuery.getCameraSupplier()) && StringUtils.isBlank(cameraInfoQuery.getCameraSn()) && StringUtils.isBlank(
                        cameraInfoQuery.getCameraNumber())) {
                    iterator.remove();
                }
            }
            String cameraInfo = JSON.toJSONString(pointNew.getCameraInfoList());
            pointNew.setCameraInfo(cameraInfo);
        }
        PointNew pointNewOld = queryByName(pointNew.getName());
        if (Objects.nonNull(pointNewOld)) {
            return R.fail("该点位已存在");
        }
        if (Objects.isNull(pointNew.getInstallTime()) || Objects.isNull(pointNew.getWarrantyPeriod())) {
            pointNew.setWarrantyTime(null);
        }
        pointNew.setDelFlag(PointNew.DEL_NORMAL);
        pointNew.setCreateTime(System.currentTimeMillis());
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
        this.insert(pointNew);
        return R.ok();
    }
    
    public PointNew queryByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        return this.getBaseMapper().selectOne(new QueryWrapper<PointNew>().eq("name", name).eq("del_flag", 0));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R putAdminPointNew(PointNew pointNew) {
        PointNew queryById = pointNewMapper.queryById(pointNew.getId());
        if (Objects.isNull(queryById)) {
            return R.fail("请传入点位id");
        }
        
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
        // Integer row = this.update(pointNew);
        if (!this.updateById(pointNew)) {
            return R.fail("数据库错误");
        }
        
        if (pointNew.getProductIds().isEmpty()) {
            return R.fail("请传入点位id");
        }
        
        pointNew.getProductIds().forEach(item -> {
            
            PointProductBind oldPointProductBind = pointProductBindMapper.selectOne(new QueryWrapper<PointProductBind>().eq("product_id", item));
            
            if (ObjectUtils.isNotNull(oldPointProductBind)) {
                pointProductBindMapper.deleteById(oldPointProductBind.getId());
            }
            
            PointProductBind pointProductBind = new PointProductBind();
            pointProductBind.setPointId(pointNew.getId());
            pointProductBind.setProductId(item);
            pointProductBind.setPointType(PointProductBind.TYPE_POINT);
            pointProductBindService.insert(pointProductBind);
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
        if (Objects.nonNull(r)) {
            return r;
        }
        // 清除空的产品信息
        if (Objects.nonNull(pointNew.getProductInfoList())) {
            Iterator<ProductInfoQuery> iterator = pointNew.getProductInfoList().iterator();
            while (iterator.hasNext()) {
                ProductInfoQuery productInfoQuery = iterator.next();
                if (Objects.isNull(productInfoQuery.getProductId()) || Objects.isNull(productInfoQuery.getNumber())) {
                    iterator.remove();
                }
            }
            String productInfo = JSON.toJSONString(pointNew.getProductInfoList());
            pointNew.setProductInfo(productInfo);
        }
        // 清除空的摄像头信息
        if (Objects.nonNull(pointNew.getCameraInfoList())) {
            Iterator<CameraInfoQuery> iterator = pointNew.getCameraInfoList().iterator();
            while (iterator.hasNext()) {
                CameraInfoQuery cameraInfoQuery = iterator.next();
                if (StringUtils.isBlank(cameraInfoQuery.getCameraSupplier()) && StringUtils.isBlank(cameraInfoQuery.getCameraSn()) && StringUtils.isBlank(
                        cameraInfoQuery.getCameraNumber())) {
                    iterator.remove();
                }
            }
            String cameraInfo = JSON.toJSONString(pointNew.getCameraInfoList());
            pointNew.setCameraInfo(cameraInfo);
        }
        
        PointNew pointNewOld = queryByName(pointNew.getName());
        if (Objects.nonNull(pointNewOld) && !Objects.equals(pointNew.getId(), pointNewOld.getId())) {
            return R.fail("该点位已存在");
        }
        
        if (Objects.isNull(pointNew.getInstallTime()) || Objects.isNull(pointNew.getWarrantyPeriod())) {
            pointNew.setWarrantyTime(null);
        }
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
        
        int update = this.pointNewMapper.update(pointNew);
        if (update > 0) {
            return R.ok();
        }
        return R.fail("修改失败");
    }
    
    @Override
    public R pointInfo(Long pid) {
        LambdaQueryWrapper<PointNew> queryWrapper = new LambdaQueryWrapper<PointNew>().eq(PointNew::getId, pid).eq(PointNew::getDelFlag, PointNew.DEL_NORMAL);
        PointNew pointNew = this.pointNewMapper.selectOne(queryWrapper);
        
        if (Objects.isNull(pointNew)) {
            return R.fail("未查询到相关数据");
        }
        
        PointNewInfoVo pointNewInfoVo = new PointNewInfoVo();
        
        if (Objects.nonNull(pointNew.getCityId())) {
            City byId = cityService.getById(pointNew.getCityId());
            pointNew.setCityName(byId.getName());
            Province province = provinceService.queryByIdFromDB(byId.getPid());
            pointNew.setProvince(province.getName());
        }
        
        if (Objects.nonNull(pointNew.getCustomerId())) {
            Customer byId = customerService.getById(pointNew.getCustomerId());
            if (Objects.nonNull(byId)) {
                pointNew.setCustomerName(byId.getName());
            }
        }
        
        pointNewInfoVo.setPointNew(pointNew);
        List<File> pointFileList = fileService.queryByPointId(pid);
        pointNewInfoVo.setPointFileList(pointFileList);
        
        List<PointProductBind> pointProductBindList = pointProductBindService.queryByPointNewIdAndBindType(pid, PointProductBind.TYPE_POINT);
        ArrayList<ProductNew> productNews = new ArrayList<>();
        Map<String, Long> map = new HashMap<>();
        List<Map> productTypeAndNumList = new ArrayList<>();
        
        if (Objects.nonNull(pointProductBindList)) {
            pointProductBindList.forEach(item -> {
                ProductNew productNew = productNewService.queryByIdFromDB(item.getProductId());
                if (Objects.isNull(productNew)) {
                    return;
                }
                
                Product product = productService.getBaseMapper().selectById(productNew.getModelId());
                if (Objects.nonNull(product)) {
                    productNew.setModelName(product.getName());
                }
                
                Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
                if (Objects.nonNull(batch)) {
                    productNew.setBatchName(batch.getBatchNo());
                }
                
                List<File> productFileList = fileService.queryByProductNewId(productNew.getId(), File.FILE_TYPE_PRODUCT_PRODUCT);
                productNew.setFileList(productFileList);
                productNews.add(productNew);
                
                // productTypeAndNum
                map.put(product.getName(), map.containsKey(product.getName()) ? map.get(product.getName()) + 1 : 1);
            });
            
            pointNewInfoVo.setProductNew(productNews);
            
            for (Map.Entry entry : map.entrySet()) {
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
    public Integer countPoint(String name, Integer cid, Integer status, Long customerId, Long startTime, Long endTime, Long createUid, String snNo, Integer productSeries,
            Integer auditStatus) {
        return this.pointNewMapper.countPoint(name, cid, status, customerId, startTime, endTime, createUid, snNo, productSeries, auditStatus);
    }
    
    @Override
    public List<PointNew> queryAllByLimitExcel(String name, Integer cid, Integer status, Long customerId, Long startTime, Long endTime, Long createUid, String snNo,
            Integer productSeries, Integer auditStatus) {
        List<PointNew> pointNews = this.pointNewMapper.queryAllByLimitExcel(name, cid, status, customerId, startTime, endTime, createUid, snNo, productSeries, auditStatus);
        return pointNews;
    }
    
    @Override
    public R putAdminPointNewCreateUser(Long id, Long createUid) {
        if (Objects.isNull(id) || Objects.isNull(createUid)) {
            return R.fail("参数非法，请检查");
        }
        
        User user = userService.getUserById(createUid);
        if (Objects.isNull(user)) {
            return R.fail("没有查询到该用户");
        }
        
        Integer len = pointNewMapper.putAdminPointNewCreateUser(id, createUid);
        
        if (len != null && len > 0) {
            return R.ok();
        }
        
        return R.fail("修改失败");
    }
    
    public R checkPropertes(PointNew pointNew) {
        if (Objects.isNull(pointNew.getAuditStatus())) {
            return R.fail("请填写审核状态");
        }
        if (Objects.isNull(pointNew.getProductSeries())) {
            return R.fail("请填写产品系列");
        }
        if (Objects.isNull(pointNew.getCityId())) {
            return R.fail("请填写城市信息");
        }
        if (Objects.isNull(pointNew.getCustomerId())) {
            return R.fail("请填写客户信息");
        }
        if (StringUtils.isBlank(pointNew.getName())) {
            return R.fail("请填写点位名称");
        }
        if (Objects.isNull(pointNew.getStatus())) {
            return R.fail("请填写点位状态");
        }
        if (Objects.isNull(pointNew.getInstallType())) {
            return R.fail("请填写安装类型");
        }
        if (Objects.isNull(pointNew.getAddress())) {
            return R.fail("请填写详细地址");
        }
        if (Objects.isNull(pointNew.getInstallTime())) {
            return R.fail("请填写安装时间");
        }
        if (Objects.isNull(pointNew.getWarrantyPeriod())) {
            return R.fail("请填写质保有效期");
        }
        if (Objects.isNull(pointNew.getWarrantyTime())) {
            return R.fail("请填写质保结束时间");
        }
        if (Objects.isNull(pointNew.getIsAcceptance())) {
            return R.fail("请确认是否验收");
        }
        if (CollectionUtils.isEmpty(pointNew.getProductInfoList())) {
            return R.fail("请填写产品信息");
        }
        if (Objects.isNull(pointNew.getCoordX()) || Objects.isNull(pointNew.getCoordY())) {
            return R.fail("详细地址经纬度为空，请选择详细地址");
        }
        return null;
    }
    
    @Override
    public void updatePastWarrantyStatus() {
        pointNewMapper.updatePastWarrantyStatus(System.currentTimeMillis());
    }
    
    @Override
    public List<PointNewPullVo> queryPointNewPull(String name) {
        return pointNewMapper.queryPointNewPull(name);
    }
    
    @Override
    public R pointNewMapStatistics(List<BigDecimal> coordXList, List<BigDecimal> coordYList, Long cityId, Long provinceId, Integer productSeries) {
        if (CollectionUtils.isEmpty(coordXList) || coordXList.size() != 2) {
            return R.ok("纬度范围不合法,请传入正确范围");
        }
        if (CollectionUtils.isEmpty(coordYList) || coordYList.size() != 2) {
            return R.ok("经度范围不合法,请传入正确范围");
        }
        
        List<PointNewMapStatisticsVo> pointNewMapStatisticsVo = pointNewMapper.mapStatistics(coordXList.get(0), coordXList.get(1), coordYList.get(0), coordYList.get(1), cityId,
                provinceId, productSeries);
        
        return R.ok(pointNewMapStatisticsVo);
    }
    
    @Override
    public R pointNewMapProvinceCount() {
        return R.ok(pointNewMapper.pointNewMapProvinceCount());
    }
    
    @Override
    public R pointNewMapCityCount(Long pid) {
        return R.ok(pointNewMapper.pointNewMapCityCount(pid));
    }
    
    @Override
    public R batchUpdateAuditStatus(PointAuditStatusQuery pointAuditStatusQuery) {
        if (CollectionUtils.isEmpty(pointAuditStatusQuery.getIds()) || Objects.isNull(pointAuditStatusQuery.getAuditStatus())) {
            return R.fail("参数不合法");
        }
        
        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)) {
            return R.fail("未查询到用户信息");
        }
        
        User user = userService.getUserById(uid);
        if (Objects.isNull(user)) {
            return R.fail("未查询到用户信息");
        }
        
        pointAuditStatusQuery.setAuditUid(uid);
        pointAuditStatusQuery.setAuditUserName(user.getUserName());
        pointAuditStatusQuery.setAuditTime(System.currentTimeMillis());
        pointNewMapper.batchUpdateAuditStatus(pointAuditStatusQuery);
        
        pointAuditStatusQuery.getIds().parallelStream().forEach(id -> {
            PointNew pointNew = this.queryByIdFromDB(id);
            if (Objects.isNull(pointNew)) {
                return;
            }
            // 生成操作记录
            generateAuditRecord(id, pointAuditStatusQuery.getAuditStatus(), pointAuditStatusQuery.getAuditRemarks(), user);
            
            // 发送Mq消息
            PointNew pointNewMq = new PointNew();
            pointNewMq.setName(pointNew.getName());
            pointNewMq.setAuditStatus(pointAuditStatusQuery.getAuditStatus());
            pointNewMq.setAuditRemarks(pointAuditStatusQuery.getAuditRemarks());
            pointNewMq.setAuditUserName(pointAuditStatusQuery.getAuditUserName());
            this.sendAuditStatusNotifyMq(pointNewMq);
        });
        
        return R.ok();
    }
    
    
    @Override
    public R queryFiles(Long pid) {
        List<File> pointFileList = fileService.queryByPointId(pid);
        if (CollectionUtils.isEmpty(pointFileList)) {
            return R.ok();
        }
        
        List<FileVo> fileVos = pointFileList.parallelStream().map(item -> {
            String url = null;
            try {
                long expiration = Optional.ofNullable(storageConfig.getExpiration()).orElse(1000L * 60L * 3L) + System.currentTimeMillis();
                url = aliyunOssService.getOssFileUrl(storageConfig.getBucketName(), storageConfig.getDir() + item.getFileName(), expiration);
            } catch (Exception e) {
                log.error("aliyunOss down File Error!", e);
            }
            
            FileVo vo = new FileVo();
            vo.setId(item.getId());
            vo.setUrl(url);
            vo.setFileType(item.getFileType());
            return vo;
        }).collect(Collectors.toList());
        
        return R.ok(fileVos);
    }
    
    @Override
    public void updateMany(List<PointNew> pointNew) {
        pointNewMapper.updateMany(pointNew);
    }
    
    @Override
    public R pointBindSerialNumber(PointQuery pointQuery) {
        PointNew pointNew = getById(pointQuery.getId());
        if (Objects.isNull(pointNew)) {
            return R.failMsg("未找到点位信息!");
        }
        
        if (ObjectUtil.isNotEmpty(pointQuery.getProductSerialNumberIdAndSetNoMap())) {
            Set<Map.Entry<Long, Integer>> entrySet = pointQuery.getProductSerialNumberIdAndSetNoMap().entrySet();
            for (Map.Entry<Long, Integer> entry : entrySet) {
                PointProductBind pointProductBind = pointProductBindMapper.selectOne(new QueryWrapper<PointProductBind>().eq("product_id", entry.getKey()));
                
                if (ObjectUtils.isNotNull(pointProductBind)) {
                    pointProductBindService.deleteById(pointProductBind.getId());
                }
            }
        }
        
        if (ObjectUtil.isNotEmpty(pointQuery.getProductSerialNumberIdAndSetNoMap())) {
            pointQuery.getProductSerialNumberIdAndSetNoMap().forEach((k, v) -> {
                PointProductBind bind = new PointProductBind();
                bind.setPointId(pointQuery.getId());
                bind.setProductId(k);
                bind.setPointType(PointProductBind.TYPE_POINT);
                pointProductBindMapper.insert(bind);
            });
        }
        return R.ok();
    }
    
    @Override
    public R updateAuditStatus(PointAuditStatusQuery pointAuditStatusQuery) {
        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)) {
            return R.fail("未查询到用户信息");
        }
        
        User user = userService.getUserById(uid);
        if (Objects.isNull(user)) {
            return R.fail("未查询到用户信息");
        }
        
        if (Objects.isNull(pointAuditStatusQuery.getId()) || Objects.isNull(pointAuditStatusQuery.getAuditStatus())) {
            return R.fail("参数不合法");
        }
        
        PointNew pointNew = this.getById(pointAuditStatusQuery.getId());
        if (Objects.isNull(pointNew)) {
            return R.fail("未查询到相关点位");
        }
        
        PointNew pointNewUpdate = new PointNew();
        pointNewUpdate.setId(pointAuditStatusQuery.getId());
        pointNewUpdate.setAuditUid(uid);
        pointNewUpdate.setAuditUserName(user.getUserName());
        pointNewUpdate.setAuditTime(System.currentTimeMillis());
        pointNewUpdate.setAuditStatus(pointAuditStatusQuery.getAuditStatus());
        pointNewUpdate.setAuditRemarks(pointAuditStatusQuery.getAuditRemarks());
        pointNewMapper.updateAuditStatus(pointNewUpdate);
        
        // 生成记录
        generateAuditRecord(pointNew.getId(), pointNewUpdate.getAuditStatus(), pointNewUpdate.getAuditRemarks(), user);
        // 发送Mq消息
        if (Objects.equals(pointNewUpdate.getAuditStatus(), PointNew.AUDIT_STATUS_FAIL)) {
            pointNewUpdate.setName(pointNew.getName());
            this.sendAuditStatusNotifyMq(pointNewUpdate);
        }
        return R.ok();
    }
    
    private void sendAuditStatusNotifyMq(PointNew pointNewUpdate) {
        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = maintenanceUserNotifyConfigService.queryByPermissions(MaintenanceUserNotifyConfig.TYPE_REVIEW, null);
        if (Objects.isNull(maintenanceUserNotifyConfig) || org.springframework.util.StringUtils.isEmpty(maintenanceUserNotifyConfig.getPhones())) {
            return;
        }
        List<String> phones = JsonUtil.fromJsonArray(maintenanceUserNotifyConfig.getPhones(), String.class);
        
        if (CollectionUtils.isEmpty(phones)) {
            return;
        }
        
        if (Objects.equals(maintenanceUserNotifyConfig.getPermissions() & MaintenanceUserNotifyConfig.P_AUDIT_FAILED, MaintenanceUserNotifyConfig.P_AUDIT_FAILED)) {
            phones.forEach(p -> {
                MqNotifyCommon<MqPointNewAuditNotify> query = new MqNotifyCommon<>();
                query.setType(MqNotifyCommon.TYPE_AFTER_SALES_POINT_AUDIT);
                query.setTime(System.currentTimeMillis());
                query.setPhone(p);
                
                MqPointNewAuditNotify mqPointNewAuditNotify = new MqPointNewAuditNotify();
                mqPointNewAuditNotify.setPointName(pointNewUpdate.getName());
                mqPointNewAuditNotify.setAuditUserName(pointNewUpdate.getAuditUserName());
                mqPointNewAuditNotify.setAuditRemark(pointNewUpdate.getAuditRemarks());
                mqPointNewAuditNotify.setAuditResult("未通过");
                query.setData(mqPointNewAuditNotify);
                
                Pair<Boolean, String> result = rocketMqService.sendSyncMsg(MqConstant.TOPIC_MAINTENANCE_NOTIFY, JsonUtil.toJson(query), MqConstant.TAG_AFTER_SALES, "", 0);
                if (!result.getLeft()) {
                    log.error("SEND WORKORDER AUDIT MQ ERROR! pointName={}, msg={}", pointNewUpdate.getName(), result.getRight());
                }
            });
        }
    }
    
    private void generateAuditRecord(Long id, Integer status, String remark, User user) {
        PointNewAuditRecord pointNewAuditRecord = new PointNewAuditRecord();
        pointNewAuditRecord.setUid(user.getId());
        pointNewAuditRecord.setUserName(user.getUserName());
        pointNewAuditRecord.setPointId(id);
        pointNewAuditRecord.setAuditStatus(status);
        pointNewAuditRecord.setCreateTime(System.currentTimeMillis());
        pointNewAuditRecord.setRemark(remark);
        pointNewAuditRecordService.insert(pointNewAuditRecord);
    }
    
    // 备货管理
    @Override
    public R productNewDeliverList(Long offset, Long size, String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime) {
        List<ProductNewDeliverVo> productNewDeliverVos = pointNewMapper.productNewDeliverList(offset, size, batchNo, sn, deviceName, cabinetSn, tenantName, startTime, endTime);
        if (CollectionUtils.isEmpty(productNewDeliverVos)) {
            return R.ok(new ArrayList<ProductNewDeliverVo>());
        }
        for (int i = 0; i < productNewDeliverVos.size(); i++) {
            // 打包时间同压测成功之后的结束时间，因此前端只需要获取压测结束时间
            if (!ProductNew.TEST_RESULT_SUCCESS.equals(productNewDeliverVos.get(i).getTestResult())) {
                productNewDeliverVos.get(i).setTestEndTime(null);
            }
        }
        return R.ok(productNewDeliverVos);
    }
    
    @Override
    public R productNewDeliverCount(String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime) {
        return R.ok(pointNewMapper.productNewDeliverCount(batchNo, sn, deviceName, cabinetSn, tenantName, startTime, endTime));
    }
    
    @Override
    public R productNewDeliverExportExcel(String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime,
            HttpServletResponse response) {
        List<ProductNewDeliverVo> productNewDeliverVos = pointNewMapper.productNewDeliverExport(batchNo, sn, deviceName, cabinetSn, tenantName, startTime, endTime);
        if (CollectionUtils.isEmpty(productNewDeliverVos)) {
            return R.ok();
        }
        String fileName = "备货.xlsx";
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        ArrayList<List<Object>> resultList = new ArrayList<>();
        if (productNewDeliverVos.size() > 1000) {
            List<Object> list = new ArrayList<>();
            list.add("导出数据最大不能超过1000条");
            
            resultList.add(list);
        } else {
            for (int i = 0; i < productNewDeliverVos.size(); i++) {
                // 打包时间同压测成功之后的结束时间，因此前端只需要获取压测结束时间
                if (!ProductNew.TEST_RESULT_SUCCESS.equals(productNewDeliverVos.get(i).getTestResult())) {
                    productNewDeliverVos.get(i).setTestEndTime(null);
                }
            }
            String[] header = {"批次号", "资产编码", "deviceName", "柜机编码", "productKey", "打包时间", "运营商", "发货时间", "创建时间", "更新时间"};
            
            List<Object> headList = new ArrayList<Object>();
            for (int i = 0; i < header.length; i++) {
                headList.add(header[i]);
            }
            resultList.add(headList);
            productNewDeliverVos.parallelStream().forEachOrdered(item -> {
                try {
                    if (Objects.nonNull(item)) {
                        List<Object> list = new ArrayList<>();
                        list.add(item.getBatchNo());
                        list.add(item.getNo());
                        list.add(item.getDeviceName());
                        list.add(item.getCabinetSn());
                        list.add(item.getProductKey());
                        list.add(Objects.isNull(item.getTestEndTime()) ? "" : DateUtils.stampToTime(item.getTestEndTime().toString()));
                        list.add(item.getTenantName());
                        list.add(Objects.isNull(item.getDeliverTime()) ? "" : DateUtils.stampToTime(item.getDeliverTime().toString()));
                        list.add(Objects.isNull(item.getCreateTime()) ? "" : DateUtils.stampToTime(item.getCreateTime().toString()));
                        list.add(Objects.isNull(item.getUpdateTime()) ? "" : DateUtils.stampToTime(item.getUpdateTime().toString()));
                        resultList.add(list);
                    }
                    
                } catch (Exception e) {
                    log.error("DeliverExcel Error", e);
                }
            });
        }
        
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write1(resultList, sheet, table).finish();
            
        } catch (IOException e) {
            throw new NullPointerException("导出报表失败！请联系管理员处理！");
        }
        return R.ok();
    }
    
    @Override
    public R productNewDeliverMaterialHistoryExportExcel(Long[] ids) {
        ArrayList<Map> materialHistoryVos = new ArrayList<>();
        List<ExportMaterialConfig> exportMaterialConfigs = exportMaterialConfigMapper.selectPage(new ExportMaterialConfig(), 0L, 20L);
        if (CollectionUtils.isEmpty(exportMaterialConfigs)) {
            return R.failMsg("请先配置物料导出顺序");
        }
        
        List<ProductNewDeliverVo> productNewDeliverVos = pointNewMapper.selectDeliverBatchIds(Arrays.asList(ids));
        
        if (CollectionUtils.isEmpty(productNewDeliverVos)) {
            return R.ok();
        }
        if (productNewDeliverVos.size() > 500) {
            return R.failMsg("导出数据最大不能超过500条");
        }
        
        // 获取 productNewDeliverVos 的设备编码
        List<String> collect = productNewDeliverVos.stream().map(ProductNewDeliverVo::getNo).collect(Collectors.toList());
        
        List<Material> materialByIds = materialTraceabilityMapper.selectListByNos(collect);
        List<Long> collectSupplierIds = productNewDeliverVos.stream().map(ProductNewDeliverVo::getSupplierId).collect(Collectors.toList());
        List<Supplier> supplierList = supplierMapper.selectList(new LambdaQueryWrapper<Supplier>().in(Supplier::getId, collectSupplierIds));
        Map<Long, Supplier> longSupplierMap = supplierList.stream().collect(Collectors.toMap(Supplier::getId, Function.identity(), (oldValue, newValue) -> newValue));
        // 根据物料编号分组
        Map<String, List<Material>> materialGroup = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialByIds)) {
            materialGroup = materialByIds.stream().collect(Collectors.groupingBy(Material::getSn));
        }
        
        for (ProductNewDeliverVo temp : productNewDeliverVos) {
            MaterialHistoryVo materialHistoryVo = new MaterialHistoryVo();
            materialHistoryVo.setCabinetSn(temp.getCabinetSn());
            materialHistoryVo.setNo(temp.getNo());
            materialHistoryVo.setDeliverTime(temp.getDeliverTime());
            // 工厂名称
            if (Objects.nonNull(temp.getSupplierId())) {
                materialHistoryVo.setSupplierName(longSupplierMap.get(temp.getSupplierId()).getName());
                materialHistoryVo.setTenantName(longSupplierMap.get(temp.getSupplierId()).getName());
            }
            materialHistoryVo.setProductionTime(temp.getCreateTime());
            
            // materialHistoryVo 转 Map
            String jsonBean = new Gson().toJson(materialHistoryVo);
            LinkedHashMap materialHistoryMap = new Gson().fromJson(jsonBean, LinkedHashMap.class);
            
            // 生成列表
            fillMaterialHistory(temp.getNo(), materialGroup, materialHistoryMap, exportMaterialConfigs);
            materialHistoryVos.add(materialHistoryMap);
        }
        
        return R.ok(materialHistoryVos);
    }
    
    @Override
    public R productNewDeliverMaterialPanelExportExcel(Long[] ids) {
        List<ExportMaterialConfig> exportMaterialConfigs = exportMaterialConfigMapper.selectPage(new ExportMaterialConfig(), 0L, 20L);
        if (CollectionUtils.isEmpty(exportMaterialConfigs)) {
            return R.failMsg("请先配置物料导出顺序");
        }
        List<ProductNewDeliverVo> productNewDeliverVos = pointNewMapper.selectDeliverBatchIds(Arrays.asList(ids));
        
        if (CollectionUtils.isEmpty(productNewDeliverVos)) {
            return R.ok();
        }
        if (productNewDeliverVos.size() > 500) {
            return R.failMsg("导出数据最大不能超过500条");
        }
        
        // 获取 productNewDeliverVos 的设备编码
        List<String> collect = productNewDeliverVos.stream().map(ProductNewDeliverVo::getNo).collect(Collectors.toList());
        
        List<Material> materialByIds = materialTraceabilityMapper.selectListByNos(collect);
        List<Long> collectSupplierIds = productNewDeliverVos.stream().map(ProductNewDeliverVo::getSupplierId).collect(Collectors.toList());
        List<Supplier> supplierList = supplierMapper.selectList(new LambdaQueryWrapper<Supplier>().in(Supplier::getId, collectSupplierIds));
        Map<Long, Supplier> longSupplierMap = supplierList.stream().collect(Collectors.toMap(Supplier::getId, Function.identity(), (oldValue, newValue) -> newValue));
        
        // 根据物料编号分组
        Map<String, List<Material>> materialGroup = new HashMap<>();
        if (CollectionUtils.isNotEmpty(materialByIds)) {
            materialGroup = materialByIds.stream().collect(Collectors.groupingBy(Material::getSn));
        }
        //        List<MaterialHistoryVo> productNewDeliverVoResult = new ArrayList<>();
        ArrayList<Map> productNewDeliverVoResult = new ArrayList<>();
        
        for (ProductNewDeliverVo temp : productNewDeliverVos) {
            MaterialHistoryVo materialHistoryVo = new MaterialHistoryVo();
            materialHistoryVo.setCabinetSn(temp.getCabinetSn());
            materialHistoryVo.setNo(temp.getNo());
            materialHistoryVo.setDeliverTime(temp.getDeliverTime());
            materialHistoryVo.setTenantName(temp.getTenantName());
            
            // 工厂名称
            if (Objects.nonNull(temp.getSupplierId())) {
                materialHistoryVo.setSupplierName(longSupplierMap.get(temp.getSupplierId()).getName());
            }
            
            ExportMaterialConfig exportMaterialConfig = exportMaterialConfigs.stream().filter(e -> {
                return Objects.equals(e.getAssociationStatus(), ATMEL);
            }).findFirst().get();
            LinkedHashMap materialHistoryMap = new LinkedHashMap<>();
            if (CollectionUtils.isNotEmpty(materialGroup)) {
                // 生成列表
                List<Material> y5030011 = materialGroup.get(exportMaterialConfig.getPn());
                if (CollectionUtils.isNotEmpty(y5030011)) {
                    List<Material> materials = y5030011.stream().filter(x -> Objects.equals(x.getProductNo(), temp.getNo())).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(materials)) {
                        materialHistoryVo.setAtmelID(materials.get(0).getAtmelID());
                        materialHistoryVo.setProductionTime(materials.get(0).getTestTime());
                    }
                }
                
                // materialHistoryVo 转 Map
                String jsonBean = new Gson().toJson(materialHistoryVo);
                materialHistoryMap = new Gson().fromJson(jsonBean, LinkedHashMap.class);
                
                materialHistoryMap.put(exportMaterialConfig.getPn(),
                        generateLists(temp.getNo(), exportMaterialConfig.getPn(), exportMaterialConfig.getMaterialAlias(), materialGroup));
            } else {
                materialHistoryVo.setAtmelID("");
                materialHistoryMap.put(exportMaterialConfig.getMaterialAlias(), "");
            }
            productNewDeliverVoResult.add(materialHistoryMap);
        }
        ;
        
        return R.ok(productNewDeliverVoResult);
    }
    
    //
    private void fillMaterialHistory(String no, Map<String, List<Material>> materialGroup, Map materialHistoryVo, List<ExportMaterialConfig> exportMaterialConfigs) {
        
        for (ExportMaterialConfig exportMaterialConfig : exportMaterialConfigs) {
            String pn = exportMaterialConfig.getPn();
            List<MaterialCellVo> tempMaterialCellVoList;
            tempMaterialCellVoList = generateLists(no, pn, exportMaterialConfig.getMaterialAlias(), materialGroup);
            
            materialHistoryVo.put(pn, tempMaterialCellVoList);
            if (associationStatusCheck(materialGroup, exportMaterialConfig, ATMEL, pn)) {
                List<Material> materials = materialGroup.get(pn).stream().filter(x -> Objects.equals(x.getProductNo(), no)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(materials)) {
                    materialHistoryVo.put("AtmelID", materials.get(0).getAtmelID());// setAtmelID(materials.get(0).getAtmelID());
                }
            } else if (associationStatusCheck(materialGroup, exportMaterialConfig, IMEL, pn)) {
                List<Material> materials = materialGroup.get(pn).stream().filter(x -> Objects.equals(x.getProductNo(), no)).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(materials)) {
                    materialHistoryVo.put("IMEI", materialGroup.get(pn).get(0).getImei()); //setImei(materialGroup.get(pn).get(0).getImei());
                }
            } else {
                materialHistoryVo.put("IMEI", "");
                materialHistoryVo.put("AtmelID", "");
            }
        }
        
        //        if (CollectionUtils.isNotEmpty(materialGroup)) {
        //            List<MaterialCellVo> y8030010 = generateLists(no, "Y8030010", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //            List<MaterialCellVo> y8030017 = generateLists(no, "Y8030017", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //            List<MaterialCellVo> y5030015 = generateLists(no, "Y5030015", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //            List<MaterialCellVo> y5030011 = generateLists(no, "Y5030011", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //            List<MaterialCellVo> y5030010 = generateLists(no, "Y5030010", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //            List<MaterialCellVo> y5000301 = generateLists(no, "Y5000301", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //            List<MaterialCellVo> y5030012 = generateLists(no, "Y5030012", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //            List<MaterialCellVo> y5000322 = generateLists(no, "Y5000322", exportMaterialConfig.getMaterialAlias(), materialGroup);
        //
        //            materialHistoryVo.setTouchPanel(y8030010);
        //            materialHistoryVo.setLinuxBoard(y8030017);
        //            materialHistoryVo.setSixInOne(y5030015);
        //            if (CollectionUtils.isNotEmpty(materialGroup.get("Y5030011"))) {
        //                List<Material> materials = materialGroup.get("Y5030011").stream().filter(x -> Objects.equals(x.getProductNo(), no)).collect(Collectors.toList());
        //                if (CollectionUtils.isNotEmpty(materials)) {
        //                    materialHistoryVo.setAtmelID(materials.get(0).getAtmelID());
        //                }
        //            }
        //            materialHistoryVo.setConnectorBoard(y5030011);
        //            materialHistoryVo.setCommunicationBoard(y5030010);
        //            materialHistoryVo.setACCharger(y5000301);
        //            materialHistoryVo.setDCDCBoard(y5030012);
        //            materialHistoryVo.setModule4G(y5000322);
        //            if (CollectionUtils.isNotEmpty(materialGroup.get("Y5000322"))) {
        //                List<Material> materials = materialGroup.get("Y5000322").stream().filter(x -> Objects.equals(x.getProductNo(), no)).collect(Collectors.toList());
        //                if (CollectionUtils.isNotEmpty(materials)) {
        //                    materialHistoryVo.setImei(materialGroup.get("Y5000322").get(0).getImei());
        //                }
        //            }
        //            return;
        //        }
        //
        //        materialHistoryVo.setTouchPanel(getMaterialCellVos("Y8030010"));
        //        materialHistoryVo.setLinuxBoard(getMaterialCellVos("Y8030017"));
        //        materialHistoryVo.setSixInOne(getMaterialCellVos("Y5030015"));
        //        materialHistoryVo.setAtmelID("");
        //        materialHistoryVo.setConnectorBoard(getMaterialCellVos("Y5030011"));
        //        materialHistoryVo.setCommunicationBoard(getMaterialCellVos("Y5030010"));
        //        materialHistoryVo.setACCharger(getMaterialCellVos("Y5030010"));
        //        materialHistoryVo.setDCDCBoard(getMaterialCellVos("Y5030012"));
        //        materialHistoryVo.setModule4G(getMaterialCellVos("Y5000322"));
        //        materialHistoryVo.setImei("");
        
    }
    
    private static boolean associationStatusCheck(Map<String, List<Material>> materialGroup, ExportMaterialConfig exportMaterialConfig, Integer associationStatus, String pn) {
        return CollectionUtils.isNotEmpty(materialGroup) && Objects.equals(exportMaterialConfig.getAssociationStatus(), associationStatus) && CollectionUtils.isNotEmpty(
                materialGroup.get(pn));
    }
    
    private static List<MaterialCellVo> getMaterialCellVos(String materialAlias) {
        List<MaterialCellVo> defaultList = new ArrayList<>();
        MaterialCellVo materialCellVo = new MaterialCellVo();
        materialCellVo.setMaterialAlias(materialAlias);
        materialCellVo.setSn("");
        materialCellVo.setName("");
        defaultList.add(materialCellVo);
        return defaultList;
    }
    
    private List<MaterialCellVo> generateLists(String no, String sn, String materialAlias, Map<String, List<Material>> materialGroup) {
        List<MaterialCellVo> materialCellVos = new ArrayList<>();
        // 物料分组或物料列表为空时 设置默认值
        if (CollectionUtils.isEmpty(materialGroup) || CollectionUtils.isEmpty(materialGroup.get(sn))) {
            return getMaterialCellVos(materialAlias);
        }
        materialGroup.get(sn).forEach(tp -> {
            if (Objects.equals(no, tp.getProductNo())) {
                MaterialCellVo materialCellVo = new MaterialCellVo();
                materialCellVo.setMaterialAlias(materialAlias);
                materialCellVo.setSn(tp.getMaterialSn());
                materialCellVo.setName(tp.getName());
                materialCellVos.add(materialCellVo);
            }
        });
        if (CollectionUtils.isEmpty(materialCellVos)) {
            return getMaterialCellVos(sn);
        }
        return materialCellVos;
    }
    
    @Override
    public R printClientInfo(Long pid) {
        LambdaQueryWrapper<PointNew> queryWrapper = new LambdaQueryWrapper<PointNew>().eq(PointNew::getId, pid).eq(PointNew::getDelFlag, PointNew.DEL_NORMAL);
        PointNew pointNew = this.pointNewMapper.selectOne(queryWrapper);
        if (Objects.isNull(pointNew)) {
            return R.failMsg("该点位不存在");
        }
        PointNew pointNewResult = new PointNew();
        if (Objects.nonNull(pointNew.getCustomerId())) {
            Customer byId = customerService.getById(pointNew.getCustomerId());
            if (Objects.nonNull(byId)) {
                pointNewResult.setCustomerName(byId.getName());
            }
        }
        
        pointNewResult.setProductInfo(pointNew.getProductInfo());
        
        List<Product> productAll = productService.list();
        Map<Long, Product> productAllMap = productAll.stream().collect(Collectors.toMap(Product::getId, product -> product));
        
        if (Objects.nonNull(pointNew.getProductInfo())) {
            List<ProductInfoQuery> productInfo = JSON.parseArray(pointNew.getProductInfo(), ProductInfoQuery.class);
            for (ProductInfoQuery pro : productInfo) {
                if (Objects.isNull(pro)) {
                    continue;
                }
                Product product = productAllMap.get(pro.getProductId());
                if (Objects.nonNull(product)) {
                    pro.setProductName(product.getName());
                }
            }
            pointNewResult.setProductInfoList(productInfo);
        }
        return R.ok(pointNewResult);
    }
    
}
