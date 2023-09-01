package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiliulou.afterserver.constant.CacheConstants;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.BatchMapper;
import com.xiliulou.afterserver.mapper.DeviceApplyCounterMapper;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.vo.OrderBatchVo;
import com.xiliulou.cache.redis.RedisService;
import com.xiliulou.core.json.JsonUtil;
import com.xiliulou.iot.service.RegisterDeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

/**
 * (Batch)表服务实现类
 *
 * @author Hardy
 * @since 2021-08-16 15:50:17
 */
@Service("batchService")
@Slf4j
public class BatchServiceImpl implements BatchService {
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
    public static final String PRODUCT_KEY = "a1mqS72fHNi";
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
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    @Override
    public List<Batch> queryAllByLimit(String batchNo,int offset, int limit, Long modelId, Long supplierId) {
        return this.batchMapper.queryAllByLimit(batchNo,offset, limit,modelId , supplierId);
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

        List<Batch> batchOlds = queryByName(batch.getBatchNo());
        if(CollectionUtils.isNotEmpty(batchOlds)){
            return R.fail("批次号已存在");
        }
        Product product = productService.getBaseMapper().selectById(batch.getModelId());
        if (Objects.isNull(product)) {
            return R.fail("产品型号有误，请检查");
        }
        if(Objects.isNull(product.getCode())){
            return R.fail("产品型号编码为空,请重新选择");
        }

        if(Objects.isNull(batch.getProductNum()) || batch.getProductNum() <= 0){
            return R.fail("请传入正确的产品数量");
        }

        Supplier supplier = supplierService.getById(batch.getSupplierId());
        if (Objects.isNull(supplier)) {
            return R.fail("供应商选择有误，请检查");
        }

        if(Objects.isNull(supplier.getCode())){
            return R.fail("供应商编码为空,请重新选择");
        }

        if(Objects.isNull(batch.getType())){
            return R.fail("请输入批次类型");
        }

        batch.setCreateTime(System.currentTimeMillis());
        batch.setUpdateTime(System.currentTimeMillis());
        Batch insert = this.insert(batch);

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
        if(Objects.nonNull(productNew.getType())){
            codeStr.append(productNew.getType());
        }
        productNew.setCode(codeStr.toString());
        DeviceApplyCounter deviceApplyCounter=new DeviceApplyCounter();
        //有屏无屏
        String screen="";
        if(Objects.isNull(product.getHasScreen())||Objects.equals(product.HAS_SCREEN,product.getHasScreen())){
            deviceApplyCounter.setType("H");
            screen="S";
        }else{
            deviceApplyCounter.setType("N");
            screen="N";
        }
        //电柜仓数
        Integer boxNumber=0;
        if (Objects.nonNull(product.getBoxNumber())){
            boxNumber=product.getBoxNumber();
        }
        //消防类型
        String fireFightStr="";
        if(Objects.equals(product.getFireFightingType(),0)){
            fireFightStr="W";
        }else{
            fireFightStr="A";
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyMMdd");
        String dateString = formatter.format(new Date());
        deviceApplyCounter.setDate(dateString);
        Integer serialNum = productNewMapper.queryMaxSerialNum(codeStr.toString());
        if(Objects.isNull(serialNum)){
            serialNum = 0;
        }
        DeviceApplyCounter cabinet=new DeviceApplyCounter();
        cabinet.setType("CABINETSN");
        cabinet.setDate(dateString);
        String cabinetSn=COMPANY_NAME+String.format("%02d", boxNumber)+screen+fireFightStr+dateString+"0";
        Set<String> deviceNames=new HashSet<String>();
        for (int i = 0; i < productNew.getProductCount(); i++) {
            serialNum++;
            String serialNumStr = String.format("%04d", serialNum);
            StringBuilder sb = new StringBuilder();
            sb.append(product.getCode()).append("-");
            sb.append(supplier.getCode()).append(batch.getBatchNo())
                    .append(serialNumStr);
            if(Objects.nonNull(productNew.getType())){
                sb.append(productNew.getType());
            }
            productNew.setSerialNum(serialNumStr);
            productNew.setNo(sb.toString());
            productNew.setCreateTime(System.currentTimeMillis());
            productNew.setUpdateTime(System.currentTimeMillis());
            productNew.setDelFlag(ProductNew.DEL_NORMAL);
            if(Objects.equals(product.getProductSeries(),3)){
                DeviceApplyCounter counter = deviceApplyCounterMapper.queryByDateAndType(deviceApplyCounter);
                if(Objects.isNull(counter)){
                    deviceApplyCounter.setCount(1L);
                }else{
                    deviceApplyCounter.setCount(counter.getCount()+1);
                }
                deviceApplyCounterMapper.insertOrUpdate(deviceApplyCounter);
                String deviceName = deviceApplyCounter.getType() + deviceApplyCounter.getDate() + String.format("%05d", deviceApplyCounter.getCount());
                productNew.setDeviceName(deviceName);
                productNew.setProductKey(PRODUCT_KEY);
                productNew.setIsUse(ProductNew.NOT_USE);
                //查询出厂序号
                DeviceApplyCounter snCounter = deviceApplyCounterMapper.queryByDateAndType(cabinet);
                if(Objects.isNull(snCounter)){
                    cabinet.setCount(1L);
                }else{
                    cabinet.setCount(snCounter.getCount()+1);
                }
                deviceApplyCounterMapper.insertOrUpdate(cabinet);
                productNew.setCabinetSn(cabinetSn+String.format("%04d", cabinet.getCount()));
                deviceNames.add(deviceName);
            }
            productNewMapper.insertOne(productNew);
            PointProductBind bind = new PointProductBind();
            bind.setPointId(batch.getSupplierId());
            bind.setProductId(productNew.getId());
            bind.setPointType(PointProductBind.TYPE_SUPPLIER);
            pointProductBindService.insert(bind);
        }
        //如果是换电柜，则自动维护三元组
        if(Objects.equals(product.getProductSeries(),3)&&deviceNames.size()>0){
            Long applyId = registerDeviceService.batchCheckDeviceNames(PRODUCT_KEY, deviceNames);
            log.info("batch check finished:deviceNames={} applyId={} ", deviceNames,applyId);
            if (Objects.nonNull(applyId)){
                boolean b = registerDeviceService.batchRegisterDeviceWithApplyId(PRODUCT_KEY, applyId);
                log.info("batch register finished:result={} applyId={} ", b,applyId);
                //注册失败则提示
                if (!b){
                    throw  new CustomBusinessException("注册三元组失败，请重新生成批次");
                }
            }
        }

        return R.ok();
    }

    @Override
    public R delOne(Long id) {
        List<ProductNew> list = productNewService.queryByBatch(id);
        if(CollectionUtils.isNotEmpty(list)){
            return R.fail("删除失败,请删除产品列表中关联的数据");
        }

        this.deleteById(id);
        return R.ok();
    }

    @Override
    public R queryByfactory(Long offset, Long size) {
        Long uid = SecurityUtils.getUid();
        if(Objects.isNull(uid)){
            return R.fail("未查询到相关用户");
        }

        User user = userService.getUserById(uid);
        if(Objects.isNull(user)){
            return R.fail("未查询到相关用户");
        }

        Supplier supplier = supplierService.getById(user.getThirdId());
        if(Objects.isNull(supplier)){
            return R.fail("用户未绑定工厂，请联系管理员");
        }
        Page page = PageUtil.getPage(offset, size);
        page = batchMapper.selectPage(page, new QueryWrapper<Batch>().eq("supplier_id", user.getThirdId()).orderByDesc("create_time"));

        List<Batch> batchList = page.getRecords();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        List<OrderBatchVo> data = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(batchList)){
            batchList.stream().forEach(item -> {
                OrderBatchVo orderBatchVo = new OrderBatchVo();
                orderBatchVo.setId(item.getId());
                orderBatchVo.setBatchNo(item.getBatchNo());
                orderBatchVo.setProductNum(item.getProductNum());
                orderBatchVo.setRemarks(StringUtils.isBlank(item.getRemarks()) ? "暂无" : item.getRemarks());
                orderBatchVo.setCreateTime(sdf.format(new Date(item.getCreateTime())));
                Product product = productService.getById(item.getModelId());
                if(Objects.nonNull(product)){
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
