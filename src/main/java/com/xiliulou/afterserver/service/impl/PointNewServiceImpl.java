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
import com.xiliulou.afterserver.constant.MqConstant;
import com.xiliulou.afterserver.constant.cache.WorkOrderConstant;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.entity.mq.notify.MqNotifyCommon;
import com.xiliulou.afterserver.entity.mq.notify.MqPointNewAuditNotify;
import com.xiliulou.afterserver.mapper.*;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.vo.PointNewInfoVo;
import com.xiliulou.afterserver.web.query.*;
import com.xiliulou.afterserver.web.vo.FileVo;
import com.xiliulou.afterserver.web.vo.PointNewMapStatisticsVo;
import com.xiliulou.afterserver.web.vo.PointNewPullVo;
import com.xiliulou.afterserver.web.vo.ProductNewDeliverVo;
import com.xiliulou.cache.redis.RedisService;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.mq.service.RocketMqService;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
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
        
        PointNew pointNew= this.getById(id);
        if (Objects.isNull(pointNew)) {
            pointNew=this.getById(id);
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
    
    /**
     * 插入点位
     *
     * @param pointNew
     * @return
     */
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
        PointNew pointNewOld = queryByName(pointNew.getName());
        if(Objects.nonNull(pointNewOld)){
            return R.fail("该点位已存在");
        }
        if(Objects.isNull(pointNew.getInstallTime()) || Objects.isNull(pointNew.getWarrantyPeriod())){
            pointNew.setWarrantyTime(null);
        }
        pointNew.setDelFlag(PointNew.DEL_NORMAL);
        pointNew.setCreateTime(System.currentTimeMillis());
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
        this.insert(pointNew);
        return R.ok();
    }

    public PointNew queryByName(String name){
        if(StringUtils.isBlank(name)) {return null;}
        return this.getBaseMapper().selectOne(new QueryWrapper<PointNew>().eq("name", name).eq("del_flag", 0));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R putAdminPointNew(PointNew pointNew) {
        PointNew queryById = pointNewMapper.queryById(pointNew.getId());
        if (Objects.isNull(queryById)){
            return R.fail("请传入点位id");
        }

        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
        //Integer row = this.update(pointNew);
        if (!this.updateById(pointNew)) {
            return R.fail("数据库错误");
        }

        if (pointNew.getProductIds().isEmpty()) {
            return R.fail("请传入点位id");
        }

        pointNew.getProductIds().forEach(item -> {

            PointProductBind oldPointProductBind = pointProductBindMapper
                    .selectOne(new QueryWrapper<PointProductBind>()
                            .eq("product_id", item));

            if(ObjectUtils.isNotNull(oldPointProductBind)){
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
        if(Objects.nonNull(r)){
            return r;
        }
        //清除空的产品信息
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
        //清除空的摄像头信息
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


        PointNew pointNewOld = queryByName(pointNew.getName());
        if(Objects.nonNull(pointNewOld) && !Objects.equals(pointNew.getId(), pointNewOld.getId())){
            return R.fail("该点位已存在");
        }


        if(Objects.isNull(pointNew.getInstallTime()) || Objects.isNull(pointNew.getWarrantyPeriod())){
            pointNew.setWarrantyTime(null);
        }
        pointNew.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);

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

        List<PointProductBind> pointProductBindList = pointProductBindService.queryByPointNewIdAndBindType(pid, PointProductBind.TYPE_POINT);
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

                List<File> productFileList = fileService.queryByProductNewId(productNew.getId(), File.FILE_TYPE_PRODUCT_PRODUCT);
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
        List<PointNew> pointNews = this.pointNewMapper.queryAllByLimitExcel(name, cid, status, customerId, startTime, endTime, createUid, snNo, productSeries, auditStatus);
        return pointNews;
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
        if(Objects.isNull(pointNew.getAddress())){
            return R.fail("请填写详细地址");
        }
        if(Objects.isNull(pointNew.getInstallTime())){
            return R.fail("请填写安装时间");
        }
        if(Objects.isNull(pointNew.getWarrantyPeriod())){
            return R.fail("请填写质保有效期");
        }
        if(Objects.isNull(pointNew.getWarrantyTime())){
            return R.fail("请填写质保结束时间");
        }
        if(Objects.isNull(pointNew.getIsAcceptance())){
            return R.fail("请确认是否验收");
        }
        if(CollectionUtils.isEmpty(pointNew.getProductInfoList())){
            return R.fail("请填写产品信息");
        }
        if(Objects.isNull(pointNew.getCoordX()) || Objects.isNull(pointNew.getCoordY())){
            return R.fail("详细地址经纬度为空，请选择详细地址");
        }
        return null;
    }

    @Override
    public void updatePastWarrantyStatus(){
        pointNewMapper.updatePastWarrantyStatus(System.currentTimeMillis());
    }

    @Override
    public List<PointNewPullVo> queryPointNewPull(String name) {
        return pointNewMapper.queryPointNewPull(name);
    }

    @Override
    public R pointNewMapStatistics(List<BigDecimal> coordXList, List<BigDecimal> coordYList, Long cityId, Long provinceId, Integer productSeries) {
        if(CollectionUtils.isEmpty(coordXList) || coordXList.size() != 2) {
            return R.ok("纬度范围不合法,请传入正确范围");
        }
        if(CollectionUtils.isEmpty(coordYList) || coordYList.size() != 2) {
            return R.ok("经度范围不合法,请传入正确范围");
        }

        List<PointNewMapStatisticsVo> pointNewMapStatisticsVo = pointNewMapper.mapStatistics(coordXList.get(0), coordXList.get(1), coordYList.get(0), coordYList.get(1), cityId, provinceId, productSeries);

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
        if(CollectionUtils.isEmpty(pointAuditStatusQuery.getIds()) || Objects.isNull(pointAuditStatusQuery.getAuditStatus())){
            return R.fail("参数不合法");
        }

        Long uid = SecurityUtils.getUid();
        if(Objects.isNull(uid)) {
            return R.fail("未查询到用户信息");
        }

        User user = userService.getUserById(uid);
        if(Objects.isNull(user)) {
            return R.fail("未查询到用户信息");
        }

        pointAuditStatusQuery.setAuditUid(uid);
        pointAuditStatusQuery.setAuditUserName(user.getUserName());
        pointAuditStatusQuery.setAuditTime(System.currentTimeMillis());
        pointNewMapper.batchUpdateAuditStatus(pointAuditStatusQuery);


        pointAuditStatusQuery.getIds().parallelStream().forEach(id -> {
            PointNew pointNew = this.queryByIdFromDB(id);
            if(Objects.isNull(pointNew)) {
                return;
            }
            //生成操作记录
            generateAuditRecord(id, pointAuditStatusQuery.getAuditStatus(),pointAuditStatusQuery.getAuditRemarks(), user);

            //发送Mq消息
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
        if(CollectionUtils.isEmpty(pointFileList)) {
            return R.ok();
        }

        List<FileVo> fileVos = pointFileList.parallelStream().map(item -> {
            String url = null;
            try{
                long expiration = Optional.ofNullable(storageConfig.getExpiration()).orElse(1000L * 60L * 3L) + System.currentTimeMillis();
                url = aliyunOssService.getOssFileUrl(storageConfig.getBucketName(), storageConfig.getDir() + item.getFileName(), expiration);
            }catch (Exception e){
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
            Set<Map.Entry<Long, Integer>> entrySet =  pointQuery.getProductSerialNumberIdAndSetNoMap().entrySet();
            for(Map.Entry<Long, Integer> entry : entrySet){
                PointProductBind pointProductBind = pointProductBindMapper
                        .selectOne(new QueryWrapper<PointProductBind>()
                                .eq("product_id", entry.getKey()));

                if(ObjectUtils.isNotNull(pointProductBind)){
                    pointProductBindService.deleteById(pointProductBind.getId());
                }
            }
        }



        if (ObjectUtil.isNotEmpty(pointQuery.getProductSerialNumberIdAndSetNoMap())) {
            pointQuery.getProductSerialNumberIdAndSetNoMap().forEach((k,v) -> {
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
        if(Objects.isNull(uid)) {
            return R.fail("未查询到用户信息");
        }

        User user = userService.getUserById(uid);
        if(Objects.isNull(user)) {
            return R.fail("未查询到用户信息");
        }

        if(Objects.isNull(pointAuditStatusQuery.getId()) || Objects.isNull(pointAuditStatusQuery.getAuditStatus())){
            return R.fail("参数不合法");
        }

        PointNew pointNew = this.getById(pointAuditStatusQuery.getId());
        if(Objects.isNull(pointNew)){
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

        //生成记录
        generateAuditRecord(pointNew.getId(), pointNewUpdate.getAuditStatus(),pointNewUpdate.getAuditRemarks(), user);
        //发送Mq消息
        if(Objects.equals(pointNewUpdate.getAuditStatus(), PointNew.AUDIT_STATUS_FAIL)){
            pointNewUpdate.setName(pointNew.getName());
            this.sendAuditStatusNotifyMq(pointNewUpdate);
        }
        return R.ok();
    }

    private void sendAuditStatusNotifyMq(PointNew pointNewUpdate) {
        MaintenanceUserNotifyConfig maintenanceUserNotifyConfig = maintenanceUserNotifyConfigService.queryByPermissions(MaintenanceUserNotifyConfig.TYPE_REVIEW, null);
        if(Objects.isNull(maintenanceUserNotifyConfig) || org.springframework.util.StringUtils.isEmpty(maintenanceUserNotifyConfig.getPhones())) {
            return;
        }
        List<String> phones = JsonUtil.fromJsonArray(maintenanceUserNotifyConfig.getPhones(), String.class);

        if(CollectionUtils.isEmpty(phones)) {
            return;
        }

        if(Objects.equals(maintenanceUserNotifyConfig.getPermissions() & MaintenanceUserNotifyConfig.P_AUDIT_FAILED, MaintenanceUserNotifyConfig.P_AUDIT_FAILED)) {
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

                Pair<Boolean, String> result = rocketMqService.sendSyncMsg(MqConstant.TOPIC_MAINTENANCE_NOTIFY, JsonUtil
                    .toJson(query), MqConstant.TAG_AFTER_SALES, "", 0);
                if (!result.getLeft()) {
                    log.error("SEND WORKORDER AUDIT MQ ERROR! pointName={}, msg={}", pointNewUpdate.getName(), result.getRight());
                }
            });
        }
    }

    private void generateAuditRecord(Long id, Integer status, String remark, User user){
        PointNewAuditRecord pointNewAuditRecord = new PointNewAuditRecord();
        pointNewAuditRecord.setUid(user.getId());
        pointNewAuditRecord.setUserName(user.getUserName());
        pointNewAuditRecord.setPointId(id);
        pointNewAuditRecord.setAuditStatus(status);
        pointNewAuditRecord.setCreateTime(System.currentTimeMillis());
        pointNewAuditRecord.setRemark(remark);
        pointNewAuditRecordService.insert(pointNewAuditRecord);
    }

    //备货管理
    @Override
    public R productNewDeliverList(Long offset, Long size, String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime) {
        List<ProductNewDeliverVo> productNewDeliverVos = pointNewMapper.productNewDeliverList(offset,size,batchNo,sn,deviceName,cabinetSn,tenantName,startTime,endTime);
        if (CollectionUtils.isEmpty(productNewDeliverVos)) {
            return R.ok(new ArrayList<ProductNewDeliverVo>());
        }
        for (int i = 0; i < productNewDeliverVos.size(); i++) {
            //打包时间同压测成功之后的结束时间，因此前端只需要获取压测结束时间
            if (!ProductNew.TEST_RESULT_SUCCESS.equals(productNewDeliverVos.get(i).getTestResult())) {
                productNewDeliverVos.get(i).setTestEndTime(null);
            }
        }
        return R.ok(productNewDeliverVos);
    }

    @Override
    public R productNewDeliverCount(String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime) {
        return R.ok(pointNewMapper.productNewDeliverCount( batchNo,sn,deviceName,cabinetSn,tenantName,startTime,endTime));
    }
    @Override
    public R productNewDeliverExportExcel( String batchNo, String sn, String deviceName, String cabinetSn, String tenantName, Long startTime, Long endTime, HttpServletResponse response) {
        List<ProductNewDeliverVo> productNewDeliverVos = pointNewMapper.productNewDeliverExport( batchNo,sn,deviceName,cabinetSn,tenantName,startTime,endTime);
        if (CollectionUtils.isEmpty(productNewDeliverVos)) {
            return R.ok();
        }
        String fileName = "备货.xlsx";
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        ArrayList<List<Object>> resultList = new ArrayList<>();
        if (productNewDeliverVos.size() > 1000) {
            List<Object> list=new ArrayList<>();
            list.add("导出数据最大不能超过1000条");

            resultList.add(list);
        }else{
            for (int i = 0; i < productNewDeliverVos.size(); i++) {
                //打包时间同压测成功之后的结束时间，因此前端只需要获取压测结束时间
                if (!ProductNew.TEST_RESULT_SUCCESS.equals(productNewDeliverVos.get(i).getTestResult())) {
                    productNewDeliverVos.get(i).setTestEndTime(null);
                }
            }
            String[] header = {"批次号", "资产编码", "deviceName", "柜机编码","productKey", "打包时间", "运营商", "发货时间", "创建时间", "更新时间"};




            List<Object> headList=new ArrayList<Object>();
            for (int i = 0; i <header.length ; i++) {
                headList.add(header[i]);
            }
            resultList.add(headList);
            productNewDeliverVos.parallelStream().forEachOrdered(item -> {
                try {
                    if(Objects.nonNull(item)){
                        List<Object> list = new ArrayList<>();
                        list.add(item.getBatchNo());
                        list.add(item.getNo());
                        list.add(item.getDeviceName());
                        list.add(item.getCabinetSn());
                        list.add(item.getProductKey());
                        list.add(Objects.isNull(item.getTestEndTime())?"":DateUtils.stampToTime(item.getTestEndTime().toString()));
                        list.add(item.getTenantName());
                        list.add(Objects.isNull(item.getDeliverTime())?"":DateUtils.stampToTime(item.getDeliverTime().toString()));
                        list.add(Objects.isNull(item.getCreateTime())?"":DateUtils.stampToTime(item.getCreateTime().toString()));
                        list.add(Objects.isNull(item.getUpdateTime())?"":DateUtils.stampToTime(item.getUpdateTime().toString()));
                        resultList.add(list);
                    }

                } catch (Exception e) {
                    log.error("DeliverExcel Error" , e);
                }
            });
        }


        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition",
                    "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write1(resultList, sheet, table).finish();

        } catch (IOException e) {
            throw new NullPointerException("导出报表失败！请联系管理员处理！");
        }
        return R.ok();
    }

}
