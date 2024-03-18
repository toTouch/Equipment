package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.config.ProductConfig;
import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.entity.DeviceApplyCounter;
import com.xiliulou.afterserver.entity.PointProductBind;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductFile;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.BatchMapper;
import com.xiliulou.afterserver.mapper.DeviceApplyCounterMapper;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.PointProductBindService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.DeviceSolutionUtil;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.vo.OrderBatchVo;
import com.xiliulou.iot.entity.response.QueryDeviceDetailResult;
import com.xiliulou.iot.service.RegisterDeviceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.xiliulou.afterserver.entity.Batch.HUAWEI_CLOUD_SaaS;

/**
 * (Batch)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-16 15:50:17
 */
@Service("batchService")
@Slf4j
public class BatchServiceImpl implements BatchService {
    
    static final Pattern ALPHANUMERIC = Pattern.compile("[^a-zA-Z0-9]");
    
    @Autowired
    private DeviceSolutionUtil deviceSolutionUtil;
    
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
    
    @Autowired
    private PointProductBindService pointProductBindService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductConfig productConfig;
    
    public static final String COMPANY_NAME = "BY";
    
    @Autowired
    private RegisterDeviceService registerDeviceService;
    
    @Autowired
    private DeviceApplyCounterMapper deviceApplyCounterMapper;
    
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
     * @param offset     查询起始位置
     * @param limit      查询条数
     * @param notShipped
     * @return 对象列表
     */
    @Override
    public List<Batch> queryAllByLimit(String batchNo, int offset, int limit, Long modelId, Long supplierId, Integer notShipped) {
        return this.batchMapper.queryAllByLimit(batchNo, offset, limit, modelId, supplierId, notShipped);
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
        log.debug("更新批次未发货数量" + batch);
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
    public List<Batch> queryByName(String batchName) {
        return batchMapper.selectList(new QueryWrapper<Batch>().eq("batch_no", batchName));
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R saveBatch(Batch batch) {
        Product product = productService.getBaseMapper().selectById(batch.getModelId());
        // 就是工厂
        Supplier supplier = supplierService.getById(batch.getSupplierId());
        // 校验
        R<String> fail = verifyBeforeCreatingABatch(batch, product, supplier);
        if (fail != null) {
            return fail;
        }
        
        ProductNew productNew = insertBatchAndproductFileAndInitProductNew(batch, product, supplier);
        
        DeviceApplyCounter deviceApplyCounter = new DeviceApplyCounter();
        // 有屏无屏
        String screen = "";
        if (Objects.isNull(product.getHasScreen()) || Objects.equals(Product.HAS_SCREEN, product.getHasScreen())) {
            deviceApplyCounter.setType("H");
            screen = "S";
        } else {
            deviceApplyCounter.setType("N");
            screen = "N";
        }
        // 电柜仓数
        Integer boxNumber = 0;
        if (Objects.nonNull(product.getBoxNumber())) {
            boxNumber = product.getBoxNumber();
        }
        // 消防类型
        String fireFightStr = "";
        if (Objects.equals(product.getFireFightingType(), 0)) {
            fireFightStr = "W";
        } else {
            fireFightStr = "A";
        }
        // 工厂简称
        String co = "";
        if (Objects.nonNull(supplier.getSimpleName())) {
            co = supplier.getSimpleName();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        String dateString = formatter.format(new Date());
        deviceApplyCounter.setDate(dateString);
        Integer serialNum = productNewMapper.queryMaxSerialNum(productNew.getCode());
        if (Objects.isNull(serialNum)) {
            serialNum = 0;
        }
        DeviceApplyCounter cabinet = new DeviceApplyCounter();
        cabinet.setType("CABINETSN");
        cabinet.setDate(dateString);
        String cabinetSn = COMPANY_NAME + String.format("%02d", boxNumber) + screen + fireFightStr + dateString + co;
        Set<String> deviceNames = new HashSet<String>();
        
        // 获取换电柜机柜类型 0:SaaS换电柜 1:api换电柜
        String key = obtainTheCabinetType(batch);
        
        // 去重并校验deviceNames
        List<String> customDeviceNameList = batch.getCustomDeviceNameList();
        deduplicationAndVerificationDeviceNames(batch, customDeviceNameList, product, key);
        
        for (int i = 0; i < productNew.getProductCount(); i++) {
            serialNum++;
            String serialNumStr = String.format("%04d", serialNum);
            String no = initProductNewNo(batch, product, supplier, serialNumStr, productNew);
            productNew.setSerialNum(serialNumStr);
            productNew.setNo(no);
            productNew.setCreateTime(System.currentTimeMillis());
            productNew.setUpdateTime(System.currentTimeMillis());
            productNew.setDelFlag(ProductNew.DEL_NORMAL);
            
            // 换电柜逻辑
            if (Objects.equals(product.getProductSeries(), Product.BATTERY_REPLACEMENT_CABINET)) {
                DeviceApplyCounter counter = deviceApplyCounterMapper.queryByDateAndType(deviceApplyCounter);
                if (Objects.isNull(counter)) {
                    deviceApplyCounter.setCount(1L);
                } else {
                    deviceApplyCounter.setCount(counter.getCount() + 1);
                }
                deviceApplyCounterMapper.insertOrUpdate(deviceApplyCounter);
                String deviceName = "";
                // api换电柜 getBatteryReplacementCabinetType 0:SaaS换电柜 1:api换电柜
                if (CollectionUtils.isNotEmpty(customDeviceNameList)) {
                    deviceName = customDeviceNameList.get(i);
                } else {
                    deviceName = deviceApplyCounter.getType() + deviceApplyCounter.getDate() + String.format("%05d", deviceApplyCounter.getCount());
                    ;
                }
                productNew.setDeviceName(deviceName);
                productNew.setProductKey(key);
                productNew.setIsUse(ProductNew.NOT_USE);
                // 查询出厂序号
                DeviceApplyCounter snCounter = deviceApplyCounterMapper.queryByDateAndType(cabinet);
                
                if (Objects.isNull(snCounter)) {
                    cabinet.setCount(1L);
                } else {
                    cabinet.setCount(snCounter.getCount() + 1);
                }
                deviceApplyCounterMapper.insertOrUpdate(cabinet);
                productNew.setCabinetSn(cabinetSn + String.format("%04d", cabinet.getCount()));
                deviceNames.add(deviceName);
            }
            productNewMapper.insertOne(productNew);
            PointProductBind bind = new PointProductBind();
            bind.setPointId(batch.getSupplierId());
            bind.setProductId(productNew.getId());
            bind.setPointType(PointProductBind.TYPE_SUPPLIER);
            pointProductBindService.insert(bind);
        }
        
        // 不是换电柜，程序结束
        if (!Objects.equals(product.getProductSeries(), Product.BATTERY_REPLACEMENT_CABINET) && deviceNames.isEmpty()) {
            return R.ok();
        }
        
        if (HUAWEI_CLOUD_SaaS.equals(batch.getBatteryReplacementCabinetType())) {
            //20240314 华为云IOT注册限制每秒50次请求 此接口超时时间为30秒
            Pair<Boolean, String> booleanStringPair = deviceSolutionUtil.batchRegisterDevice(deviceNames, key);
            log.info("batch register finished:result={} applyId={} ", booleanStringPair.getLeft(), key);
            if (!booleanStringPair.getLeft()) {
                throw new CustomBusinessException(booleanStringPair.getRight());
            }
            return R.ok();
        }
        
        // 是换电柜则自动维护三元组  批量检查自定义设备名称的合法性
        Long applyId = registerDeviceService.batchCheckDeviceNames(key, deviceNames);
        log.info("batch check finished:deviceNames={} applyId={} ", deviceNames, applyId);
        if (Objects.nonNull(applyId)) {
            // 批量注册设备
            boolean b = registerDeviceService.batchRegisterDeviceWithApplyId(key, applyId);
            log.info("batch register finished:result={} applyId={} ", b, applyId);
            // 注册失败则提示
            if (!b) {
                throw new CustomBusinessException("注册三元组失败，请重新生成批次");
            }
        }
        
        return R.ok();
    }
    
    private String initProductNewNo(Batch batch, Product product, Supplier supplier, String serialNumStr, ProductNew productNew) {
        StringBuilder sb = new StringBuilder();
        sb.append(product.getCode()).append("-");
        sb.append(supplier.getCode()).append(batch.getBatchNo()).append(serialNumStr);
        if (Objects.nonNull(productNew.getType())) {
            sb.append(productNew.getType());
        }
        return String.valueOf(sb);
    }
    
    /**
     * 去重并校验deviceNames <br/> getProductSeries 3:换电柜  getBatteryReplacementCabinetType 0:SaaS换电柜 1:api换电柜
     */
    private void deduplicationAndVerificationDeviceNames(Batch batch, List<String> customDeviceNameList, Product product, String key) {
        if (CollectionUtils.isNotEmpty(customDeviceNameList) && Objects.equals(product.getProductSeries(), 3)) {
            List<String> customDeviceNameDisList = customDeviceNameList.stream().distinct().collect(Collectors.toList());
            if (customDeviceNameList.size() != customDeviceNameDisList.size()) {
                throw new CustomBusinessException("deviceName有重复");
            }
            if (customDeviceNameList.size() != batch.getProductNum()) {
                throw new CustomBusinessException("deviceName数量与产品数量不匹配");
            }
            
            for (String deviceName : customDeviceNameList) {
                if (deviceName.length() < 5 || deviceName.length() > 12) {
                    throw new CustomBusinessException("deviceName长度必须在5-12位间");
                }
                if (isContainNonAlphanumeric(deviceName)) {
                    throw new CustomBusinessException("deviceName仅支持字母和数字");
                }
                
                QueryDeviceDetailResult queryDeviceDetailResult = registerDeviceService.queryDeviceDetail(key, deviceName);
                if (Objects.nonNull(queryDeviceDetailResult)) {
                    throw new CustomBusinessException("deviceName已存在" + deviceName);
                }
            }
            
            // 同批次同型号下未删除(0)deviceName是已否存在
            List<ProductNew> customDeviceName = productNewMapper.selectList(new LambdaQueryWrapper<ProductNew>().eq(ProductNew::getDelFlag, 0)
                    .in(Objects.nonNull(customDeviceNameDisList), ProductNew::getDeviceName, customDeviceNameDisList));
            
            if (CollectionUtils.isNotEmpty(customDeviceName)) {
                // 收集已存在的产品集的deviceName字段
                String deviceNamecollect = customDeviceName.stream().map(ProductNew::getDeviceName).distinct().limit(10).collect(Collectors.joining("、"));
                throw new CustomBusinessException("deviceName已存在" + deviceNamecollect);
            }
        }
    }
    
    private String obtainTheCabinetType(Batch batch) {
        // 获取换电柜机柜类型 0:阿里SaaS换电柜 1:api换电柜 3:华为saas换电柜
        switch (batch.getBatteryReplacementCabinetType()) {
            case 0:
                return productConfig.getKey();
            case 1:
                return productConfig.getApiKey();
            case 3:
                return productConfig.getHuaweiKey();
            default:
                return null;
        }
    }
    
    private ProductNew insertBatchAndproductFileAndInitProductNew(Batch batch, Product product, Supplier supplier) {
        batch.setCreateTime(System.currentTimeMillis());
        batch.setUpdateTime(System.currentTimeMillis());
        batch.setNotShipped(batch.getProductNum());
        Batch insert = this.insert(batch);
        // 附件上传
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
        if (Objects.nonNull(productNew.getType())) {
            codeStr.append(productNew.getType());
        }
        productNew.setCode(codeStr.toString());
        return productNew;
    }
    
    private R<String> verifyBeforeCreatingABatch(Batch batch, Product product, Supplier supplier) {
        // 产品校验
        if (Objects.isNull(product)) {
            return R.fail("产品型号有误，请检查");
        }
        if (Objects.isNull(product.getCode())) {
            return R.fail("产品型号编码为空,请重新选择");
        }
        // 批次号(batchNo)+产品型号(modelId)不可重复
        Batch batchTemp = getByNameAndModeId(batch.getBatchNo(), batch.getModelId());
        // 批次校验
        if (Objects.nonNull(batchTemp)) {
            return R.fail("同批次已有同型号产品已存在");
        }
        
        if (Objects.isNull(batch.getType())) {
            return R.fail("请输入批次类型");
        }
        
        if (Objects.isNull(batch.getProductNum()) || batch.getProductNum() <= 0) {
            return R.fail("请传入正确的产品数量");
        }
        
        // 供应商校验
        if (Objects.isNull(supplier)) {
            return R.fail("供应商选择有误，请检查");
        }
        
        if (Objects.isNull(supplier.getCode())) {
            return R.fail("供应商编码为空,请重新选择");
        }
        return null;
    }
    
    public static boolean isContainNonAlphanumeric(String input) {
        Matcher matcher = ALPHANUMERIC.matcher(input);
        return matcher.find();
    }
    
    @Override
    public Batch getByNameAndModeId(String batchNo, Long modelId) {
        if (StringUtils.isBlank(batchNo) && Objects.isNull(modelId)) {
            return null;
        }
        LambdaQueryWrapper<Batch> wrapper = new LambdaQueryWrapper<Batch>().eq(Batch::getBatchNo, batchNo).eq(Batch::getModelId, modelId);
        return batchMapper.selectOne(wrapper);
    }
    
    @Override
    public List<Batch> ListBatchByProductId(Long productId) {
        LambdaQueryWrapper<Batch> batchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        batchLambdaQueryWrapper.eq(Objects.nonNull(productId), Batch::getModelId, productId);
        return batchMapper.selectList(batchLambdaQueryWrapper);
    }
    
    @Override
    public Integer batchUpdateById(ArrayList<Batch> batches) {
        if (CollectionUtils.isEmpty(batches)) {
            return null;
        }
        return batchMapper.batchUpdateById(batches);
    }
    
    @Override
    public R delOne(Long id) {
        // 删除产品 查这个批次有哪些产品，删批次前要清空产品
        List<ProductNew> list = productNewService.queryByBatch(id);
        if (CollectionUtils.isNotEmpty(list)) {
            return R.fail("删除失败,请删除产品列表中关联的数据");
        }
        
        this.deleteById(id);
        return R.ok();
    }
    
    @Override
    public R queryByfactory(Long offset, Long size, Integer notShipped, String batchNo) {
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
        LambdaQueryWrapper<Batch> batchLambdaQueryWrapper = new LambdaQueryWrapper<>();
        batchLambdaQueryWrapper.like(StringUtils.isNotBlank(batchNo), Batch::getBatchNo, batchNo).gt(Objects.nonNull(notShipped) && notShipped > 0, Batch::getNotShipped, 0)
                .eq(Objects.nonNull(notShipped) && notShipped == 0, Batch::getNotShipped, 0).eq(Objects.nonNull(supplier), Batch::getSupplierId, supplier.getId())
                .orderByDesc(Batch::getCreateTime);
        
        page = batchMapper.selectPage(page, batchLambdaQueryWrapper);
        
        List<Batch> batchList = page.getRecords();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        List<OrderBatchVo> data = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(batchList)) {
            batchList.stream().forEach(item -> {
                OrderBatchVo orderBatchVo = new OrderBatchVo();
                orderBatchVo.setId(item.getId());
                orderBatchVo.setBatchNo(item.getBatchNo());
                orderBatchVo.setProductNum(item.getProductNum());
                orderBatchVo.setRemarks(StringUtils.isBlank(item.getRemarks()) ? "暂无" : item.getRemarks());
                orderBatchVo.setCreateTime(sdf.format(new Date(item.getCreateTime())));
                Product product = productService.getById(item.getModelId());
                if (Objects.nonNull(product)) {
                    orderBatchVo.setModelName(product.getName());
                }
                data.add(orderBatchVo);
            });
        }
        
        Map result = new HashMap(2);
        result.put("data", data);
        result.put("total", page.getTotal());
        return R.ok(result);
    }
}
