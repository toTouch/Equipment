package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.huaweicloud.sdk.iotda.v5.model.ShowDeviceResponse;
import com.xiliulou.afterserver.constant.AuditProcessConstans;
import com.xiliulou.afterserver.constant.ProductNewStatusSortConstants;
import com.xiliulou.afterserver.entity.AuditProcess;
import com.xiliulou.afterserver.entity.AuditValue;
import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.entity.CompressionRecord;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.DeliverLog;
import com.xiliulou.afterserver.entity.File;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.PointProductBind;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.ProductNewTestContent;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.entity.request.ProductNewRequest;
import com.xiliulou.afterserver.mapper.CompressionRecordMapper;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.mapper.ProductMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.AuditProcessService;
import com.xiliulou.afterserver.service.AuditValueService;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.CameraService;
import com.xiliulou.afterserver.service.ColorCardService;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.service.DeliverLogService;
import com.xiliulou.afterserver.service.DeliverService;
import com.xiliulou.afterserver.service.FileService;
import com.xiliulou.afterserver.service.IotCardService;
import com.xiliulou.afterserver.service.PointProductBindService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductNewTestContentService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.DeviceSolutionUtil;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import com.xiliulou.afterserver.web.query.CabinetCompressionQuery;
import com.xiliulou.afterserver.web.query.CompressionQuery;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import com.xiliulou.afterserver.web.query.ProductNewQuery;
import com.xiliulou.afterserver.web.vo.AuditProcessVo;
import com.xiliulou.afterserver.web.vo.BatchProductNewVo;
import com.xiliulou.afterserver.web.vo.CabinetCompressionContentVo;
import com.xiliulou.afterserver.web.vo.CabinetCompressionVo;
import com.xiliulou.afterserver.web.vo.DeliverProductNewInfoVo;
import com.xiliulou.afterserver.web.vo.DeviceMessageVo;
import com.xiliulou.afterserver.web.vo.ProductNewProcessInfoVo;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.iot.entity.response.QueryDeviceDetailResult;
import com.xiliulou.iot.service.RegisterDeviceService;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xiliulou.afterserver.constant.ProductNewStatusSortConstants.STATUS_PRODUCTION;
import static com.xiliulou.afterserver.constant.ProductNewStatusSortConstants.STATUS_TESTED;
import static com.xiliulou.afterserver.entity.Batch.ALIYUN_SaaS_ELECTRIC_SWAP_CABINET;
import static com.xiliulou.afterserver.entity.Batch.API_ELECTRIC_SWAP_CABINET;
import static com.xiliulou.afterserver.entity.Batch.HUAWEI_CLOUD_SaaS;
import static com.xiliulou.afterserver.entity.Batch.TCP_ELECTRIC_SWAP_CABINET;

/**
 * (ProductNew)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-17 10:29:14
 */
@Service("productNewService")
@Slf4j
public class ProductNewServiceImpl extends ServiceImpl<ProductNewMapper, ProductNew> implements ProductNewService {
    
    public static final int MAX_BYTES_ERROR_MESSAGE = 190;
    
    @Autowired
    private DeviceSolutionUtil deviceSolutionUtil;
    
    @Autowired
    private RegisterDeviceService registerDeviceService;
    
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
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private IotCardService iotCardService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private CameraService cameraService;
    
    @Autowired
    private DeliverLogService deliverLogService;
    
    @Autowired
    private DeliverService deliverService;
    
    @Autowired
    private WarehouseService warehouseService;
    
    @Autowired
    private ColorCardService colorCardService;
    
    @Autowired
    private StorageConfig storageConfig;
    
    @Autowired
    private AliyunOssService aliyunOssService;
    
    @Autowired
    private PointProductBindService pointProductBindService;
    
    @Autowired
    private AuditProcessService auditProcessService;
    
    @Autowired
    private AuditValueService auditValueService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ProductNewTestContentService productNewTestContentService;
    
    @Autowired
    private CompressionRecordMapper compressionRecordMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    
    public static String subStringByBytes(String str) {
        if (Objects.isNull(str)) {
            return str;
        }
        byte[] bytes = str.getBytes(StandardCharsets.UTF_8);
        if (bytes.length > MAX_BYTES_ERROR_MESSAGE) {
            byte[] subBytes = Arrays.copyOfRange(bytes, 0, MAX_BYTES_ERROR_MESSAGE); // 截取前190个字节
            str = new String(subBytes, StandardCharsets.UTF_8);
            
        }
        return str;
    }
    
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
    public List<ProductNew> queryAllByLimit(int offset, int limit, ProductNewRequest request, List<Long> list) {
        String no = request.getNo();
        Long modelId = request.getModelId();
        Long startTime = request.getStartTime();
        Long endTime = request.getEndTime();
        String testType = request.getTestType();
        String cabinetSn = request.getCabinetSn();
        Long batchId = request.getBatchId();
        
        return this.productNewMapper.queryAllByLimit(offset, limit, no, modelId, startTime, endTime, list, testType, cabinetSn, batchId);
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
        
        if (Objects.isNull(productNew.getProductCount()) || productNew.getProductCount() <= 0) {
            return R.fail("请传入正确的产品数量");
        }
        
        Supplier supplier = supplierService.getById(productNew.getSupplierId());
        if (Objects.isNull(supplier)) {
            return R.fail("供应商选择有误，请检查");
        }
        if (Objects.isNull(supplier.getCode())) {
            return R.fail("供应商编码为空,请重新选择");
        }
        
        Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(batch)) {
            return R.fail("批次号选择有误，请检查");
        }
        if (Objects.isNull(batch.getBatchNo())) {
            return R.fail("批次号为空，请重新选择");
        }
        
        StringBuilder codeStr = new StringBuilder();
        codeStr.append(product.getCode()).append("-");
        codeStr.append(supplier.getCode()).append(batch.getBatchNo());
        if (Objects.nonNull(productNew.getType())) {
            codeStr.append(productNew.getType());
        }
        productNew.setCode(codeStr.toString());
        
        Integer serialNum = productNewMapper.queryMaxSerialNum(codeStr.toString());
        if (Objects.isNull(serialNum)) {
            serialNum = 0;
        }
        
        for (int i = 0; i < productNew.getProductCount(); i++) {
            serialNum++;
            String serialNumStr = String.format("%04d", serialNum);
            StringBuilder sb = new StringBuilder();
            sb.append(product.getCode()).append("-");
            sb.append(supplier.getCode()).append(batch.getBatchNo()).append(serialNumStr);
            if (Objects.nonNull(productNew.getType())) {
                sb.append(productNew.getType());
            }
            
            productNew.setSerialNum(serialNumStr);
            productNew.setNo(sb.toString());
            productNew.setCreateTime(System.currentTimeMillis());
            productNew.setDelFlag(ProductNew.DEL_NORMAL);
            this.insert(productNew);
        }
        
