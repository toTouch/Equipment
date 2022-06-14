package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.constant.AuditProcessConstans;
import com.xiliulou.afterserver.constant.CommonConstants;
import com.xiliulou.afterserver.constant.ProductNewStatusSortConstants;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.mapper.PointProductBindMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DataUtil;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.ApiRequestQuery;
import com.xiliulou.afterserver.web.query.CompressionQuery;
import com.xiliulou.afterserver.web.query.ProductNewDetailsQuery;
import com.xiliulou.afterserver.web.query.ProductNewQuery;
import com.xiliulou.afterserver.web.vo.*;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.storage.config.StorageConfig;
import com.xiliulou.storage.service.impl.AliyunOssService;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
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
    public List<ProductNew> queryAllByLimit(int offset, int limit, String no, Long modelId, Long startTime, Long endTime, List<Long> list) {
        return this.productNewMapper.queryAllByLimit(offset, limit, no, modelId, startTime, endTime, list);
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
            sb.append(supplier.getCode()).append(batch.getBatchNo())
                    .append(serialNumStr);
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
        //这里状态改成已收货 位置要发生改变 利用发货日志表查询
        if (Objects.equals(3, query.getStatus())) {
            DeliverLog deliverLog = deliverLogService.getBaseMapper().selectOne(
                    new QueryWrapper<DeliverLog>().eq("product_id", query.getId()));

            if (Objects.nonNull(deliverLog)) {
                Deliver deliver = deliverService.getById(deliverLog.getId());
                if (Objects.nonNull(deliver)) {
                    Integer destinationType = deliver.getDestinationType();
                    if (Objects.equals(destinationType, 1)) {
                        PointNew pointNew = pointNewMapper.selectOne(
                                new QueryWrapper<PointNew>().eq("name", deliver.getDestination()));
                        if (Objects.nonNull(pointNew)) {
                            this.bindPoint(query.getId(), pointNew.getId(), 1);
                        }
                    }
                    if (Objects.equals(destinationType, 2)) {
                        WareHouse wareHouse = warehouseService.getBaseMapper().selectOne(
                                new QueryWrapper<WareHouse>().eq("ware_houses", deliver.getDestination()));
                        if (Objects.nonNull(wareHouse)) {
                            this.bindPoint(query.getId(), new Long(wareHouse.getId()), 2);
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
        Boolean aBoolean = this.deleteById(id);
        if (aBoolean) {
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
    public Integer count(String no, Long modelId, Long startTime, Long endTime, List<Long> list) {
        return this.productNewMapper.countProduct(no, modelId, startTime, endTime, list);
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

        PointProductBind pointProductBind = pointProductBindMapper
                .selectOne(new QueryWrapper<PointProductBind>()
                        .eq("product_id", productId));

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
        CompressionQuery compression = null;
        try {
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        } catch (Exception e) {
            log.error("COMPRESSION PROPERTY CAST ERROR! check error", e);
            return R.fail(null, null, "参数解析错误");
        }

        if (CollectionUtils.isNotEmpty(compression.getNoList())) {
            return R.fail(null, null, "压测柜机不存在，请核对");
        }

        List<String> errorNos = new ArrayList<>();
        List<String> errorStatus = new ArrayList<>();

        //String mainProductNo = "";

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
                //统计是否有多主柜
                if (Objects.equals(product.getType(), ProductNew.TYPE_M)) {
                    mainProducts.add(product);
                }

                //统计是否有前置检测未通过柜机
                AuditProcess auditProcess = auditProcessService.getByType(AuditProcess.TYPE_PRE);
                Integer status = auditProcessService.getAuditProcessStatus(auditProcess, product);
                if(!Objects.equals(status, AuditProcessVo.STATUS_FINISHED)
                        || !Objects.equals(product.getStatus(), ProductNewStatusSortConstants.STATUS_PRE_DETECTION)){
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

        if(CollectionUtils.isNotEmpty(errorStatus)) {
            return R.fail(errorStatus, null, "柜机前置检测未通过或非前置检测完成状态,请核对");
        }

        //更新物联网卡
        ProductNew mainProduct = mainProducts.get(0);
        auditValueService.biandOrUnbindEntry(AuditProcessConstans.PRODUCT_IOT_AUDIT_ENTRY, iotCard.getSn(), mainProduct.getId());
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

        if (CollectionUtils.isNotEmpty(compression.getNoList())) {
            return R.fail(null, null, "压测柜机不存在，请核对");
        }

        if (StringUtils.isBlank(compression.getCompressionFile())) {
            return R.fail(null, null, "测试文件为空");
        }

        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if (Objects.isNull(iotCard)) {
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

        for (String no : compression.getNoList()) {
            ProductNew productOld = this.queryByNo(no);
            if (Objects.nonNull(productOld)) {
                continue;
            }

            ProductNew product = new ProductNew();
            product.setNo(no);
            product.setTestFile(compression.getCompressionFile());
            product.setTestResult(1);

            //更新物联网卡
            auditValueService.biandOrUnbindEntry(AuditProcessConstans.PRODUCT_IOT_AUDIT_ENTRY, iotCard.getSn(), productOld.getId());
            //更新柜机状态
            Integer status = auditProcessService.getAuditProcessStatus(byType, productOld);
            if(Objects.equals(status, AuditProcessVo.STATUS_FINISHED)){
                product.setStatus(ProductNewStatusSortConstants.STATUS_POST_DETECTION);
            } else {
                product.setStatus(ProductNewStatusSortConstants.STATUS_TESTED);
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
    public R queryByBatchAndSupplier(Long batchId, Long offset, Long size) {
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
        page = productNewMapper.selectPage(page, new QueryWrapper<ProductNew>().eq("batch_id", batchId)
                .eq("supplier_id", user.getThirdId())
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
            return R.fail("未查询到相关用户");
        }

        if (Objects.isNull(productNew)) {
            log.error("QUERY PROCESS INFO ERROR! not found no! no={}", no);
            return R.fail("资产编码不存在");
        }

        if (!Objects.equals(productNew.getSupplierId(), user.getThirdId())) {
            log.error("QUERY PROCESS INFO ERROR! current user inconsistent  factory! supplierId={}, userThirdId", productNew.getSupplierId(), user.getThirdId());
            return R.fail(null, "柜机厂家与登录厂家不一致，请重新登陆");
        }

        Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(batch)) {
            log.error("QUERY PROCESS INFO ERROR! not found batch! batch={}", productNew.getBatchId());
            return R.fail(null, "柜机未绑定批次，请重新登陆");
        }

        SimpleDateFormat simp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<AuditProcessVo> voList = new ArrayList<>();
        ProductNewProcessInfoVo vo = new ProductNewProcessInfoVo();
        vo.setId(productNew.getId());
        vo.setNo(productNew.getNo());
        vo.setBatchId(productNew.getBatchId());
        vo.setBatchNo(batch.getBatchNo());
        vo.setAuditProcessList(voList);
        vo.setCreateTime(simp.format(new Date(productNew.getCreateTime())));
        vo.setProductStatus(getStatusName(productNew.getStatus()));

        List<AuditProcess> auditProcessList = auditProcessService.getBaseMapper().selectList(new QueryWrapper<AuditProcess>().orderByAsc("id"));
        //如果搜索页面配置为空，则只获取压测状态，发货状态随压测状态改变
        if (CollectionUtils.isEmpty(auditProcessList)) {
            AuditProcessVo testVo = auditProcessService.createTestAuditProcessVo();
            testVo.setStatus(ProductNew.TEST_RESULT_SUCCESS.equals(productNew.getTestResult()) ? AuditProcessVo.STATUS_FINISHED : AuditProcessVo.STATUS_EXECUTING);
            AuditProcessVo deliverVo = auditProcessService.createDeliverAuditProcessVo();
            deliverVo.setStatus(AuditProcessVo.STATUS_FINISHED.equals(testVo.getStatus()) ? ProductNewProcessInfoVo.STATUS_FINISHED : ProductNewProcessInfoVo.STATUS_UN_FINISHED);
            voList.add(testVo);
            voList.add(deliverVo);

            vo.setAuditProcessList(voList);
            return R.ok(vo);
        }

        //统计流程状态的类型共有几种
        Set<Integer> statusSet = new HashSet<>(3);
        //获取所有流程状态
        auditProcessList.parallelStream().forEachOrdered(item -> {
            Integer status = auditProcessService.getAuditProcessStatus(item, productNew);
            AuditProcessVo auditProcessVo = new AuditProcessVo();
            BeanUtils.copyProperties(item, auditProcessVo);
            auditProcessVo.setStatus(status);

            voList.add(auditProcessVo);
            statusSet.add(status);
        });

        //获取压测状态
        AuditProcessVo testAuditProcessVo = auditProcessService.createTestAuditProcessVo();
        //获取当前压测状态，
        if(Objects.equals(productNew.getTestResult(), ProductNew.TEST_RESULT_SUCCESS)) {
            //如果测试完成 那么压测状态一定是绿灯
            testAuditProcessVo.setStatus(AuditProcessVo.STATUS_FINISHED);
        } else {
            //如果测试未完成，需要看前置检查是否完成：完成则压测正在执行，未完则置灰
            voList.forEach(item -> {
                if(Objects.equals(AuditProcess.TYPE_PRE, item.getType())) {
                    testAuditProcessVo.setStatus(Objects.equals(item.getStatus(), AuditProcessVo.STATUS_FINISHED)? AuditProcessVo.STATUS_EXECUTING : AuditProcessVo.STATUS_UNFINISHED);
                }
            });
        }
        voList.add(1,testAuditProcessVo);
        statusSet.add(testAuditProcessVo.getStatus());

        AuditProcessVo deliverVo = auditProcessService.createDeliverAuditProcessVo();
        if (statusSet.size() > 1) {
            //如果状态有多个，那么发货状态一定置灰
            deliverVo.setStatus(ProductNewProcessInfoVo.STATUS_UN_FINISHED);
        } else if (statusSet.contains(AuditProcessVo.STATUS_UNFINISHED)) {
            //流程调整 如果流程中全部为未完成，那么前置检测状态应为正在执行
            auditProcessService.processStatusAdjustment(voList);
            deliverVo.setStatus(ProductNewProcessInfoVo.STATUS_UN_FINISHED);
        } else {
            deliverVo.setStatus(ProductNewProcessInfoVo.STATUS_FINISHED);
        }
        voList.add(deliverVo);

        return R.ok(vo);
    }


    @Override
    public BaseMapper<ProductNew> getBaseMapper() {
        return this.productNewMapper;
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
    public R checkProperty(String no) {
        ProductNew productNew = this.queryByNo(no);
        if (Objects.isNull(productNew)) {
            return R.fail(null, "10001", "柜机资产编码不存在，请核对");
        }



        Batch productBatch = batchService.queryByIdFromDB(productNew.getBatchId());
        if (Objects.isNull(productBatch)) {
            return R.fail(null, null, "未查询到柜机批次，请联系管理员");
        }


        Product product = productService.getById(productNew.getModelId());
        if (Objects.isNull(product)) {
            return R.fail(null, "10001", "未查询到柜机类型，请联系管理员");
        }



        SimpleDateFormat sim = new SimpleDateFormat("hh:mm");

        DeliverProductNewInfoVo vo = new DeliverProductNewInfoVo();
        vo.setId(productNew.getId());
        vo.setBatchId(productNew.getBatchId());
        vo.setBatchNo(productBatch.getBatchNo());
        vo.setModelName(product.getName());
        vo.setNo(productNew.getNo());
        vo.setStatusName(getStatusName(productNew.getStatus()));
        vo.setInsertTime(sim.format(new Date()));
        return R.fail(vo);
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
    public R pointList(Integer offset, Integer limit, String no, Long modelId, Long pointId, Integer pointType, Long startTime, Long endTime) {
        List<Long> productIds = null;
        if (Objects.nonNull(pointId) || Objects.nonNull(pointType)) {
            productIds = pointProductBindService.queryProductIdsByPidAndPtype(pointId, pointType);
            //这里如果没查到就添加一个默认的，否则productIds为空，列表返回全部
            if (CollectionUtils.isEmpty(productIds)) {
                productIds = new ArrayList<>();
                productIds.add(-1L);
            }
        }

        List<ProductNew> productNews = this.queryAllByLimit(offset, limit, no, modelId, startTime, endTime, productIds);

        productNews.parallelStream().forEach(item -> {

            PointProductBind pointProductBind = pointProductBindService.queryByProductId(item.getId());
            if (Objects.nonNull(pointProductBind)) {
                if (Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_POINT)) {
                    PointNew pointNew = this.pointNewMapper.selectById(pointProductBind.getPointId());
                    if (Objects.nonNull(pointNew)) {
                        item.setPointId(pointNew.getId().intValue());
                        item.setPointName(pointNew.getName());
                        item.setPointType(PointProductBind.TYPE_POINT);
                    }
                }
                if (Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_WAREHOUSE)) {
                    WareHouse wareHouse = warehouseService.getById(pointProductBind.getPointId());
                    if (Objects.nonNull(wareHouse)) {
                        item.setPointId(wareHouse.getId());
                        item.setPointName(wareHouse.getWareHouses());
                        item.setPointType(PointProductBind.TYPE_WAREHOUSE);
                    }
                }
                if (Objects.equals(pointProductBind.getPointType(), PointProductBind.TYPE_SUPPLIER)) {
                    Supplier supplier = supplierService.getById(pointProductBind.getPointId());
                    if (Objects.nonNull(supplier)) {
                        item.setPointId(supplier.getId().intValue());
                        item.setPointName(supplier.getName());
                        item.setPointType(PointProductBind.TYPE_SUPPLIER);
                    }
                }
            }

            if (Objects.nonNull(item.getModelId())) {
                Product product = productService.getBaseMapper().selectById(item.getModelId());
                if (Objects.nonNull(product)) {
                    item.setModelName(product.getName());
                }
            }

            if (Objects.nonNull(item.getBatchId())) {
                Batch batch = batchService.queryByIdFromDB(item.getBatchId());
                if (Objects.nonNull(batch)) {
                    item.setBatchName(batch.getBatchNo());
                }
            }

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

        Integer count = this.count(no, modelId, startTime, endTime, productIds);

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
        return this.productNewMapper.selectOne(new QueryWrapper<ProductNew>().eq("no", no));
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
        }
        return statusName;
    }
}
