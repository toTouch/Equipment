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
import com.xiliulou.afterserver.web.vo.BatchProductNewVo;
import com.xiliulou.afterserver.web.vo.DeliverProductNewInfoVo;
import com.xiliulou.afterserver.web.vo.ProductNewDetailsVo;
import com.xiliulou.core.json.JsonUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    public List<ProductNew> queryAllByLimit(int offset, int limit,String no,Long modelId,Long startTime,Long endTime, Long pointId, Integer pointType) {
        return this.productNewMapper.queryAllByLimit(offset, limit,no,modelId,startTime,endTime, pointId, pointType);
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

        Supplier supplier = supplierService.getById(productNew.getSupplierId());
        if (Objects.isNull(supplier)) {
            return R.fail("供应商选择有误，请检查");
        }
        if(Objects.isNull(supplier.getCode())){
            return R.fail("供应商编码为空,请重新选择");
        }

        Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
        if(Objects.isNull(batch)){
            return R.fail("批次号选择有误，请检查");
        }
        if(Objects.isNull(batch.getBatchNo())){
            return R.fail("批次号为空，请重新选择");
        }

        StringBuilder codeStr = new StringBuilder();
        codeStr.append(product.getCode()).append("-");
        codeStr.append(supplier.getCode()).append(batch.getBatchNo());
        if(Objects.nonNull(productNew.getType())){
            codeStr.append(productNew.getType());
        }
        productNew.setCode(codeStr.toString());

        Integer serialNum = productNewMapper.queryMaxSerialNum(codeStr.toString());
        if(Objects.isNull(serialNum)){
            serialNum = 0;
        }

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
            productNew.setDelFlag(ProductNew.DEL_NORMAL);
            this.insert(productNew);
        }

        return R.ok();
    }




    @Override
    public R putAdminProductNew(ProductNewQuery query) {
        ProductNew productNewOld = this.productNewMapper.queryById(query.getId());
        if(Objects.isNull(productNewOld)){
            return R.fail("未查询到相关柜机信息");
        }

        if(Objects.nonNull(query.getCameraCardId())){
            Camera camera =  cameraService.getById(query.getCameraCardId());
            if(Objects.isNull(camera)){
                return R.fail("未查询摄像头序列号");
            }

            ProductNew productNew = productNewMapper.selectOne(new QueryWrapper<ProductNew>()
                    .eq("camera_id", camera.getId())
                    .eq("del_flag", ProductNew.DEL_NORMAL));

            if(Objects.nonNull(productNew)){
                return R.fail("序列号已绑定到其他产品");
            }
        }


        ProductNew updateProductNew = new ProductNew();
        updateProductNew.setId(query.getId());
        updateProductNew.setExpirationStartTime(query.getExpirationStartTime());
        updateProductNew.setYears(query.getYears());
        updateProductNew.setExpirationEndTime(query.getExpirationEndTime());
        updateProductNew.setStatus(query.getStatus());
        updateProductNew.setUpdateTime(System.currentTimeMillis());
        //这里状态改成已收货 位置要发生改变 利用发货日志表查询
        if(Objects.equals(3, query.getStatus())){
            DeliverLog deliverLog = deliverLogService.getBaseMapper().selectOne(
                    new QueryWrapper<DeliverLog>().eq("product_id", query.getId()));

            if(Objects.nonNull(deliverLog)){
                Deliver deliver = deliverService.getById(deliverLog.getId());
                if(Objects.nonNull(deliver)){
                    Integer destinationType = deliver.getDestinationType();
                    if(Objects.equals(destinationType, 1)){
                        PointNew pointNew = pointNewMapper.selectOne(
                                new QueryWrapper<PointNew>().eq("name", deliver.getDestination()));
                        if(Objects.nonNull(pointNew)){
                            this.bindPoint(query.getId(), pointNew.getId(), 1);
                        }
                    }
                    if(Objects.equals(destinationType, 2)){
                        WareHouse wareHouse = warehouseService.getBaseMapper().selectOne(
                                new QueryWrapper<WareHouse>().eq("ware_houses", deliver.getDestination()));
                        if(Objects.nonNull(wareHouse)){
                            this.bindPoint(query.getId(), new Long(wareHouse.getId()), 2);
                        }
                    }
                    if(Objects.equals(destinationType, 3)){
                        Supplier supplier = supplierService.querySupplierName(deliver.getDestination());
                        if(Objects.nonNull(supplier)){
                            this.bindPoint(query.getId(), supplier.getId(), 3);
                        }
                    }
                }
            }
        }
        updateProductNew.setIotCardId(query.getIotCardId());
        updateProductNew.setCameraId(query.getCameraCardId());
        updateProductNew.setColor(query.getColor());
        updateProductNew.setSurface(query.getSurface());
        updateProductNew.setRemarks(query.getRemarks());

        Integer update = this.update(updateProductNew);
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
        ProductNew productNew = this.productNewMapper.selectOne(eq);
        if(Objects.nonNull(productNew)){
            Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
            if(Objects.nonNull(batch)){
                productNew.setBatchName(batch.getBatchNo());
            }

            Product product = productService.getById(productNew.getModelId());
            if(Objects.nonNull(product)){
                productNew.setModelName(product.getName());
            }

            return productNew;
        }
        return null;
    }

    @Override
    public Integer count(String no,Long modelId,Long startTime,Long endTime, Long pointId) {
        return this.productNewMapper.countProduct(no,modelId,startTime,endTime,pointId);
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
    public R bindPoint(Long productId, Long pointId, Integer pointType) {
        ProductNew productNew = this.queryByIdFromDB(productId);
        if(Objects.isNull(productNew)){
            return R.fail("未查询到相关柜机");
        }
        productNew.setUpdateTime(System.currentTimeMillis());
        this.update(productNew);

        PointProductBind pointProductBind = pointProductBindMapper
                    .selectOne(new QueryWrapper<PointProductBind>()
                        .eq("product_id", productId));

        if(ObjectUtils.isNotNull(pointProductBind)){
            pointProductBindMapper.deleteById(pointProductBind.getId());
        }

        if(Objects.isNull(pointType)){
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
        List<ProductNew> productNewList = productNewMapper.selectList(wrapper);
        return productNewList;
    }

    @Override
    public R checkCompression(ApiRequestQuery apiRequestQuery) {
        CompressionQuery compression = null;
        try{
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        }catch (Exception e){
            log.error("COMPRESSION PROPERTY CAST ERROR! check error", e);
            return R.fail(null, null, "参数解析错误");
        }

        List<String> errorNos = new ArrayList<>();

        String mainProductNo = "";

        if(Objects.isNull(compression.getIotCard())){
            return R.fail(null, null, "未上报主柜物联网卡号");
        }

        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if(Objects.isNull(iotCard)){
            return R.fail(null, null, "物联网卡号【"+compression.getIotCard()+"】不存在，请核对");
        }

        ArrayList<ProductNew> mainProducts = new ArrayList(1);

        if(CollectionUtils.isNotEmpty(compression.getNoList())){
            for(String no : compression.getNoList()){
                ProductNew product = this.queryByNo(no);
                if(Objects.isNull(product)){
                    errorNos.add(no);
                } else {
                    if(Objects.equals(product.getType(), ProductNew.TYPE_M)){
                        mainProducts.add(product);
                    }
                }
            }

            if(CollectionUtils.isNotEmpty(errorNos)){
                return R.fail(errorNos, null, "资产编码不存在，请核对");
            }

            if(!(mainProducts.size() == 1) || Objects.isNull(mainProducts.get(0))){
                return R.fail(mainProducts, null, "主柜不存在或存在多个，请核对");
            }

            ProductNew mainProduct = mainProducts.get(0);
            mainProductNo = mainProduct.getNo();
            if(!Objects.equals(mainProduct.getIotCardId(), iotCard.getId())){
                return R.fail(null, null,"主柜绑定物联网卡号与上报物联网卡号不一致，请修改");
            }

            if(!Objects.equals(iotCard.getBatchId(), mainProduct.getBatchId())){
                return R.fail(null, null, "主柜批次与物联网卡批次不一致，请核对");
            }

        }

        return R.ok(Arrays.asList(mainProductNo));
    }



    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public R successCompression(ApiRequestQuery apiRequestQuery) {
        CompressionQuery compression = null;
        try{
            compression = JSON.parseObject(apiRequestQuery.getData(), CompressionQuery.class);
        }catch (Exception e){
            log.error("COMPRESSION PROPERTY CAST ERROR! success error", e);
            return R.fail(null, null, "参数解析错误");
        }

        if(StringUtils.isBlank(compression.getCompressionFile())){
            return R.fail(null, null, "测试文件为空");
        }

        IotCard iotCard = iotCardService.queryBySn(compression.getIotCard());
        if(Objects.isNull(iotCard)){
            return R.fail(null, null, "未查询到物联网卡信息");
        }

        ArrayList<ProductNew> mainProducts = new ArrayList(1);
        if(CollectionUtils.isNotEmpty(compression.getNoList())){
            for(String no : compression.getNoList()){
                ProductNew product = this.queryByNo(no);
                if(Objects.equals(product.getType(), ProductNew.TYPE_M)){
                    mainProducts.add(product);
                }

            }
        }

        if(!(mainProducts.size() == 1) || Objects.isNull(mainProducts.get(0))){
            return R.fail(mainProducts, null, "主柜不存在或存在多个，请核对");
        }

        if(CollectionUtils.isNotEmpty(compression.getNoList())){
            for(String no : compression.getNoList()){
                ProductNew productOld = this.queryByNo(no);
                if(Objects.nonNull(productOld)){
                    ProductNew product = new ProductNew();
                    product.setNo(no);
                    product.setTestFile(compression.getCompressionFile());
                    product.setTestResult(1);
                    product.setStatus(6);
                    product.setIotCardId(iotCard.getId());
                    productNewMapper.updateByNo(product);
                }
            }
        }

        return R.ok();
    }

    @Override
    public R findIotCard(String no) {
        ProductNew productNew = this.productNewMapper.selectOne(new QueryWrapper<ProductNew>().eq("no", no));
        if(Objects.isNull(productNew)){
            return R.fail(null, null, "未查询到柜机信息，请检查资产编码是否正确");
        }

        if(Objects.equals(productNew.getType(), ProductNew.TYPE_M)){
            return R.fail(null, null, "资产编码不是主柜类型，请检查");
        }

        if(Objects.isNull(productNew.getIotCardId())){
            return R.fail(null, null, "柜机未录入物联网卡号");
        }
        Map<String, Object> result = new HashMap<>();
        result.put("data", productNew.getIotCardId());
        return  R.ok(result);
    }

    @Override
    public R queryByBatchAndSupplier(Long batchId, Long offset, Long size) {
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
        page = productNewMapper.selectPage(page, new QueryWrapper<ProductNew>().eq("batch_id", batchId)
                .eq("supplier_id", user.getThirdId())
                .eq("del_flag", ProductNew.DEL_NORMAL));

        List<ProductNew> list = page.getRecords();
        if(CollectionUtils.isEmpty(list)){
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
        result.put("total",  page.getTotal());
        return R.ok(result);
    }

    @Override
    public R queryProductNewInfoById(String no) {
        ProductNew productNew = this.queryByNo(no);
        ProductNewDetailsVo vo = new ProductNewDetailsVo();

        if(!Objects.isNull(productNew)){
            vo.setId(productNew.getId());
            Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
            if(Objects.nonNull(batch)){
                vo.setBatchId(productNew.getBatchId());
                vo.setBatchNo(batch.getBatchNo());
            }
            vo.setNo(productNew.getNo());
            vo.setStatus(productNew.getStatus());
            vo.setStatusName(this.getStatusName(productNew.getStatus()));

            if(Objects.nonNull(productNew.getCameraId())){
                Camera camera = cameraService.getById(productNew.getCameraId());
                if(Objects.nonNull(camera)){
                    vo.setCameraId(camera.getId());
                    vo.setSerialNum(camera.getSerialNum());

                    IotCard iotCard = iotCardService.getById(camera.getIotCardId());
                    if(Objects.nonNull(iotCard)){
                        vo.setCameraCardId(iotCard.getId());
                        vo.setCameraCard(iotCard.getSn());
                    }
                }
            }

            IotCard iotCard = iotCardService.getById(productNew.getIotCardId());
            if(Objects.nonNull(iotCard)){
                vo.setIotCardId(iotCard.getId());
                vo.setIotCardNo(iotCard.getSn());
            }

            vo.setColor(productNew.getColor());
            vo.setSurface(productNew.getSurface());
        }

        return R.ok(vo);
    }

    @Override
    public BaseMapper<ProductNew> getBaseMapper() {
        return this.productNewMapper;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public R updateProductNew(ProductNewDetailsQuery query) {
        log.error("手持终端更新批次订单-------->" + JsonUtil.toJson(query));
        ProductNew productNewOld = this.productNewMapper.queryById(query.getId());
        if(Objects.isNull(productNewOld)){
            return R.fail(null, null, "未查询到相关柜机信息");
        }

        Camera camera = new Camera();
        if(StringUtils.isNotBlank(query.getSerialNum())){
            camera =  cameraService.queryBySerialNum(query.getSerialNum());
            if(Objects.isNull(camera)){
                return R.fail(null, null, "未查询摄像头序列号");
            }

            ProductNew productNew = productNewMapper.selectOne(new QueryWrapper<ProductNew>()
                    .eq("camera_id", camera.getId())
                    .eq("del_flag", ProductNew.DEL_NORMAL));

            if(Objects.nonNull(productNew) && !Objects.equals(productNew.getId(), query.getId())){
                return R.fail(null, null, "序列号已绑定到其他产品");
            }
        }


        ProductNew updateProductNew = new ProductNew();
        updateProductNew.setId(query.getId());
        updateProductNew.setCameraId(camera.getId());
        updateProductNew.setIotCardId(query.getIotCardId());
        updateProductNew.setColor(query.getColor());
        updateProductNew.setSurface(query.getSurface());
        this.productNewMapper.updateById(updateProductNew);

        if(Objects.nonNull(camera.getId())){
            Camera updateCamera = new Camera();
            updateCamera.setId(camera.getId());
            updateCamera.setIotCardId(query.getCameraCardId());
            cameraService.updateById(updateCamera);
        }

        return R.ok();
    }

    @Override
    public R checkProperty(String no) {
        ProductNew productNew = this.queryByNo(no);
        if(Objects.isNull(productNew)){
            return R.fail(null,null,"柜机资产编码不存在，请核对");
        }

        if(!Objects.equals(productNew.getStatus(), 6) && !Objects.equals(productNew.getTestResult(), 1)){
            return R.fail(null,null,"柜机非已测试状态，请录入已测试柜机");
        }

        if(Objects.isNull(productNew.getIotCardId())){
            return R.fail(null,null,"物联卡号未绑定，请录入");
        }

        IotCard iotCard = iotCardService.getById(productNew.getIotCardId());
        if(Objects.isNull(iotCard)){
            return R.fail(null,null,"未查询到物联网卡信息，请核对");
        }

        Batch productBatch = batchService.queryByIdFromDB(productNew.getBatchId());
        if(Objects.isNull(productBatch)){
            return R.fail(null,null,"未查询到柜机批次，请联系管理员");
        }

        Batch iotBatch = batchService.queryByIdFromDB(iotCard.getBatchId());
        if(Objects.isNull(iotBatch)){
            return R.fail(null,null,"未查询到物联网卡批次，请录入");
        }

        if(Objects.equals(productNew.getType(), ProductNew.TYPE_M) && !Objects.equals(iotCard.getBatchId(), productNew.getBatchId())){
            return R.fail(null,null,"柜机批次与物联网卡批次不一致，请核对");
        }

        if(Objects.isNull(productNew.getCameraId())){
            return R.fail(null,null,"摄像头序列号未绑定，请录入");
        }

        Camera camera = cameraService.getById(productNew.getCameraId());
        if(Objects.isNull(camera)){
            return R.fail(null,null,"未查询到摄像序列号，请核对");
        }

        if(Objects.isNull(camera.getIotCardId())){
            return R.fail(null,null,"摄像头物联网卡号未绑定，请录入");
        }

        IotCard cameraCard = iotCardService.getById(camera.getIotCardId());
        if(Objects.isNull(cameraCard)){
            return R.fail(null,null,"未查询到摄像头物联网卡信息，请核对");
        }

        if(Objects.isNull(productNew.getColor())){
            return R.fail(null,null,"柜机颜色未填写，请录入");
        }

        if(Objects.isNull(productNew.getColor())){
            return R.fail(null,null,"柜机颜色未填写，请录入");
        }

        if(Objects.isNull(productNew.getSurface())){
            return R.fail(null,null,"柜机外观未填写，请录入");
        }

        Product product = productService.getById(productNew.getModelId());
        if(Objects.isNull(product)){
            return R.fail(null,null,"未查询到柜机类型，请联系管理员");
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


    private ProductNew queryByNo(String no){
        if(Objects.isNull(no)){
            return null;
        }
        return this.productNewMapper.selectOne(new QueryWrapper<ProductNew>().eq("no", no));
    }

    private String getStatusName(Integer status){
        String statusName = "";
        switch (status){
            case 0: statusName = "生产中"; break;
            case 1: statusName = "运输中"; break;
            case 2: statusName = "已收货"; break;
            case 3: statusName = "使用中"; break;
            case 4: statusName = "拆机柜"; break;
            case 5: statusName = "已报废"; break;
            case 6: statusName = "已测试"; break;
        }
        return statusName;
    }
}