        return R.ok();
    }
    
    @Override
    public R putAdminProductNew(ProductNewQuery query) {
        ProductNew productNewOld = this.productNewMapper.queryById(query.getId());
        if (Objects.isNull(productNewOld)) {
            return R.fail("未查询到相关柜机信息");
        }
        
        //        if (Objects.nonNull(query.getCameraCardId())) {
        //            Camera camera = cameraService.getById(query.getCameraCardId());
        //            if (Objects.isNull(camera)) {
        //                return R.fail("未查询到摄像头序列号");
        //            }
        //        }
        
        Double statusValueOld = ProductNewStatusSortConstants.acquireStatusValue(productNewOld.getStatus());
        if (Objects.isNull(statusValueOld)) {
            return R.fail("产品状态不合法");
        }
        Double statusValue = ProductNewStatusSortConstants.acquireStatusValue(query.getStatus());
        if (Objects.isNull(statusValue)) {
            return R.fail("输入状态不合法");
        }
        
        if (statusValueOld.compareTo(statusValue) > 0) {
            return R.fail("产品状态不能回溯，请合法修改");
        }
        
        ProductNew updateProductNew = new ProductNew();
        updateProductNew.setId(query.getId());
        updateProductNew.setExpirationStartTime(query.getExpirationStartTime());
        updateProductNew.setYears(query.getYears());
        updateProductNew.setExpirationEndTime(query.getExpirationEndTime());
        updateProductNew.setStatus(query.getStatus());
        updateProductNew.setUpdateTime(System.currentTimeMillis());
        // 这里状态改成已收货 位置要发生改变 利用发货日志表查询
        if (Objects.equals(3, query.getStatus())) {
            DeliverLog deliverLog = deliverLogService.getBaseMapper().selectOne(new QueryWrapper<DeliverLog>().eq("product_id", query.getId()));
            
            if (Objects.nonNull(deliverLog)) {
                Deliver deliver = deliverService.getById(deliverLog.getId());
                if (Objects.nonNull(deliver)) {
                    Integer destinationType = deliver.getDestinationType();
                    if (Objects.equals(destinationType, 1)) {
                        PointNew pointNew = pointNewMapper.selectOne(new QueryWrapper<PointNew>().eq("name", deliver.getDestination()));
                        if (Objects.nonNull(pointNew)) {
                            this.bindPoint(query.getId(), pointNew.getId(), 1);
                        }
                    }
                    if (Objects.equals(destinationType, 2)) {
                        WareHouse wareHouse = warehouseService.getBaseMapper().selectOne(new QueryWrapper<WareHouse>().eq("ware_houses", deliver.getDestination()));
                        if (Objects.nonNull(wareHouse)) {
                            this.bindPoint(query.getId(), Long.valueOf(wareHouse.getId()), 2);
                            
                        }
                    }
                    if (Objects.equals(destinationType, 3)) {
                        Supplier supplier = supplierService.querySupplierName(deliver.getDestination());
                        if (Objects.nonNull(supplier)) {
                            this.bindPoint(query.getId(), supplier.getId(), 3);
                        }
                    }
                }
            }
        }
        //        updateProductNew.setIotCardId(query.getIotCardId());
        //        updateProductNew.setCameraId(query.getCameraCardId());
        //        updateProductNew.setColor(query.getColor());
        //        updateProductNew.setSurface(query.getSurface());
        updateProductNew.setRemarks(query.getRemarks());
        updateProductNew.setAppVersion(query.getAppVersion());
        updateProductNew.setSysVersion(query.getSysVersion());
        
        Integer update = this.update(updateProductNew);
        if (update > 0) {
            return R.ok();
        }
        return R.fail("数据库错误");
    }
    
    @Override
    public R delAdminProductNew(Long id) {
        // 查询产品
        ProductNew productNew = productNewMapper.queryById(id);
        Boolean aBoolean = this.deleteById(id);
        if (aBoolean) {
            // 更新产品数 同产品批次可能不同
            Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
            batchService.queryByIdFromDB(id);
            if (Objects.isNull(batch) && batch.getProductNum() <= 0) {
                return R.fail("找不到产品，请刷新页面");
            }
            int i = batch.getProductNum() - 1;
            batch.setProductNum(i);
            batchService.update(batch);
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
        if (isRepeat) {
            return R.fail("有重复数据");
        }
        
        productNewList.forEach(item -> {
            item.setCreateTime(System.currentTimeMillis());
            int update = productNewMapper.update(item);
            if (update == 0) {
                log.error("WX ERROR!   update ProductNew error data:{}", item.toString());
                throw new NullPointerException("数据库异常，请联系管理员");
            }
        });
        return R.ok();
    }
    
    @Override
    public ProductNew prdouctInfoByNo(String no) {
        LambdaQueryWrapper<ProductNew> eq = new LambdaQueryWrapper<ProductNew>().eq(ProductNew::getNo, no).eq(ProductNew::getDelFlag, ProductNew.DEL_NORMAL);
        ProductNew productNew = this.productNewMapper.selectOne(eq);
        if (Objects.nonNull(productNew)) {
            Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
            if (Objects.nonNull(batch)) {
                productNew.setBatchName(batch.getBatchNo());
            }
            
            Product product = productService.getById(productNew.getModelId());
            if (Objects.nonNull(product)) {
                productNew.setModelName(product.getName());
            }
            
            return productNew;
        }
        return null;
    }
    
    @Override
    public Integer count(int offset, int limit, ProductNewRequest request, List<Long> list) {
        String no = request.getNo();
        Long modelId = request.getModelId();
        Long startTime = request.getStartTime();
        Long endTime = request.getEndTime();
        String testType = request.getTestType();
        String cabinetSn = request.getCabinetSn();
        Long batchId = request.getBatchId();
        return this.productNewMapper.countProduct(no, modelId, startTime, endTime, list, testType, cabinetSn, batchId);
    }
    
    @Override
    public R getProductFile(Long id, Integer fileType) {
        List<File> fileList = fileService.queryByProductNewId(id, fileType);
        return R.ok(fileList);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateStatusFromBatch(List<Long> ids, Integer status) {
        int row = this.productNewMapper.updateStatusFromBatch(ids, status);
        if (row == 0) {
            return R.fail("未修改数据");
        }
        return R.ok();
    }
    
    @Override
    public R queryProductInfo(String no) {
        Map<String, Object> map = new HashMap<>();
        
        QueryWrapper<ProductNew> wrapper = new QueryWrapper<>();
        wrapper.eq("no", no);
        ProductNew productNew = productNewMapper.selectOne(wrapper);
        
        if (ObjectUtils.isNotNull(productNew)) {
            Product product = productService.getById(productNew.getModelId());
            if (ObjectUtils.isNotNull(product)) {
                map.put("name", product.getName());
            }
            Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
            if (ObjectUtils.isNotNull(batch)) {
                map.put("batchNo", batch.getBatchNo());
            }
            map.put("no", no);
        }
        
        if (map.size() < 3 && map.size() > 1) {
            log.error("查询结果有误，请重新录入: name={}, batchNo={}, no={}", map.get("name"), map.get("batchNo"), map.get("no"));
            return R.fail("查询结果有误，请重新录入");
        }
        
        if (map.isEmpty()) {
            log.info("查无此产品");
            return R.fail("查无此产品");
        }
        
        return R.fail(map);
    }
    
    @Override
    public R queryLikeProductByNo(String no) {
        QueryWrapper<ProductNew> wrapper = null;
        if (!StringUtils.checkValNull(no)) {
            wrapper = new QueryWrapper<>();
            wrapper.like("no", no);
        }
        List<ProductNew> list = productNewMapper.selectList(wrapper);
        return R.fail(list);
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public R bindPoint(Long productId, Long pointId, Integer pointType) {
        ProductNew productNew = this.queryByIdFromDB(productId);
        if (Objects.isNull(productNew)) {
            return R.fail("未查询到相关柜机");
        }
        productNew.setUpdateTime(System.currentTimeMillis());
        this.update(productNew);
        
        PointProductBind pointProductBind = pointProductBindMapper.selectOne(new QueryWrapper<PointProductBind>().eq("product_id", productId));
        
        if (ObjectUtils.isNotNull(pointProductBind)) {
            pointProductBindMapper.deleteById(pointProductBind.getId());
        }
        
        if (Objects.isNull(pointType)) {
            return R.fail("请输入位置类型");
        }
        
        PointProductBind bind = new PointProductBind();
        bind.setPointId(pointId);
        bind.setProductId(productId);
        bind.setPointType(pointType);
        pointProductBindMapper.insert(bind);
        
        return R.ok();
    }
    
    @Override
    public List<ProductNew> queryByBatch(Long id) {
        QueryWrapper<ProductNew> wrapper = new QueryWrapper<>();
        wrapper.eq("batch_id", id);
        wrapper.eq("del_flag", ProductNew.DEL_NORMAL);
        List<ProductNew> productNewList = productNewMapper.selectList(wrapper);
        return productNewList;
    }
    
    @Override
    public R checkCompression(ApiRequestQuery apiRequestQuery) {
        log.info("url: /app/compression/check checkCompression: {}", apiRequestQuery.getData());
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! check error", e);
            return R.fail(null, null, "参数解析错误");
        }
        
        if (CollectionUtils.isEmpty(compression.getNoList())) {
            return R.fail(null, null, "压测柜机不存在，请核对");
        }
        
        List<String> errorNos = new ArrayList<>();
        List<String> errorStatus = new ArrayList<>();
        
        // String mainProductNo = "";
        
        if (Objects.isNull(compression.getIotCard())) {
            return R.fail(null, null, "未上报主柜物联网卡号");
        }
        
        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if (Objects.isNull(iotCard)) {
            return R.fail(null, null, "物联网卡号【" + compression.getIotCard() + "】不存在，请核对");
        }
        
        ArrayList<ProductNew> mainProducts = new ArrayList<>(1);
        
        for (String no : compression.getNoList()) {
            ProductNew product = this.queryByNo(no);
            if (Objects.isNull(product)) {
                errorNos.add(no);
            } else {
                // 统计是否有多主柜
                if (Objects.equals(product.getType(), ProductNew.TYPE_M)) {
                    mainProducts.add(product);
                }
                
                // 统计是否有前置检测未通过柜机
                AuditProcess auditProcess = auditProcessService.getByType(AuditProcess.TYPE_PRE);
                Integer status = auditProcessService.getAuditProcessStatus(auditProcess, product);
                if ((!Objects.equals(status, AuditProcessVo.STATUS_FINISHED) || !Objects.equals(product.getStatus(), ProductNewStatusSortConstants.STATUS_PRE_DETECTION))
                        && !Objects.equals(product.getStatus(), STATUS_TESTED)) {
                    errorStatus.add(product.getNo());
                }
            }
        }
        
        if (CollectionUtils.isNotEmpty(errorNos)) {
            return R.fail(errorNos, null, "资产编码不存在，请核对");
        }
        
        if (!(mainProducts.size() == 1) || Objects.isNull(mainProducts.get(0))) {
            return R.fail(mainProducts, null, "主柜不存在或存在多个，请核对");
        }
        
        if (CollectionUtils.isNotEmpty(errorStatus)) {
            return R.fail(errorStatus, null, "柜机前置检测未通过或非前置检测完成、已测试状态,请核对");
        }
        
        // 更新物联网卡
        ProductNew mainProduct = mainProducts.get(0);
        
        // auditValueService.biandOrUnbindEntry(AuditProcessConstans.PRODUCT_IOT_AUDIT_ENTRY, iotCard.getSn(), mainProduct.getId());
        log.info("url: /app/compression/check mainProduct.getNo: {}", mainProduct.getNo());
        
        return R.ok(Arrays.asList(mainProduct.getNo()));
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public R successCompression(ApiRequestQuery apiRequestQuery) {
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
            return R.fail(null, null, "参数解析错误");
        }
        
        if (CollectionUtils.isEmpty(compression.getNoList())) {
            return R.fail(null, null, "压测柜机不存在，请核对");
        }
        
        if (StringUtils.isBlank(compression.getCompressionFile())) {
            return R.fail(null, null, "测试文件为空");
        }
        
        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if (Objects.isNull(iotCard)) {
            log.info("未查询到物联网卡信息 {}", compression.getIotCard());
            return R.fail(null, null, "未查询到物联网卡信息");
        }
        
        ArrayList<ProductNew> mainProducts = new ArrayList(1);
        for (String no : compression.getNoList()) {
            ProductNew product = this.queryByNo(no);
            if (Objects.equals(product.getType(), ProductNew.TYPE_M)) {
                mainProducts.add(product);
            }
            
        }
        
        if (!(mainProducts.size() == 1) || Objects.isNull(mainProducts.get(0))) {
            return R.fail(mainProducts, null, "主柜不存在或存在多个，请核对");
        }
        
        AuditProcess byType = auditProcessService.getByType(AuditProcess.TYPE_POST);
        ProductNew mainProduct = mainProducts.get(0);
        // 需要拷贝的值的组件id
        List<Long> copyLong = Arrays.asList(AuditProcessConstans.CAMERA_SN_AUDIT_ENTRY, AuditProcessConstans.CAMERA_IOT_AUDIT_ENTRY, AuditProcessConstans.PRODUCT_IOT_AUDIT_ENTRY,
                AuditProcessConstans.PRODUCT_COLOR_AUDIT_ENTRY, AuditProcessConstans.DOOR_COLOR_AUDIT_ENTRY, AuditProcessConstans.PRODUCT_SURFACE_AUDIT_ENTRY,
                AuditProcessConstans.CAMERA_SN_AUDIT_ENTRY_TOW);
        // 获取主柜需要同步到副柜的值
        List<AuditValue> mainValues = auditValueService.getByPidAndEntryIds(copyLong, mainProduct.getId());
        
        for (String no : compression.getNoList()) {
            ProductNew productOld = this.queryByNo(no);
            if (Objects.isNull(productOld)) {
                continue;
            }
            
            ProductNew product = productNewMapper.queryByNo(no);
            if (Objects.isNull(product)) {
                continue;
            }
            
            // ProductNew product = new ProductNew();
            // product.setNo(no);
            product.setTestFile(compression.getCompressionFile());
            product.setTestResult(1);
            product.setTestType(compression.getTestType());
            
            // 这里需要将主柜的数据同步到副柜
            // 获取副柜需要同步的值
            // List<AuditValue> productValues = auditValueService.getByPidAndEntryIds(copyLong, product.getId());
            // 更新
            auditValueService.copyValueToTargetValueIsNoll(mainValues, product.getId());
            
            // 更新柜机状态
            Integer status = auditProcessService.getAuditProcessStatus(byType, productOld);
            if (Objects.equals(status, AuditProcessVo.STATUS_FINISHED)) {
                product.setStatus(ProductNewStatusSortConstants.STATUS_POST_DETECTION);
            } else {
                product.setStatus(STATUS_TESTED);
            }
            productNewMapper.updateByNo(product);
        }
        
        return R.ok();
    }
    
    @Override
    public R findIotCard(String no) {
        //        ProductNew productNew = this.productNewMapper.selectOne(new QueryWrapper<ProductNew>().eq("no", no));
        //        if (Objects.isNull(productNew)) {
        //            return R.fail(null, null, "未查询到柜机信息，请检查资产编码是否正确");
        //        }
        //
        //        if (Objects.equals(productNew.getType(), ProductNew.TYPE_M)) {
        //            return R.fail(null, null, "资产编码不是主柜类型，请检查");
        //        }
        //
        //        if (Objects.isNull(productNew.getIotCardId())) {
        //            return R.fail(null, null, "柜机未录入物联网卡号");
        //        }
        //        Map<String, Object> result = new HashMap<>();
        //        result.put("data", productNew.getIotCardId());
        //        return R.ok(result);
        return R.ok();
    }
    
    @Override
    public R queryByBatchAndSupplier(Long batchId, Long offset, Long size, String productNo) {
        Long uid = SecurityUtils.getUid();
        if (Objects.isNull(uid)) {
            return R.fail("未查询到相关用户");
        }
        
        User user = userService.getUserById(uid);
        if (Objects.isNull(user)) {
            return R.fail("未查询到相关用户");
        }
        
        Supplier supplier = supplierService.getById(user.getThirdId());
        if (Objects.isNull(supplier)) {
            return R.fail("用户未绑定工厂，请联系管理员");
        }
        
        Page page = PageUtil.getPage(offset, size);
        page = productNewMapper.selectPage(page,
                new QueryWrapper<ProductNew>().eq("batch_id", batchId).eq("supplier_id", user.getThirdId()).like(StringUtils.isNotBlank(productNo), "no", productNo)
                        .eq("del_flag", ProductNew.DEL_NORMAL));
        
        List<ProductNew> list = page.getRecords();
        if (CollectionUtils.isEmpty(list)) {
            return R.fail(null, "查询产品信息为空");
        }
        
        List<BatchProductNewVo> data = new ArrayList<>();
        list.stream().forEach(item -> {
            BatchProductNewVo batchProductNewVo = new BatchProductNewVo();
            batchProductNewVo.setId(item.getId());
            batchProductNewVo.setNo(item.getNo());
            batchProductNewVo.setStatus(this.getStatusName(item.getStatus()));
            
            data.add(batchProductNewVo);
        });
        
        Map result = new HashMap(2);
        result.put("data", data);
        result.put("total", page.getTotal());
        return R.ok(result);
    }
    
    @Override
    public R queryProductNewProcessInfo(String no, HttpServletResponse response) {
        ProductNew productNew = this.queryByNo(no);
        
        User user = userService.getUserById(SecurityUtils.getUid());
        if (Objects.isNull(user)) {
            log.error("QUERY PROCESS INFO ERROR! not found uid! uid={}", SecurityUtils.getUid());
            return R.fail(null, "未查询到相关用户");
        }
        
        if (Objects.isNull(productNew)) {
            log.error("QUERY PROCESS INFO ERROR! not found no! no={}", no);
            return R.fail(null, "资产编码不存在");
        }
        Product product = productMapper.selectById(productNew.getModelId());
        if (!Objects.equals(productNew.getSupplierId(), user.getThirdId())) {
            log.error("QUERY PROCESS INFO ERROR! current user inconsistent  factory! supplierId={}, userThirdId", productNew.getSupplierId(), user.getThirdId());
            return R.fail(null, "柜机厂家与登录厂家不一致，请重新登陆");
        }
        
        Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(batch)) {
            log.error("QUERY PROCESS INFO ERROR! not found batch! batch={}", productNew.getBatchId());
            return R.fail(null, "柜机未绑定批次，请检查");
        }
        
        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AuditProcessVo> voList = new ArrayList<>();
        ProductNewProcessInfoVo vo = new ProductNewProcessInfoVo();
        vo.setId(productNew.getId());
        vo.setNo(productNew.getNo());
        vo.setBatchId(productNew.getBatchId());
        vo.setBatchNo(batch.getBatchNo());
        vo.setModelName(product.getName());
        vo.setAuditProcessList(voList);
        vo.setCreateTime(simp.format(new Date(productNew.getCreateTime())));
        vo.setProductStatus(getStatusName(productNew.getStatus()));
        vo.setCabinetSn(productNew.getCabinetSn()); // 资产编码打印同样实现底部增加对应柜机编码
        vo.setDeviceName(productNew.getDeviceName());
        vo.setProductKey(productNew.getProductKey());
        
        List<AuditProcess> auditProcessList = auditProcessService.getBaseMapper().selectList(new QueryWrapper<AuditProcess>().orderByAsc("id"));
        // 如果搜索页面配置为空，则只获取压测状态，发货状态随压测状态改变
        if (CollectionUtils.isEmpty(auditProcessList)) {
            AuditProcessVo testVo = auditProcessService.createTestAuditProcessVo();
            testVo.setStatus(ProductNew.TEST_RESULT_SUCCESS.equals(productNew.getTestResult()) ? AuditProcessVo.STATUS_FINISHED : AuditProcessVo.STATUS_EXECUTING);
            AuditProcessVo deliverVo = auditProcessService.createDeliverAuditProcessVo();
            boolean flag = ProductNewStatusSortConstants.acquireStatusValue(productNew.getStatus()) >= ProductNewStatusSortConstants.acquireStatusValue(
                    ProductNewStatusSortConstants.STATUS_SHIPPED);
            deliverVo.setStatus(flag ? ProductNewProcessInfoVo.STATUS_FINISHED : ProductNewProcessInfoVo.STATUS_UN_FINISHED);
            voList.add(testVo);
            voList.add(deliverVo);
            
            vo.setAuditProcessList(voList);
            return R.ok(vo);
        }
        
        // 统计流程状态的类型共有几种
        Set<Integer> statusSet = new HashSet<>(3);
        // 获取所有流程状态
        auditProcessList.parallelStream().forEachOrdered(item -> {
            Integer status = auditProcessService.getAuditProcessStatus(item, productNew);
            AuditProcessVo auditProcessVo = new AuditProcessVo();
            BeanUtils.copyProperties(item, auditProcessVo);
            auditProcessVo.setStatus(status);
            
            voList.add(auditProcessVo);
            statusSet.add(status);
        });
        
        AuditProcessVo preAuditProcessVo = null;
        AuditProcessVo postAuditProcessVo = null;
        for (AuditProcessVo item : voList) {
            if (Objects.equals(AuditProcess.TYPE_PRE, item.getType())) {
                preAuditProcessVo = item;
            }
            
            if (Objects.equals(AuditProcess.TYPE_POST, item.getType())) {
                postAuditProcessVo = item;
            }
        }
        
        // 获取压测状态
        AuditProcessVo testAuditProcessVo = auditProcessService.createTestAuditProcessVo();
        // 获取当前压测状态，
        if (Objects.equals(productNew.getTestResult(), ProductNew.TEST_RESULT_SUCCESS)) {
            // 如果测试完成 那么压测状态一定是绿灯
            testAuditProcessVo.setStatus(AuditProcessVo.STATUS_FINISHED);
        } else {
            // 如果测试未完成，需要看前置检查是否完成：完成则压测正在执行，未完则置灰
            if (Objects.nonNull(preAuditProcessVo)) {
                testAuditProcessVo.setStatus(
                        Objects.equals(preAuditProcessVo.getStatus(), AuditProcessVo.STATUS_FINISHED) ? AuditProcessVo.STATUS_EXECUTING : AuditProcessVo.STATUS_UNFINISHED);
            }
        }
        voList.add(1, testAuditProcessVo);
        statusSet.add(testAuditProcessVo.getStatus());
        
        auditProcessService.processStatusAdjustment(voList);
        
        AuditProcessVo deliverVo = auditProcessService.createDeliverAuditProcessVo();
        boolean flag = ProductNewStatusSortConstants.acquireStatusValue(productNew.getStatus()) >= ProductNewStatusSortConstants.acquireStatusValue(
                ProductNewStatusSortConstants.STATUS_SHIPPED);
        deliverVo.setStatus(flag ? ProductNewProcessInfoVo.STATUS_FINISHED : ProductNewProcessInfoVo.STATUS_UN_FINISHED);
        voList.add(deliverVo);
        
        if (Objects.equals(preAuditProcessVo.getStatus(), AuditProcessVo.STATUS_FINISHED) && Objects.equals(testAuditProcessVo.getStatus(), AuditProcessVo.STATUS_FINISHED) && !Objects.equals(
                postAuditProcessVo.getStatus(), AuditProcessVo.STATUS_FINISHED)) {
            postAuditProcessVo.setStatus(AuditProcessVo.STATUS_EXECUTING);
        }
        return R.ok(vo);
    }
    
    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public R updateProductNew(ProductNewDetailsQuery query) {
        //        ProductNew productNewOld = this.productNewMapper.queryById(query.getId());
        //        if (Objects.isNull(productNewOld)) {
        //            return R.fail(null, null, "未查询到相关柜机信息");
        //        }
        //
        //        Camera camera = new Camera();
        //        if (StringUtils.isNotBlank(query.getSerialNum())) {
        //            camera = cameraService.queryBySerialNum(query.getSerialNum());
        //            if (Objects.isNull(camera)) {
        //                return R.fail(null, null, "未查询到摄像头序列号");
        //            }
        //
        //           /* ProductNew productNew = productNewMapper.selectOne(new QueryWrapper<ProductNew>()
        //                    .eq("camera_id", camera.getId())
        //                    .eq("del_flag", ProductNew.DEL_NORMAL));
        //
        //            if(Objects.nonNull(productNew) && !Objects.equals(productNew.getId(), query.getId())){
        //                return R.fail(null, null, "序列号已绑定到其他产品");
        //            }*/
        //        }
        //
        //
        //        ProductNew updateProductNew = new ProductNew();
        //        updateProductNew.setId(query.getId());
        //        updateProductNew.setCameraId(camera.getId());
        //        updateProductNew.setIotCardId(query.getIotCardId());
        //        updateProductNew.setColor(query.getColor());
        //        updateProductNew.setSurface(query.getSurface());
        //        this.productNewMapper.updateById(updateProductNew);
        //
        //        if (Objects.nonNull(camera.getId())) {
        //            Camera updateCamera = new Camera();
        //            updateCamera.setId(camera.getId());
        //            updateCamera.setIotCardId(query.getCameraCardId());
        //            cameraService.updateById(updateCamera);
        //        }
        
        return R.ok();
    }
    
    @Override
    public R checkProperty(String no, String deliverNo) {
        ProductNew productNew = this.queryByNo(no);
        if (Objects.isNull(productNew)) {
            return R.fail(null, "00000", "柜机资产编码不存在，请核对");
        }
        
        Batch productBatch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(productBatch)) {
            return R.fail(null, "00000", "未查询到柜机批次，请联系管理员");
        }
        
        Product product = productService.getById(productNew.getModelId());
        if (Objects.isNull(product)) {
            return R.fail(null, "00000", "未查询到柜机类型，请联系管理员");
        }
        
        AuditProcess pre = auditProcessService.getByType(AuditProcess.TYPE_PRE);
        Integer preStatus = auditProcessService.getAuditProcessStatus(pre, productNew);
        if (!Objects.equals(preStatus, AuditProcessVo.STATUS_FINISHED)) {
            return R.fail(null, "10001", "产品前置检查未完成");
        }
        
        if (!Objects.equals(productNew.getTestResult(), ProductNew.TEST_RESULT_SUCCESS)) {
            return R.fail(null, "00000", "产品老化测试未完成");
        }
        
        AuditProcess post = auditProcessService.getByType(AuditProcess.TYPE_POST);
        Integer status = auditProcessService.getAuditProcessStatus(post, productNew);
        if (!Objects.equals(status, AuditProcessVo.STATUS_FINISHED)) {
            return R.fail(null, "10002", "产品后置检查未完成");
        }
        
        if (!Objects.equals(productNew.getStatus(), ProductNewStatusSortConstants.STATUS_POST_DETECTION)) {
            return R.fail(null, "00000", statusErrorMsg(productNew.getStatus()));
        }
        
        SimpleDateFormat sim = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dataTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        DeliverProductNewInfoVo vo = new DeliverProductNewInfoVo();
        vo.setId(productNew.getId());
        vo.setBatchId(productNew.getBatchId());
        vo.setBatchNo(productBatch.getBatchNo());
        vo.setModelName(product.getName());
        vo.setNo(productNew.getNo());
        vo.setStatusName(getStatusName(productNew.getStatus()));
        vo.setInsertTime(sim.format(new Date()));
        
        Deliver deliver = deliverService.queryByNo(deliverNo);
        if (Objects.isNull(deliver)) {
            vo.setCanPrint(false);
            return R.ok(vo);
        }
        
        vo.setCanPrint(true);
        vo.setCustomerName(queryCustomerName(deliver));
        vo.setSum(Optional.ofNullable(JsonUtil.fromJsonArray(deliver.getQuantity(), Integer.class)).orElse(Lists.newArrayList()).stream().reduce(0, Integer::sum));
        vo.setType(Objects.equals(productNew.getType(), ProductNew.TYPE_M) ? "主柜" : "副柜");
        vo.setProductionTime(dataTimeFormat.format(new Date()));
        vo.setProductColor(auditValueService.getValue(AuditProcessConstans.PRODUCT_COLOR_AUDIT_ENTRY, productNew.getId()));
        vo.setDoorColor(auditValueService.getValue(AuditProcessConstans.DOOR_COLOR_AUDIT_ENTRY, productNew.getId()));
        return R.ok(vo);
    }
    
    private String queryCustomerName(Deliver deliver) {
        if (Objects.isNull(deliver)) {
            return null;
        }
        if (Objects.equals(deliver.getDestinationType(), Deliver.DESTINATION_TYPE_FACTORY) || Objects.equals(deliver.getDestinationType(), Deliver.DESTINATION_TYPE_WAREHOUSE)) {
            return deliver.getDestination();
        }
        
        PointNew pointNew = pointNewMapper.queryById(deliver.getDestinationId());
        if (Objects.isNull(pointNew)) {
            return null;
        }
        
        Customer customer = customerService.getById(pointNew.getCustomerId());
        if (Objects.isNull(customer)) {
            return null;
        }
        return customer.getName();
    }
    
    private String statusErrorMsg(Integer status) {
        String msg = "";
        if (Objects.equals(status, STATUS_PRODUCTION)) {
            msg += "产品生产中，请检验柜机后发货";
        }
        if (Objects.equals(status, ProductNewStatusSortConstants.STATUS_PRE_DETECTION)) {
            msg += "前置检测完成，请将其余检验完成发货";
        }
        if (Objects.equals(status, STATUS_TESTED)) {
            msg += "产品已测试，请将其余检验完成发货";
        }
        boolean isDeliver =
                ProductNewStatusSortConstants.acquireStatusValue(status) > ProductNewStatusSortConstants.acquireStatusValue(ProductNewStatusSortConstants.STATUS_POST_DETECTION);
        if (isDeliver) {
            msg += "产品已发货";
        }
        return msg;
    }
    
    @Override
    public R factorySaveFile(File file) {
        //        if (Objects.equals(File.FILE_TYPE_PRODUCT_ACCESSORY_PACKAGING, file.getFileType()) && Objects.equals(File.TYPE_PRODUCT, file.getType())) {
        //            List<File> fileList = fileService.queryByProductNewId(file.getBindId(), File.FILE_TYPE_PRODUCT_ACCESSORY_PACKAGING);
        //            if (Objects.nonNull(fileList) && fileList.size() >= 4) {
        //                return R.fail("附件包上传数量已达上限");
        //            }
        //        }
        //
        //        if (Objects.equals(File.FILE_TYPE_PRODUCT_OUTER_PACKAGING, file.getFileType()) && Objects.equals(File.TYPE_PRODUCT, file.getType())) {
        //            List<File> fileList = fileService.queryByProductNewId(file.getBindId(), File.FILE_TYPE_PRODUCT_OUTER_PACKAGING);
        //            if (Objects.nonNull(fileList) && fileList.size() >= 4) {
        //                return R.fail("外包装上传数量已达上限");
        //            }
        //        }
        //
        //        if (Objects.equals(File.FILE_TYPE_PRODUCT_QUALITY_INSPECTION, file.getFileType()) && Objects.equals(File.TYPE_PRODUCT, file.getType())) {
        //            List<File> fileSheepList = fileService.queryByProductNewId(file.getBindId(), File.FILE_TYPE_PRODUCT_QUALITY_INSPECTION);
        //            if (Objects.nonNull(fileSheepList) && fileSheepList.size() == 1) {
        //                fileService.removeFile(file.getId(), 0);
        //            }
        //        }
        
        file.setCreateTime(System.currentTimeMillis());
        fileService.save(file);
        return R.ok();
    }
    
    @Override
    public R selectListProductNews(Integer offset, Integer limit, ProductNewRequest request) {
        if (StringUtils.isNotBlank(request.getBatchName())) {
            List<Batch> batches = batchService.queryByName(request.getBatchName());
            if (CollectionUtils.isEmpty(batches)) {
                HashMap<String, Object> stringObjectHashMap = new HashMap<>(2);
                stringObjectHashMap.put("data", new ArrayList<>());
                stringObjectHashMap.put("count", 0);
                return R.ok(stringObjectHashMap);
            }
            request.setBatchId(batches.get(0).getId());
        }
        
        List<Long> productIds = null;
        if (Objects.nonNull(request.getPointId()) || Objects.nonNull(request.getPointType())) {
            productIds = pointProductBindService.queryProductIdsByPidAndPtype(request.getPointId(), request.getPointType());
            // 这里如果没查到就添加一个默认的，否则productIds为空，列表返回全部
            if (CollectionUtils.isEmpty(productIds)) {
                productIds = new ArrayList<>();
                productIds.add(-1L);
            }
        }
        // 分页查询 结果集
        List<ProductNew> productNews = this.queryAllByLimit(offset, limit, request, productIds);
        
        productNews.parallelStream().forEach(item -> {
            
            PointProductBind pointProductBind = pointProductBindService.queryByProductId(item.getId());
            if (Objects.nonNull(pointProductBind)) {
                // 点位查询填充
                if (Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_POINT)) {
                    PointNew pointNew = this.pointNewMapper.selectById(pointProductBind.getPointId());
                    if (Objects.nonNull(pointNew)) {
                        item.setPointId(pointNew.getId().intValue());
                        item.setPointName(pointNew.getName());
                        item.setPointType(PointProductBind.TYPE_POINT);
                    }
                }
                // 仓库查询填充
                if (Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_WAREHOUSE)) {
                    WareHouse wareHouse = warehouseService.getById(pointProductBind.getPointId());
                    if (Objects.nonNull(wareHouse)) {
                        item.setPointId(wareHouse.getId());
                        item.setPointName(wareHouse.getWareHouses());
                        item.setPointType(PointProductBind.TYPE_WAREHOUSE);
                    }
                }
                // 供应商(工厂)查询填充
                if (Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_SUPPLIER)) {
                    Supplier supplier = supplierService.getById(pointProductBind.getPointId());
                    if (Objects.nonNull(supplier)) {
                        item.setPointId(supplier.getId().intValue());
                        item.setPointName(supplier.getName());
                        item.setPointType(PointProductBind.TYPE_SUPPLIER);
                    }
                }
            }
            
            // 产品型号
            if (Objects.nonNull(item.getModelId())) {
                Product product = productService.getBaseMapper().selectById(item.getModelId());
                if (Objects.nonNull(product)) {
                    item.setModelName(product.getName());
                }
            }
            // 批次号
            if (Objects.nonNull(item.getBatchId())) {
                Batch batch = batchService.queryByIdFromDB(item.getBatchId());
                if (Objects.nonNull(batch)) {
                    item.setBatchName(batch.getBatchNo());
                }
            }
            // 供应商
            if (Objects.nonNull(item.getSupplierId())) {
                Supplier supplier = supplierService.getById(item.getSupplierId());
                if (Objects.nonNull(supplier)) {
                    item.setSupplierName(supplier.getName());
                }
            }
            
            //            if (Objects.nonNull(item.getIotCardId())) {
            //                IotCard iotCard = iotCardService.getById(item.getIotCardId());
            //                if (Objects.nonNull(iotCard)) {
            //                    item.setIotCardName(iotCard.getSn());
            //                }
            //            }
            
            //            if (Objects.nonNull(item.getCameraId())) {
            //                Camera camera = cameraService.getById(item.getCameraId());
            //                if (Objects.nonNull(camera)) {
            //                    item.setCameraSerialNum(camera.getSerialNum());
            //                }
            //            }
            
            //            ColorCard colorCard = colorCardService.getById(item.getColor());
            //            if (Objects.nonNull(colorCard)) {
            //                item.setColorName(colorCard.getName());
            //            }
            //            List<OssUrlVo> accessoryPackaging = getOssUrlVoList(item.getId(), File.FILE_TYPE_PRODUCT_ACCESSORY_PACKAGING);
            //            List<OssUrlVo> outerPackaging = getOssUrlVoList(item.getId(), File.FILE_TYPE_PRODUCT_OUTER_PACKAGING);
            //            List<OssUrlVo> qualityInspection = getOssUrlVoList(item.getId(), File.FILE_TYPE_PRODUCT_QUALITY_INSPECTION);
            //            item.setAccessoryPackagingFileList(accessoryPackaging);
            //            item.setOuterPackagingFileList(outerPackaging);
            //            item.setQualityInspectionFileList(qualityInspection);
        });
        
        Integer count = this.count(offset, limit, request, productIds);
        
        HashMap<String, Object> stringObjectHashMap = new HashMap<>(2);
        stringObjectHashMap.put("data", productNews);
        stringObjectHashMap.put("count", count);
        return R.ok(stringObjectHashMap);
    }
    
    @Override
    public ProductNew queryByNo(String no) {
        if (Objects.isNull(no)) {
            return null;
        }
        return this.productNewMapper.selectOne(new QueryWrapper<ProductNew>().eq("no", no).eq("del_flag", ProductNew.DEL_NORMAL));
    }
    
    @Override
    public String getStatusName(Integer status) {
        String statusName = "";
        switch (status) {
            case 0:
                statusName = "生产中";
                break;
            case 1:
                statusName = "运输中";
                break;
            case 2:
                statusName = "已收货";
                break;
            case 3:
                statusName = "使用中";
                break;
            case 4:
                statusName = "拆机柜";
                break;
            case 5:
                statusName = "已报废";
                break;
            case 6:
                statusName = "已测试";
                break;
            case 7:
                statusName = "前置检验合格";
                break;
            case 8:
                statusName = "后置检验合格";
                break;
            default:
                statusName = "";
        }
        return statusName;
    }
    
    @Override
    public R delOssFileByName(String name) {
        // 0 表示图片
        return fileService.removeOssOrMinio(name, 0);
    }
    
    @Override
    public R cabinetCompressionStatus(CabinetCompressionQuery cabinetCompressionQuery) {
        log.error("压测 -----> " + cabinetCompressionQuery);
        ProductNew productNew = this.baseMapper.queryByNo(cabinetCompressionQuery.getSn());
        if (Objects.isNull(productNew)) {
            return R.fail(null, "未查询到相关资产编码");
        }
        
        ProductNew productUpdate = new ProductNew();
        productUpdate.setId(productNew.getId());
        productUpdate.setTestResult(cabinetCompressionQuery.getTestStatus());
        productUpdate.setTestStartTime(cabinetCompressionQuery.getTestStartTime());
        productUpdate.setTestEndTime(cabinetCompressionQuery.getTestEndTime());
        productUpdate.setTestMsg(cabinetCompressionQuery.getTestMsg());
        baseMapper.update(productUpdate);
        
        ProductNewTestContent byDb = productNewTestContentService.queryByPid(productNew.getId());
        if (Objects.isNull(byDb)) {
            ProductNewTestContent productNewTestContent = new ProductNewTestContent();
            productNewTestContent.setPid(productNew.getId());
            productNewTestContent.setContent(cabinetCompressionQuery.getTestContent());
            productNewTestContent.setUpdateTime(System.currentTimeMillis());
            productNewTestContent.setCreateTime(System.currentTimeMillis());
            productNewTestContent.setTestContentResult(cabinetCompressionQuery.getTestContentResult());
            productNewTestContentService.insert(productNewTestContent);
            return R.ok();
        }
        
        ProductNewTestContent productNewTestContent = new ProductNewTestContent();
        productNewTestContent.setId(byDb.getId());
        productNewTestContent.setContent(cabinetCompressionQuery.getTestContent());
        productNewTestContent.setTestContentResult(cabinetCompressionQuery.getTestContentResult());
        productNewTestContent.setUpdateTime(System.currentTimeMillis());
        productNewTestContentService.update(productNewTestContent);
        return R.ok();
    }
    
    @Override
    public R cabinetCompressionCheck(String no) {
        ProductNew productNew = this.baseMapper.queryByNo(no);
        if (Objects.isNull(productNew)) {
            return R.ok(false);
        }
        return R.ok(true);
    }
    
    @Override
    public R cabinetCompressionList(Long uid, String sn, Long startTime, Long endTime) {
        Long testStartTimeBeginTime = null;
        Long testStartTimeEndTime = null;
        Long testEndTimeBeginTime = null;
        Long testEndTimeEndTime = null;
        Integer sortType = 1;
        
        if (Objects.nonNull(startTime)) {
            testStartTimeBeginTime = startTime;
            testStartTimeEndTime = startTime + 24L * 3600000;
        }
        
        if (Objects.nonNull(endTime)) {
            sortType = 2;
            testEndTimeBeginTime = endTime;
            testEndTimeEndTime = endTime + 24L * 3600000;
        }
        
        if (Objects.isNull(startTime) && Objects.isNull(endTime) && StringUtils.isBlank(sn)) {
            testStartTimeBeginTime = System.currentTimeMillis() - 72L * 3600000;
            testStartTimeEndTime = System.currentTimeMillis();
        }
        User user = userService.getUserById(uid);
        List<CabinetCompressionVo> list;
        if (Objects.isNull(user)) {
            log.error("用户不存在 uid={}", uid);
            list = baseMapper.cabinetCompressionList(sn, testStartTimeBeginTime, testStartTimeEndTime, testEndTimeBeginTime, testEndTimeEndTime, sortType, null);
            return R.ok(list);
        }
        
        list = baseMapper.cabinetCompressionList(sn, testStartTimeBeginTime, testStartTimeEndTime, testEndTimeBeginTime, testEndTimeEndTime, sortType, user.getThirdId());
        return R.ok(list);
    }
    
    @Override
    public CabinetCompressionContentVo queryProductTestInfo(Long pid) {
        return baseMapper.queryProductTestInfo(pid);
    }
    
    @Override
    public R runFullLoadTest(ApiRequestQuery apiRequestQuery) {
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
            return R.fail(null, null, "参数解析错误");
        }
        if (Objects.equals(compression.getTestStatus(), CompressionQuery.TEST_ING)) {
            CabinetCompressionQuery cabinetCompressionQuery = null;
            try {
                cabinetCompressionQuery = JSON.parseObject(apiRequestQuery.getData(), CabinetCompressionQuery.class);
            } catch (Exception e) {
                log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
                return R.fail(null, null, "参数解析错误");
            }
            return compressing(cabinetCompressionQuery);
        } else if (Objects.equals(compression.getTestStatus(), CompressionQuery.TEST_FAIL) || Objects.equals(compression.getTestStatus(), CompressionQuery.TEST_SUCC)) {
            return compressionEnd(apiRequestQuery);
        } else {
            return R.fail("SYSTEM.0002", "参数不合法");
        }
    }
    
    // 压测1.0 结束
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public R compressionEnd(ApiRequestQuery apiRequestQuery) {
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
            return R.fail(null, null, "参数解析错误");
        }
        
        if (CollectionUtils.isEmpty(compression.getNoList())) {
            return R.fail(null, null, "压测柜机不存在，请核对");
        }
        
        if (StringUtils.isBlank(compression.getCompressionFile())) {
            return R.fail(null, null, "测试文件为空");
        }
        
        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if (Objects.isNull(iotCard)) {
            log.info("未查询到物联网卡信息", compression.getIotCard());
            return R.fail(null, null, "未查询到物联网卡信息");
        }
        
        ArrayList<ProductNew> mainProducts = new ArrayList(1);
        for (String no : compression.getNoList()) {
            ProductNew product = this.queryByNo(no);
            if (Objects.equals(product.getType(), ProductNew.TYPE_M)) {
                mainProducts.add(product);
            }
            
        }
        
        if (!(mainProducts.size() == 1) || Objects.isNull(mainProducts.get(0))) {
            return R.fail(mainProducts, null, "主柜不存在或存在多个，请核对");
        }
        
        AuditProcess byType = auditProcessService.getByType(AuditProcess.TYPE_POST);
        ProductNew mainProduct = mainProducts.get(0);
        // 需要拷贝的值的组件id
        List<Long> copyLong = Arrays.asList(AuditProcessConstans.CAMERA_SN_AUDIT_ENTRY, AuditProcessConstans.CAMERA_IOT_AUDIT_ENTRY, AuditProcessConstans.PRODUCT_IOT_AUDIT_ENTRY,
                AuditProcessConstans.PRODUCT_COLOR_AUDIT_ENTRY, AuditProcessConstans.DOOR_COLOR_AUDIT_ENTRY, AuditProcessConstans.PRODUCT_SURFACE_AUDIT_ENTRY,
                AuditProcessConstans.CAMERA_SN_AUDIT_ENTRY_TOW);
        // 获取主柜需要同步到副柜的值
        List<AuditValue> mainValues = auditValueService.getByPidAndEntryIds(copyLong, mainProduct.getId());
        
        for (String no : compression.getNoList()) {
            ProductNew productOld = this.queryByNo(no);
            if (Objects.isNull(productOld)) {
                log.info("productOld 未查询到资产编码信息:{}", no);
                continue;
            }
            
            ProductNew product = productNewMapper.queryByNo(no);
            if (Objects.isNull(product)) {
                log.info("product 未查询到资产编码信息:{}", no);
                continue;
            }
            
            // ProductNew product = new ProductNew();
            // product.setNo(no);
            product.setTestFile(compression.getCompressionFile());
            product.setTestResult(compression.getTestStatus());
            product.setTestType(compression.getTestType());
            product.setErrorMessage(subStringByBytes(compression.getErrorMessage()));
            product.setTestEndTime(compression.getTestEndTime());
            
            CompressionRecord compressionRecord = compressionRecordMapper.queryCompressionByPid(product.getId());
            if (Objects.isNull(compressionRecord)) {
                log.error("{} Tested Success/Failure Non-Testing Status", no);
                continue;
            }
            compressionRecord.setTestFile(compression.getCompressionFile());
            compressionRecord.setTestResult(compression.getTestStatus());
            compressionRecord.setTestType(compression.getTestType());
            compressionRecord.setErrorMessage(subStringByBytes(compression.getErrorMessage()));
            compressionRecord.setTestEndTime(compression.getTestEndTime());
            compressionRecord.setUpdateTime(System.currentTimeMillis());
            // compressionRecord.setTestBoxFile(compression.getTestBoxFile());
            compressionRecordMapper.updateById(compressionRecord);
            
            // 这里需要将主柜的数据同步到副柜
            // 获取副柜需要同步的值
            // List<AuditValue> productValues = auditValueService.getByPidAndEntryIds(copyLong, product.getId());
            // 更新
            auditValueService.copyValueToTargetValueIsNoll(mainValues, product.getId());
            
            // 更新柜机状态
            Integer status = auditProcessService.getAuditProcessStatus(byType, productOld);
            if (Objects.equals(status, AuditProcessVo.STATUS_FINISHED)) {
                product.setStatus(ProductNewStatusSortConstants.STATUS_POST_DETECTION);
            } else {
                product.setStatus(STATUS_TESTED);
            }
            log.info("{} pressure measurement status: {}   Test Result: {}", product.getNo(), product.getStatus(), product.getTestResult());
            productNewMapper.updateByNoNew(product);
        }
        
        return R.ok();
    }
    
    /**
     * 压测2.0 结果  人机交互
     *
     * @param apiRequestQuery
     * @return
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public R compressionEnd2(ApiRequestQuery apiRequestQuery) {
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
            return R.fail(null, null, "参数解析错误");
        }
        
        if (CollectionUtils.isEmpty(compression.getNoList())) {
            return R.fail(null, null, "压测柜机不存在，请核对");
        }
        
        if (StringUtils.isBlank(compression.getCompressionFile())) {
            return R.fail(null, null, "测试文件为空");
        }
        
        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if (Objects.isNull(iotCard)) {
            return R.fail(null, null, "未查询到物联网卡信息");
        }
        if (Objects.equals(compression.getTestStatus(), CompressionQuery.TEST_FAIL)) {
            compression.setTestStatus(CompressionQuery.ELE_TEST_FAIL);
        } else {
            compression.setTestStatus(CompressionQuery.ELE_TEST_SUCC);
        }
        ArrayList<ProductNew> mainProducts = new ArrayList(1);
        for (String no : compression.getNoList()) {
            ProductNew product = this.queryByNo(no);
            if (Objects.equals(product.getType(), ProductNew.TYPE_M)) {
                mainProducts.add(product);
            }
            
        }
        
        if (!(mainProducts.size() == 1) || Objects.isNull(mainProducts.get(0))) {
            return R.fail(mainProducts, null, "主柜不存在或存在多个，请核对");
        }
        
        AuditProcess byType = auditProcessService.getByType(AuditProcess.TYPE_POST);
        ProductNew mainProduct = mainProducts.get(0);
        // 需要拷贝的值的组件id
        List<Long> copyLong = Arrays.asList(AuditProcessConstans.CAMERA_SN_AUDIT_ENTRY, AuditProcessConstans.CAMERA_IOT_AUDIT_ENTRY, AuditProcessConstans.PRODUCT_IOT_AUDIT_ENTRY,
                AuditProcessConstans.PRODUCT_COLOR_AUDIT_ENTRY, AuditProcessConstans.DOOR_COLOR_AUDIT_ENTRY, AuditProcessConstans.PRODUCT_SURFACE_AUDIT_ENTRY,
                AuditProcessConstans.CAMERA_SN_AUDIT_ENTRY_TOW);
        // 获取主柜需要同步到副柜的值
        List<AuditValue> mainValues = auditValueService.getByPidAndEntryIds(copyLong, mainProduct.getId());
        
        for (String no : compression.getNoList()) {
            ProductNew productOld = this.queryByNo(no);
            if (Objects.isNull(productOld)) {
                continue;
            }
            
            ProductNew product = productNewMapper.queryByNo(no);
            if (Objects.isNull(product)) {
                continue;
            }
            
            // ProductNew product = new ProductNew();
            // product.setNo(no);
            // product.setTestFile(compression.getCompressionFile());
            // todo
            
            product.setTestResult(compression.getTestStatus());
            product.setTestType(compression.getTestType());
            product.setErrorMessage(subStringByBytes(compression.getErrorMessage()));
            product.setTestEndTime(compression.getTestEndTime());
            
            CompressionRecord compressionRecord = compressionRecordMapper.queryEleByPid(product.getId());
            if (Objects.isNull(compressionRecord)) {
                continue;
            }
            // compressionRecord.setTestFile(compression.getCompressionFile());
            compressionRecord.setTestResult(compression.getTestStatus());
            compressionRecord.setTestType(compression.getTestType());
            compressionRecord.setErrorMessage(subStringByBytes(compression.getErrorMessage()));
            compressionRecord.setTestEndTime(compression.getTestEndTime());
            compressionRecord.setUpdateTime(System.currentTimeMillis());
            compressionRecord.setTestBoxFile(compression.getTestBoxFile());
            compressionRecordMapper.updateById(compressionRecord);
            // 这里需要将主柜的数据同步到副柜
            // 获取副柜需要同步的值
            // List<AuditValue> productValues = auditValueService.getByPidAndEntryIds(copyLong, product.getId());
            // 更新
            auditValueService.copyValueToTargetValueIsNoll(mainValues, product.getId());
            
            // 更新柜机状态
            Integer status = auditProcessService.getAuditProcessStatus(byType, productOld);
            if (Objects.equals(status, AuditProcessVo.STATUS_FINISHED)) {
                product.setStatus(ProductNewStatusSortConstants.STATUS_POST_DETECTION);
            } else {
                product.setStatus(STATUS_TESTED);
            }
            log.info("{} pressure measurement status: {}   Test Result: {}", product.getNo(), product.getStatus(), product.getTestResult());
            productNewMapper.updateByNoNew(product);
        }
        
        return R.ok();
    }
    
    @Override
    public R queryCabinetIotType(ApiRequestQuery apiRequestQuery) {
        log.info("url: /app/compression/check checkCompression: {}", apiRequestQuery.getData());
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! check error", e);
            return R.fail(null, null, "参数解析错误");
        }
        
        if (CollectionUtils.isEmpty(compression.getNoList())) {
            return R.fail(null, null, "压测柜机不存在，请核对");
        }
        
        List<String> errorNos = new ArrayList<>();
        List<String> errorStatus = new ArrayList<>();
        
        // String mainProductNo = "";
        
        if (Objects.isNull(compression.getIotCard())) {
            return R.fail(null, null, "未上报主柜物联网卡号");
        }
        
        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if (Objects.isNull(iotCard)) {
            return R.fail(null, null, "物联网卡号【" + compression.getIotCard() + "】不存在，请核对");
        }
        
        ArrayList<ProductNew> mainProducts = new ArrayList<>(1);
        
        for (String no : compression.getNoList()) {
            ProductNew product = this.queryByNo(no);
            if (Objects.isNull(product)) {
                errorNos.add(no);
            } else {
                // 统计是否有多主柜
                if (Objects.equals(product.getType(), ProductNew.TYPE_M)) {
                    mainProducts.add(product);
                }
                
                // 统计是否有前置检测未通过柜机
                AuditProcess auditProcess = auditProcessService.getByType(AuditProcess.TYPE_PRE);
                Integer status = auditProcessService.getAuditProcessStatus(auditProcess, product);
                if ((!Objects.equals(status, AuditProcessVo.STATUS_FINISHED) || !Objects.equals(product.getStatus(), ProductNewStatusSortConstants.STATUS_PRE_DETECTION))
                        && !Objects.equals(product.getStatus(), STATUS_TESTED)) {
                    errorStatus.add(product.getNo());
                }
            }
        }
        
        if (CollectionUtils.isNotEmpty(errorNos)) {
            return R.fail(errorNos, null, "资产编码不存在，请核对");
        }
        
        if (!(mainProducts.size() == 1) || Objects.isNull(mainProducts.get(0))) {
            return R.fail(mainProducts, null, "主柜不存在或存在多个，请核对");
        }
        
        if (CollectionUtils.isNotEmpty(errorStatus)) {
            return R.fail(errorStatus, null, "柜机前置检测未通过或非前置检测完成、已测试状态,请核对");
        }
        
        // 更新物联网卡
        ProductNew mainProduct = mainProducts.get(0);
        // auditValueService.biandOrUnbindEntry(AuditProcessConstans.PRODUCT_IOT_AUDIT_ENTRY, iotCard.getSn(), mainProduct.getId());
        log.info("url: /app/compression/check mainProduct.getNo: {}", mainProduct.getNo());
        
        Batch batch = batchService.queryByIdFromDB(mainProduct.getBatchId());
        HashMap<String, Object> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("productNo", Arrays.asList(mainProduct.getNo()));
        stringStringHashMap.put("cabinetType", String.valueOf(batch.getBatteryReplacementCabinetType()));
        return R.ok(stringStringHashMap);
    }
    
    public R compressing(CabinetCompressionQuery cabinetCompressionQuery) {
        ProductNew productNew = this.baseMapper.queryByNo(cabinetCompressionQuery.getSn());
        if (Objects.isNull(productNew)) {
            return R.fail(null, "未查询到相关资产编码");
        }
        ProductNew productUpdate = new ProductNew();
        productUpdate.setId(productNew.getId());
        // 判断当前资产编码是不是 成功或者失败 若是 不修改状态
        if (!Objects.equals(productNew.getTestResult(), CompressionQuery.TEST_FAIL) && !Objects.equals(productNew.getTestResult(), CompressionQuery.TEST_SUCC)) {
            productUpdate.setTestResult(cabinetCompressionQuery.getTestStatus());
        }
        productUpdate.setTestStartTime(cabinetCompressionQuery.getTestStartTime());
        productUpdate.setTestEndTime(cabinetCompressionQuery.getTestEndTime());
        productUpdate.setTestMsg(cabinetCompressionQuery.getTestMsg());
        productNewMapper.updateByConditions(productUpdate);
        
        CompressionRecord compressionRecord = new CompressionRecord();
        compressionRecord.setPid(productNew.getId());
        compressionRecord.setCreateTime(System.currentTimeMillis());
        compressionRecord.setTestResult(cabinetCompressionQuery.getTestStatus());
        compressionRecord.setTestStartTime(cabinetCompressionQuery.getTestStartTime());
        compressionRecord.setTestEndTime(cabinetCompressionQuery.getTestEndTime());
        compressionRecord.setTestMsg(cabinetCompressionQuery.getTestMsg());
        compressionRecordMapper.insert(compressionRecord);
        
        ProductNewTestContent byDb = productNewTestContentService.queryByPid(productNew.getId());
        if (Objects.isNull(byDb)) {
            ProductNewTestContent productNewTestContent = new ProductNewTestContent();
            productNewTestContent.setPid(productNew.getId());
            productNewTestContent.setContent(cabinetCompressionQuery.getTestContent());
            productNewTestContent.setUpdateTime(System.currentTimeMillis());
            productNewTestContent.setCreateTime(System.currentTimeMillis());
            productNewTestContent.setTestContentResult(cabinetCompressionQuery.getTestContentResult());
            productNewTestContentService.insert(productNewTestContent);
            return R.ok();
        }
        
        ProductNewTestContent productNewTestContent = new ProductNewTestContent();
        productNewTestContent.setId(byDb.getId());
        productNewTestContent.setContent(cabinetCompressionQuery.getTestContent());
        productNewTestContent.setTestContentResult(cabinetCompressionQuery.getTestContentResult());
        productNewTestContent.setUpdateTime(System.currentTimeMillis());
        productNewTestContentService.update(productNewTestContent);
        return R.ok();
    }
    
    public R compressing2(CabinetCompressionQuery cabinetCompressionQuery) {
        cabinetCompressionQuery.setTestStatus(CompressionQuery.ELE_TEST_ING);
        ProductNew productNew = this.baseMapper.queryByNo(cabinetCompressionQuery.getSn());
        if (Objects.isNull(productNew)) {
            return R.fail(null, "未查询到相关资产编码");
        }
        ProductNew productUpdate = new ProductNew();
        productUpdate.setId(productNew.getId());
        // 判断当前资产编码是不是 成功或者失败 若是 不修改状态
        if (!Objects.equals(productNew.getTestResult(), CompressionQuery.TEST_FAIL) && !Objects.equals(productNew.getTestResult(), CompressionQuery.TEST_SUCC)) {
            productUpdate.setTestResult(cabinetCompressionQuery.getTestStatus());
        }
        productUpdate.setTestStartTime(cabinetCompressionQuery.getTestStartTime());
        productUpdate.setTestEndTime(cabinetCompressionQuery.getTestEndTime());
        productUpdate.setTestMsg(cabinetCompressionQuery.getTestMsg());
        productNewMapper.updateByConditions(productUpdate);
        
        CompressionRecord compressionRecord = new CompressionRecord();
        compressionRecord.setPid(productNew.getId());
        compressionRecord.setCreateTime(System.currentTimeMillis());
        compressionRecord.setTestResult(cabinetCompressionQuery.getTestStatus());
        compressionRecord.setTestStartTime(cabinetCompressionQuery.getTestStartTime());
        compressionRecord.setTestEndTime(cabinetCompressionQuery.getTestEndTime());
        compressionRecord.setTestMsg(cabinetCompressionQuery.getTestMsg());
        compressionRecordMapper.insert(compressionRecord);
        
        ProductNewTestContent byDb = productNewTestContentService.queryByPid(productNew.getId());
        if (Objects.isNull(byDb)) {
            ProductNewTestContent productNewTestContent = new ProductNewTestContent();
            productNewTestContent.setPid(productNew.getId());
            productNewTestContent.setContent(cabinetCompressionQuery.getTestContent());
            productNewTestContent.setUpdateTime(System.currentTimeMillis());
            productNewTestContent.setCreateTime(System.currentTimeMillis());
            productNewTestContent.setTestContentResult(cabinetCompressionQuery.getTestContentResult());
            productNewTestContentService.insert(productNewTestContent);
            return R.ok();
        }
        
        ProductNewTestContent productNewTestContent = new ProductNewTestContent();
        productNewTestContent.setId(byDb.getId());
        productNewTestContent.setContent(cabinetCompressionQuery.getTestContent());
        productNewTestContent.setTestContentResult(cabinetCompressionQuery.getTestContentResult());
        productNewTestContent.setUpdateTime(System.currentTimeMillis());
        productNewTestContentService.update(productNewTestContent);
        return R.ok();
    }
    
    @Override
    public R queryDeviceMessage(String no) {
        DeviceMessageVo deviceMessageVo = productNewMapper.queryDeviceMessage(no);
        if (Objects.isNull(deviceMessageVo)) {
            return R.fail(null, "00000", "柜机资产编码不存在，请核对");
        }
        if (StringUtils.isBlank(deviceMessageVo.getDeviceName()) || StringUtils.isBlank(deviceMessageVo.getProductKey())) {
            return R.fail(null, "00000", "资产编码对应的三元组信息不全");
        }
      
        ProductNew productNew = productNewMapper.selectById(deviceMessageVo.getProductId());
        Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(batch)) {
            return R.fail("批次号选择有误，请检查");
        }
        // 查询 华为云/阿里云/TCP ds
        String secret = getGetDeviceSecret(batch, deviceMessageVo);
        
        deviceMessageVo.setDeviceSecret(secret);
        return R.ok(deviceMessageVo);
    }
    
    private String getGetDeviceSecret(Batch batch, DeviceMessageVo deviceMessageVo) {
        if (ALIYUN_SaaS_ELECTRIC_SWAP_CABINET.equals(batch.getBatteryReplacementCabinetType()) || API_ELECTRIC_SWAP_CABINET.equals(batch.getBatteryReplacementCabinetType())) {
            QueryDeviceDetailResult queryDeviceDetailResult = registerDeviceService.queryDeviceDetail(deviceMessageVo.getProductKey(), deviceMessageVo.getDeviceName());
            return queryDeviceDetailResult.getDeviceSecret();
        }
        
        if (HUAWEI_CLOUD_SaaS.equals(batch.getBatteryReplacementCabinetType())) {
            ShowDeviceResponse showDeviceResponse = deviceSolutionUtil.queryDeviceDetail(deviceMessageVo.getProductKey(), deviceMessageVo.getDeviceName());
            String secret = "";
            
            if (Objects.nonNull(showDeviceResponse) && Objects.nonNull(showDeviceResponse.getAuthInfo())) {
                secret = showDeviceResponse.getAuthInfo().getSecret();
            } else {
                secret = null;
            }
            return secret;
        }
        
        if (TCP_ELECTRIC_SWAP_CABINET.equals(batch.getBatteryReplacementCabinetType())) {
            return deviceMessageVo.getDeviceName();
        }
        return null;
    }
    
    
    @Override
    public R getDeviceMessage(String no) {
        DeviceMessageVo deviceMessageVo = productNewMapper.queryDeviceMessage(no);
        if (Objects.isNull(deviceMessageVo)) {
            return R.fail(null, "00000", "柜机资产编码不存在，请核对");
        }
        if (StringUtils.isBlank(deviceMessageVo.getDeviceName()) || StringUtils.isBlank(deviceMessageVo.getProductKey())) {
            return R.fail(null, "00000", "资产编码对应的三元组信息不全");
        }
        
        ProductNew productNew = productNewMapper.selectById(deviceMessageVo.getProductId());
        Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(batch)) {
            return R.fail("批次号选择有误，请检查");
        }
        // 查询 华为云/阿里云/TCP ds
        String secret = getGetDeviceSecret(batch, deviceMessageVo);
        
        deviceMessageVo.setDeviceSecret(secret);
        return R.ok(deviceMessageVo);
    }
    
    
    @Override
    public R updateUsedStatus(String no, String cpuSerialNum) {
        DeviceMessageVo deviceMessageVo = productNewMapper.queryDeviceMessage(no);
        if (Objects.isNull(deviceMessageVo)) {
            return R.fail(null, "00000", "柜机资产编码不存在，请核对");
        }
        if (StringUtils.isBlank(deviceMessageVo.getDeviceName()) || StringUtils.isBlank(deviceMessageVo.getProductKey())) {
            return R.fail(null, "00000", "资产编码对应的三元组信息不全");
        }
        if (Objects.equals(deviceMessageVo.getIsUse(), ProductNew.IS_USE)) {
            return R.fail(null, "00000", "柜机对应三元组已使用");
        }
        if (StringUtils.isNotEmpty(deviceMessageVo.getCpuSerialNum()) && !Objects.equals(deviceMessageVo.getCpuSerialNum(), cpuSerialNum)) {
            return R.fail(null, "00000", "拉取测试项失败，资产编码对应三元已绑定其他工控机");
        }
        return R.ok(productNewMapper.updateUsedStatusByNo(no, cpuSerialNum, System.currentTimeMillis()));
    }
    
    @Override
    public R runFullLoadTest2(ApiRequestQuery apiRequestQuery) {
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
            return R.fail(null, null, "参数解析错误");
        }
        if (Objects.equals(compression.getTestStatus(), CompressionQuery.TEST_ING)) {
            CabinetCompressionQuery cabinetCompressionQuery = null;
            try {
                cabinetCompressionQuery = JSON.parseObject(apiRequestQuery.getData(), CabinetCompressionQuery.class);
            } catch (Exception e) {
                log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
                return R.fail(null, null, "参数解析错误");
            }
            return compressing2(cabinetCompressionQuery);
        } else if (Objects.equals(compression.getTestStatus(), CompressionQuery.TEST_FAIL) || Objects.equals(compression.getTestStatus(), CompressionQuery.TEST_SUCC)) {
            
            return compressionEnd2(apiRequestQuery);
        } else {
            return R.fail("SYSTEM.0002", "参数不合法");
        }
    }
    
    @Override
    public R updateProductNewStatus(ProductNewQuery productNewQuery) {
        
        List<String> sns = StrUtil.split(productNewQuery.getNos(), "\\r?\\n", 0, true, true);
        if (CollectionUtil.isEmpty(sns)) {
            return R.fail("SYSTEM.0002", "参数不合法");
        }
        if (sns.size() > 20) {
            return R.fail("SYSTEM.0002", "柜机编码数不能超过20个");
        }
        List<ProductNew> beforeModification = productNewMapper.selectList(
                new LambdaQueryWrapper<ProductNew>().in(ProductNew::getNo, sns).eq(ProductNew::getDelFlag, ProductNew.DEL_NORMAL));
        List<Long> ids = beforeModification.stream().map(ProductNew::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(ids)) {
            return R.fail("SYSTEM.0002", "参数不合法");
        }
        
        ProductNew updateProductNew = new ProductNew();
        updateProductNew.setStatus(productNewQuery.getStatus());
        updateProductNew.setUpdateTime(System.currentTimeMillis());
        
        if (Objects.equals(updateProductNew.getStatus(), STATUS_PRODUCTION)) {
            productNewMapper.clearTestResult(ids, productNewQuery.getStatus());
            return R.ok(productNewMapper.updateByConditions(updateProductNew));
        }else if (Objects.equals(updateProductNew.getStatus(), STATUS_TESTED)){
            productNewMapper.updateTestResultFromBatch(ids, productNewQuery.getStatus());
        }else{
            return R.fail("SYSTEM.0002", "参数不合法");
        }
        
        return null;
    }
    
    @Override
    public R unbundledUsedStatus(String sn) {
        DeviceMessageVo deviceMessageVo = productNewMapper.queryDeviceMessage(sn);
        if (Objects.isNull(deviceMessageVo)) {
            return R.fail(null, "00000", "柜机资产编码不存在，请核对");
        }
        if (StringUtils.isBlank(deviceMessageVo.getDeviceName()) || StringUtils.isBlank(deviceMessageVo.getProductKey())) {
            return R.fail(null, "00000", "资产编码对应的三元组信息不全");
        }
        if (!Objects.equals(deviceMessageVo.getIsUse(), ProductNew.IS_USE)) {
            return R.ok();
        }
        return R.ok(productNewMapper.unbundledUsedStatus(sn, System.currentTimeMillis()));
    }
}
