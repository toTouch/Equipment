package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.constant.CacheConstants;
import com.xiliulou.afterserver.constant.ProductNewStatusSortConstants;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.DeliverMapper;
import com.xiliulou.afterserver.mapper.PointNewMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.mapper.WareHouseProductDetailsMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.web.query.DeliverFactoryProductQuery;
import com.xiliulou.afterserver.web.query.DeliverFactoryQuery;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import com.xiliulou.afterserver.web.query.ProductInfoQuery;
import com.xiliulou.afterserver.web.vo.*;
import com.xiliulou.cache.redis.RedisService;
import com.xiliulou.core.json.JsonUtil;

import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.xiliulou.afterserver.entity.Deliver.DESTINATION_TYPE_POINT;
import static com.xiliulou.afterserver.entity.PointNew.DEL_NORMAL;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:05
 **/
@Service
@Slf4j
public class DeliverServiceImpl extends ServiceImpl<DeliverMapper, Deliver> implements DeliverService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PointNewMapper pointNewMapper;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ServerService serverService;
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private WarehouseService warehouseService;
    
    @Autowired
    private WareHouseProductDetailsMapper wareHouseProductDetailsMapper;
    
    @Autowired
    private InventoryFlowBillService inventoryFlowBillService;
    
    @Autowired
    private PointNewService pointNewService;
    
    @Autowired
    private ProductNewService productNewService;
    
    @Autowired
    private DeliverLogService deliverLogService;
    
    @Autowired
    private BatchService batchService;

    @Autowired
    private  ProductNewMapper productNewMapper;
    
    @Autowired
    private RedisService redisService;
    
    @Autowired
    private DeliverMapper deliverMapper;
    
    @Override
    public IPage getPage(Long offset, Long size, DeliverQuery deliver) {
        
        Page page = PageUtil.getPage(offset, size);
        Page selectPage = baseMapper.selectPage(page, new LambdaQueryWrapper<Deliver>().eq(Objects.nonNull(deliver.getState()), Deliver::getState, deliver.getState())
                .eq(Objects.nonNull(deliver.getCreateUid()), Deliver::getCreateUid, deliver.getCreateUid())
                .eq(Objects.nonNull(deliver.getTenantName()), Deliver::getTenantName, deliver.getTenantName())
                .like(Objects.nonNull(deliver.getExpressNo()), Deliver::getExpressNo, deliver.getExpressNo())
                .like(Objects.nonNull(deliver.getExpressCompany()), Deliver::getExpressCompany, deliver.getExpressCompany())
                .like(Objects.nonNull(deliver.getNo()), Deliver::getNo, deliver.getNo()).orderByDesc(Deliver::getCreateTime)
                .ge(Objects.nonNull(deliver.getCreateTimeStart()), Deliver::getDeliverTime, deliver.getCreateTimeStart())
                .le(Objects.nonNull(deliver.getCreateTimeEnd()), Deliver::getDeliverTime, deliver.getCreateTimeEnd())
                .like(Objects.nonNull(deliver.getCity()), Deliver::getCity, deliver.getCity())
                .eq(Objects.nonNull(deliver.getPaymentMethod()), Deliver::getPaymentMethod, deliver.getPaymentMethod())
                .like(Objects.nonNull(deliver.getDestination()), Deliver::getDestination, deliver.getDestination()));
        List<Deliver> list = (List<Deliver>) selectPage.getRecords();
        if (list.isEmpty()) {
            return selectPage;
        }
        
        // 查询所有产品型号
        List<Product> productAll = productService.list();
        Map<Long, Product> productAllMap = productAll.stream().collect(Collectors.toMap(Product::getId, product -> product));
        
        list.forEach(records -> {
            
            Map map = new HashMap();
            
            if ("null".equals(records.getQuantity()) || null == records.getQuantity()) {
                records.setQuantity(JSON.toJSONString(new String[] {null}));
            }
            
            if (Objects.nonNull(records.getCreateUid())) {
                User userById = userService.getUserById(records.getCreateUid());
                if (Objects.nonNull(userById)) {
                    records.setUserName(userById.getUserName());
                }
            }
            
            if (Objects.equals(records.getState(), 1)) {
                records.setDeliver(true);
            }
            

            
            //            第三方类型 1：客户 2：供应商 3:服务商';
            if (Objects.nonNull(records.getThirdCompanyType())) {
                String name = "";
                if (records.getThirdCompanyType() == 1) {
                    Customer byId = customerService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)) {
                        name = byId.getName();
                    }
                }
                if (records.getThirdCompanyType() == 2) {
                    Supplier byId = supplierService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)) {
                        name = byId.getName();
                    }
                }
                if (records.getThirdCompanyType() == 3) {
                    Server byId = serverService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)) {
                        name = byId.getName();
                    }
                }
                records.setThirdCompanyName(name);
            }
            
            if (StrUtil.isEmpty(records.getProduct())) {
                records.setProduct(JSONUtil.toJsonStr(new ArrayList<>()));
            } else {
                // json转数组
                List<Long> productInfo = JSON.parseArray(records.getProduct(), Long.class);
                List<ProductInfoQuery> productInfoQueries = new ArrayList<>();
                
                for (Long pro : productInfo) {
                    ProductInfoQuery productInfoQuery = new ProductInfoQuery();
                    if (Objects.isNull(pro)) {
                        continue;
                    }
                    Product product = productAllMap.get(pro);
                    if (Objects.nonNull(product)) {
                        productInfoQuery.setProductName(product.getName());
                        productInfoQuery.setProductId(product.getId());
                        productInfoQueries.add(productInfoQuery);
                    }
                }
                records.setProductInfoList(productInfoQueries);
            }

            /*if(ObjectUtils.isNotNull(records.getProduct())
                    && !"[]".equals(records.getProduct())
                    && ObjectUtils.isNotNull(records.getQuantity())
                    && !"[null]".equals(records.getQuantity())){

                ArrayList<Integer> products = JSON.parseObject(records.getProduct(), ArrayList.class);
                ArrayList<Integer> quantitys = JSON.parseObject(records.getQuantity(), ArrayList.class);

                for(int i = 0; i < products.size() && ( quantitys.size() == products.size() ); i++){
                    Product p = productService.getById(products.get(i));
                    if(ObjectUtils.isNotNull(p)){
                        map.put(p.getName(), quantitys.get(i));
                    }
                }
                records.setDetails(map);
            }*/
            
            records.setPaymentMethodName(getpaymentMethodName(records.getPaymentMethod()));
            
            List<DeliverLog> deliverLogs = deliverLogService.getByDeliverId(records.getId());
            Map<String, List<DeliverInfoVo>> result = new HashMap<>();
            
            if (CollectionUtils.isNotEmpty(deliverLogs)) {
                deliverLogs.stream().forEach(item -> {
                    ProductNew productNew = productNewService.queryByIdFromDB(item.getProductId());
                    if (Objects.isNull(productNew)) {
                        return;
                    }
                    
                    Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
                    if (Objects.isNull(batch)) {
                        return;
                    }
                    
                    Product product = productService.getById(productNew.getModelId());
                    if (Objects.isNull(product)) {
                        return;
                    }
                    
                    DeliverInfoVo deliverInfoVo = new DeliverInfoVo();
                    deliverInfoVo.setModelName(product.getName());
                    deliverInfoVo.setProductNo(productNew.getNo());
                    
                    if (!result.containsKey(batch.getBatchNo())) {
                        List<DeliverInfoVo> deliverInfoVos = new ArrayList<>();
                        result.put(batch.getBatchNo(), deliverInfoVos);
                    }
                    
                    List<DeliverInfoVo> deliverInfoVos = result.get(batch.getBatchNo());
                    deliverInfoVos.add(deliverInfoVo);
                });
                
                records.setDeliverInfoVoMap(result);
            }
        });
        
        return selectPage.setRecords(list);
    }
    
    // 导出excel
    @Override
    public void exportExcel(DeliverQuery query, HttpServletResponse response) {
        List<Deliver> deliverList = baseMapper.orderList(query);
        List<Product> productAll = productService.list();
        
        if (ObjectUtil.isEmpty(deliverList)) {
            throw new CustomBusinessException("没有查询到发货信息!无法导出！");
        }
        
        if (ObjectUtil.isEmpty(productAll)) {
            throw new CustomBusinessException("没有查询到产品型号!无法导出！");
        }
        
        // headerSet
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("Sheet");
        Table table = new Table(1);
        // 动态添加 表头 headList --> 所有表头行集合
        List<List<String>> headList = new ArrayList<List<String>>();
        
        String[] header = {"发货编号", "客户", "客户电话", "起点", "终点", "物流状态", "第三方公司", "第三方承担费用（元）", "运费", "结算方式", "发货时间", "快递公司", "快递单号",
                "创建人", "备注"};
        for (String s : header) {
            List<String> headTitle = new ArrayList<String>();
            headTitle.add(s);
            headList.add(headTitle);
        }
        
        // 添加柜子资产编码，查询最大个数
        Integer noMax = deliverLogService.queryMaxCountBydeliverIds(deliverList.parallelStream().map(Deliver::getId).collect(Collectors.toList()));
        for (int i = 1; i <= noMax; i++) {
            List<String> headTitle = new ArrayList<String>();
            headTitle.add("资产编码" + i);
            headList.add(headTitle);
        }
        
        for (Product p : productAll) {
            List<String> headTitle = new ArrayList<String>();
            headTitle.add(p.getName());
            headList.add(headTitle);
        }
        table.setHead(headList);
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<List<Object>> list = new ArrayList<>();
        
        // List<Object> deliverExcelVoList = new ArrayList<>(deliverList.size());
        for (Deliver d : deliverList) {
            DeliverExportExcelVo deliverExcelVo = new DeliverExportExcelVo();
            
            List<Object> row = new ArrayList<>();
            
            // no
            row.add(d.getNo() == null ? "" : d.getNo());
            // customerName
            if (ObjectUtil.isNotNull(d.getCustomerId())) {
                Customer customer = customerService.getById(d.getCustomerId());
                if (ObjectUtil.isNotNull(customer)) {
                    row.add(customer.getName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }
            // phone
            row.add(d.getPhone() == null ? "" : d.getPhone());
            // city
            row.add(d.getCity() == null ? "" : d.getCity());
            // destination
            row.add(d.getDestination() == null ? "" : d.getDestination());
            // stateStr
            if (ObjectUtil.isNotNull(d.getState())) {
                row.add(getDeliverStatue(d.getState()));
            } else {
                row.add("");
            }
            // thirdCompanyName
            if (Objects.nonNull(d.getThirdCompanyType())) {
                String name = "";
                if (d.getThirdCompanyType() == 1) {
                    Customer byId = customerService.getById(d.getThirdCompanyId());
                    if (Objects.nonNull(byId)) {
                        name = byId.getName();
                    }
                }
                if (d.getThirdCompanyType() == 2) {
                    Supplier byId = supplierService.getById(d.getThirdCompanyId());
                    if (Objects.nonNull(byId)) {
                        name = byId.getName();
                    }
                }
                if (d.getThirdCompanyType() == 3) {
                    Server byId = serverService.getById(d.getThirdCompanyId());
                    if (Objects.nonNull(byId)) {
                        name = byId.getName();
                    }
                }
                row.add(name);
            } else {
                row.add("");
            }
            // thirdCompanyPay
            row.add(d.getThirdCompanyPay() == null ? "" : d.getThirdCompanyPay());
            // deliverCost
            row.add(d.getDeliverCost() == null ? "" : d.getDeliverCost());
            // paymentMethod
            row.add(getpaymentMethodName(d.getPaymentMethod()));
            // deliverTime
            if (ObjectUtil.isNotNull(d.getDeliverTime())) {
                row.add(simpleDateFormat.format(new Date(d.getDeliverTime())));
            } else {
                row.add("");
            }
            // expressCompany
            row.add(d.getExpressCompany() == null ? "" : d.getExpressCompany());
            // expressNo
            row.add(d.getExpressNo() == null ? "" : d.getExpressNo());
            // createUName
            if (ObjectUtil.isNotNull(d.getCreateUid())) {
                User user = userService.getById(d.getCreateUid());
                if (ObjectUtil.isNotNull(user)) {
                    row.add(user.getUserName());
                } else {
                    row.add("");
                }
            } else {
                row.add("");
            }
            // remark
            row.add(d.getRemark() == null ? "" : d.getRemark());
            
            // 支持编码
            List<DeliverLog> byDeliverId = deliverLogService.getByDeliverId(d.getId());
            if (!CollectionUtils.isEmpty(byDeliverId)) {
                byDeliverId.parallelStream().forEach(item -> {
                    ProductNew productNew = productNewService.queryByIdFromDB(item.getProductId());
                    if (Objects.isNull(productNew)) {
                        return;
                    }
                    
                    row.add(StringUtils.isBlank(productNew.getNo()) ? "" : productNew.getNo());
                });
                
                Integer limitLen = noMax - byDeliverId.size();
                for (int i = 0; i < limitLen; i++) {
                    row.add("");
                }
            } else {
                for (int i = 0; i < noMax; i++) {
                    row.add("");
                }
            }
            
            // productAndNum
            if (!StrUtil.isEmpty(d.getProduct()) && d.getProduct().matches("\\[.*\\]") && !StrUtil.isEmpty(d.getQuantity()) && !"[null]".equals(d.getQuantity()) && d.getQuantity()
                    .matches("\\[.*\\]")) {
                
                ArrayList<Integer> products = JSON.parseObject(d.getProduct(), ArrayList.class);
                ArrayList<Integer> quantitys = JSON.parseObject(d.getQuantity(), ArrayList.class);
                
                for (Product p : productAll) {
                    boolean falg = false;
                    int index = -1;
                    for (int i = 0; i < products.size(); i++) {
                        if (Objects.equals(String.valueOf(p.getId()), String.valueOf(products.get(i)))) {
                            falg = true;
                            index = i;
                        }
                    }
                    
                    if (falg) {
                        row.add(quantitys.get(index));
                    } else {
                        row.add("");
                    }
                }
                
            } else {
                for (Product p : productAll) {
                    row.add("");
                }
            }
            list.add(row);
        }
        
        String fileName = "发货管理.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcelFactory.getWriter(outputStream).write(list, sheet, table).finish();
            // EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateStatusFromBatch(List<Long> ids, Integer status) {
        // TODO 2022年2月18日09:44:02 工厂要不要直接改产品位置？
        int row = this.baseMapper.updateStatusFromBatch(ids, status);
        if (row == 0) {
            return R.fail("未修改数据");
        }
        return R.ok();
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public R insert(Deliver deliver, Long wareHouseIdStart, Long wareHouseIdEnd) {
        // R r = saveWareHouseDetails(deliver, wareHouseIdStart, wareHouseIdEnd);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        deliver.setNo(sdf.format(new Date()) + RandomUtil.randomInt(100000, 1000000));
        deliver.setCreateTime(System.currentTimeMillis());
        return R.ok(this.save(deliver));
        
    }
    
    
    @Override
    public R updateDeliver(Deliver deliver, Long wareHouseIdStart, Long wareHouseIdEnd) {
        
        R r = null;
        
        Deliver oldDeliver = this.getById(deliver.getId());
        
        if (ObjectUtils.isNotNull(oldDeliver)) {
            
            if (Integer.valueOf(2).equals(oldDeliver.getState()) || Integer.valueOf(3).equals(oldDeliver.getState())) {
                // product 型号
                // quantity 数量
                if (Objects.nonNull(oldDeliver.getProduct()) && (!oldDeliver.getProduct().equals(deliver.getProduct()) || !oldDeliver.getQuantity()
                        .equals(deliver.getQuantity()))) {
                    
                    return R.fail("已发货或已到达的订单不可改变产品型号和数量");
                }
                
                if (Integer.valueOf(1).equals(deliver.getState())) {
                    return R.fail("已发货或已到达的订单不可改变物流状态为未发货");
                }
                
                if (!Objects.equals(oldDeliver.getCityType(), deliver.getCityType()) || !Objects.equals(oldDeliver.getCityId(), deliver.getCityId())) {
                    return R.fail("已发货或已到达的订单不可改变起点或终点");
                }
                
                if (!Objects.equals(oldDeliver.getDestinationType(), deliver.getDestinationType()) || !Objects.equals(oldDeliver.getDestinationId(), deliver.getDestinationId())) {
                    return R.fail("已发货或已到达的订单不可改变起点或终点");
                }
            }
        }
        // if(r == null){
        deliver.setCreateTime(System.currentTimeMillis());
        r = R.ok(updateById(deliver));
        //}
        return r;
    }
    
    @Override
    public R queryListByFactory(Long offset, Long size) {
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
        page = baseMapper.selectPage(page,
                new QueryWrapper<Deliver>().eq("city_type", Deliver.CITY_TYPE_FACTORY).eq("city", supplier.getName()).eq("state", 1).orderByDesc("create_time"));
        List<Deliver> list = page.getRecords();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        List<OrderDeliverVo> data = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(item -> {
                OrderDeliverVo orderDeliverVo = new OrderDeliverVo();
                orderDeliverVo.setNo(item.getNo());
                orderDeliverVo.setRemark(item.getRemark());
                orderDeliverVo.setCustomerName(item.getTenantName());
                
                ArrayList<Integer> productIds = JSON.parseObject(item.getProduct(), ArrayList.class);
                ArrayList<String> quantityIds = JSON.parseObject(item.getQuantity(), ArrayList.class);
                ArrayList<OrderDeliverContentVo> orderDeliverContentVos = new ArrayList<>();
                
                if (productIds.size() == quantityIds.size()) {
                    for (int i = 0; i < productIds.size(); i++) {
                        Product product = productService.getById(productIds.get(i));
                        if (Objects.nonNull(product)) {
                            OrderDeliverContentVo orderDeliverContentVo = new OrderDeliverContentVo();
                            orderDeliverContentVo.setModelName(product.getName());
                            orderDeliverContentVo.setNum(quantityIds.get(i));
                            orderDeliverContentVos.add(orderDeliverContentVo);
                        }
                    }
                }
                
                orderDeliverVo.setContent(orderDeliverContentVos);
                orderDeliverVo.setCreateTime(sdf.format(new Date(item.getCreateTime())));
                data.add(orderDeliverVo);
            });
        }
        
        Map<String, Object> result = new HashMap<>(2);
        result.put("data", data);
        result.put("total", page.getTotal());
        return R.ok(result);
    }
    
    @Override
    public R queryContentByFactory(String no) {
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
        
        Deliver deliver = this.queryByNo(no);
        ArrayList<OrderDeliverContentVo> orderDeliverContentVos = new ArrayList<>();
        
        if (Objects.nonNull(deliver)) {
            ArrayList<Integer> productIds = JSON.parseObject(deliver.getProduct(), ArrayList.class);
            ArrayList<String> quantityIds = JSON.parseObject(deliver.getQuantity(), ArrayList.class);
            
            if (productIds.size() == quantityIds.size()) {
                for (int i = 0; i < productIds.size(); i++) {
                    Product product = productService.getById(productIds.get(i));
                    if (Objects.nonNull(product)) {
                        OrderDeliverContentVo orderDeliverContentVo = new OrderDeliverContentVo();
                        orderDeliverContentVo.setModelName(product.getName());
                        orderDeliverContentVo.setNum(quantityIds.get(i));
                        orderDeliverContentVos.add(orderDeliverContentVo);
                    }
                }
            }
        }
        return R.ok(orderDeliverContentVos);
    }
    
    @Override
    public R factoryDeliver(DeliverFactoryQuery deliverFactoryQuery) {
        if (!redisService.setNx(CacheConstants.CACHE_FACTORY_DELIVER_LOCK + SecurityUtils.getUid(), "ok", 2000L, false)) {
            return R.fail(null, null, "操作频繁，请稍后再试");
        }
        
        Deliver deliver = this.queryByNo(deliverFactoryQuery.getSn());
        if (Objects.isNull(deliver)) {
            return R.fail(null, null, "未查询到相关发货编号");
        }
        
        if (CollectionUtils.isEmpty(deliverFactoryQuery.getProductContent())) {
            return R.fail(null, null, "请传入发货信息");
        }
        
        List<String> productIds = JsonUtil.fromJsonArray(deliver.getProduct(), String.class);
        List<String> quantityIds = JsonUtil.fromJsonArray(deliver.getQuantity(), String.class);
        Map<Long, Integer> deliverInfo = fillDeliverInfo(productIds, quantityIds);
        
        // 获取产品
        List<String> productNos = deliverFactoryQuery.getProductContent().stream().map(DeliverFactoryProductQuery::getNo).collect(Collectors.toList());
        List<ProductNew> productNews = productNewService.getBaseMapper()
                .selectList(new LambdaQueryWrapper<ProductNew>().eq(ProductNew::getDelFlag, ProductNew.DEL_NORMAL).in(ProductNew::getNo, productNos));
        
        // 发货柜机包含的柜机 K: sn V: ProductNew
        if (productNews == null) {
            return R.fail(null, null, "资产编码【" + productNos + "】未查询到");
        }
        Map<String, ProductNew> stringToProductNewMap = productNews.stream().collect(Collectors.toMap(ProductNew::getNo, Function.identity(), (oldValue, newValue) -> newValue));
        
        // 录入内容填充
        Map<Long, Integer> insertInfo = new HashMap<>();
        for (DeliverFactoryProductQuery productQuery : deliverFactoryQuery.getProductContent()) {
            ProductNew productNew = stringToProductNewMap.get(productQuery.getNo());
            
            if (Objects.isNull(productNew)) {
                return R.fail(null, null, "资产编码【" + productQuery.getNo() + "】未查询到");
            }
            
            // 检验是否发货
            Boolean existDeliverLog = deliverLogService.existDeliverLog(productNew.getId());
            if (existDeliverLog) {
                return R.fail(null, null, "资产编码【" + productQuery.getNo() + "】已发货");
            }
            
            // 统计上传柜机类型个数 不存在设为1 存在则加一
            insertInfo.compute(productNew.getModelId(), (key, value) -> value == null ? 1 : value + 1);
        }
        
        if (!insertInfo.equals(deliverInfo)) {
            return R.fail(null, null, "发货内容与录入内容不一致，请检查");
        }
        
        // 发货
        Deliver updateDeliver = new Deliver();
        updateDeliver.setId(deliver.getId());
        updateDeliver.setState(Deliver.STATUS_SHIPPED);
        this.updateById(updateDeliver);
        
        // 添加发货日志
        addDeliverLog(deliverFactoryQuery, stringToProductNewMap, deliver);
        
        // 获取发货批次并去重 K: 批次id V: 此批次的产品 List<ProductNew>
        Map<Long, List<ProductNew>> batchToProductNewMap = productNews.stream().collect(Collectors.groupingBy(ProductNew::getBatchId));
        
        // 发货后更新批次未发货数量
        updateBatchUnshippedQuantity(batchToProductNewMap);
        return R.ok();
    }
    
    // 发货后更新批次未发货数量
    private void updateBatchUnshippedQuantity(Map<Long, List<ProductNew>> batchToProductNewMap) {
        batchToProductNewMap.forEach((batchId, tempProductNews) -> {
            Batch batch = batchService.queryByIdFromDB(batchId);
            Batch batchUp = new Batch();
            batchUp.setId(batchId);
            batchUp.setNotShipped(batch.getNotShipped() - tempProductNews.size());
            log.debug("已经发货数量: " +tempProductNews.size());
            batchUp.setUpdateTime(System.currentTimeMillis());
            batchService.update(batchUp);
            log.debug("更新批次未发货数量" + batchUp);
        });
    }
    
    // 填充发货信息
    private Map<Long, Integer> fillDeliverInfo(List<String> productIds, List<String> quantityIds) {
        Map<Long, Integer> deliverInfo = new HashMap<>();
        if (CollectionUtils.isNotEmpty(productIds) && CollectionUtils.isNotEmpty(quantityIds) && productIds.size() == quantityIds.size()) {
            for (int i = 0; i < productIds.size(); i++) {
                if (StringUtils.isBlank(productIds.get(i)) || Objects.equals("null", productIds.get(i)) || StringUtils.isBlank(quantityIds.get(i)) || Objects.equals("null",
                        quantityIds.get(i))) {
                    continue;
                }
                deliverInfo.put(new Long(productIds.get(i)), Integer.parseInt(quantityIds.get(i)));
            }
        }
        return deliverInfo;
    }
    
    // 添加发货日志
    private void addDeliverLog(DeliverFactoryQuery deliverFactoryQuery, Map<String, ProductNew> stringToProductNewMap, Deliver deliver) {
        ArrayList<Long> productIds = new ArrayList<>();
        for (DeliverFactoryProductQuery productQuery : deliverFactoryQuery.getProductContent()) {
            ProductNew productNew = stringToProductNewMap.get(productQuery.getNo());
            productIds.add(productNew.getId());
            
            DeliverLog deliverLog = new DeliverLog();
            deliverLog.setDeliverId(deliver.getId());
            deliverLog.setProductId(productNew.getId());
            deliverLog.setInsertTime(productQuery.getInsetTime());
            deliverLog.setCreateTime(System.currentTimeMillis());
            deliverLog.setUpdateTime(System.currentTimeMillis());
            deliverLogService.save(deliverLog);
        }
        
        // 批量更新产品发货状态
        productNewMapper.batchUpdateStatusById(productIds, ProductNewStatusSortConstants.STATUS_SHIPPED);
    }
    
    
    @Override
    public R queryIssueListByFactory() {
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
        
        List<Deliver> list = baseMapper.selectList(new QueryWrapper<Deliver>().eq("city_type", Deliver.CITY_TYPE_FACTORY).eq("city", supplier.getName()).eq("state", 2));
        
        List<OrderDeliverVo> result = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(list)) {
            list.stream().forEach(item -> {
                OrderDeliverVo orderDeliverVo = new OrderDeliverVo();
                orderDeliverVo.setNo(item.getNo());
                orderDeliverVo.setRemark(item.getRemark());
                
                ArrayList<Integer> productIds = JSON.parseObject(item.getProduct(), ArrayList.class);
                ArrayList<String> quantityIds = JSON.parseObject(item.getQuantity(), ArrayList.class);
                ArrayList<OrderDeliverContentVo> orderDeliverContentVos = new ArrayList<>();
                
                if (productIds.size() == quantityIds.size()) {
                    for (int i = 0; i < productIds.size(); i++) {
                        Product product = productService.getById(productIds.get(i));
                        if (Objects.nonNull(product)) {
                            OrderDeliverContentVo orderDeliverContentVo = new OrderDeliverContentVo();
                            orderDeliverContentVo.setModelName(product.getName());
                            orderDeliverContentVo.setNum(quantityIds.get(i));
                            orderDeliverContentVos.add(orderDeliverContentVo);
                        }
                    }
                }
                
                orderDeliverVo.setContent(orderDeliverContentVos);
                result.add(orderDeliverVo);
            });
        }
        
        return R.ok(result);
    }
    
    @Override
    public R queryIssueInfo(String no) {
        Deliver deliver = this.getBaseMapper().selectOne(new QueryWrapper<Deliver>().eq("no", no));
        
        List<DeliverLog> deliverLogs = deliverLogService.getByDeliverId(deliver.getId());
        Map<String, List<DeliverProductNewInfoVo>> classification = new HashMap<>();
        List<List<DeliverProductNewInfoVo>> result = new ArrayList<>();
        
        if (CollectionUtils.isNotEmpty(deliverLogs)) {
            deliverLogs.stream().forEach(item -> {
                DeliverProductNewInfoVo vo = new DeliverProductNewInfoVo();
                
                ProductNew productNew = productNewService.queryByIdFromDB(item.getProductId());
                if (Objects.isNull(productNew)) {
                    return;
                }
                
                Batch batch = batchService.queryByIdFromDB(productNew.getBatchId());
                if (Objects.isNull(batch)) {
                    return;
                }
                
                Product product = productService.getById(productNew.getModelId());
                if (Objects.isNull(product)) {
                    return;
                }
                
                vo.setId(item.getId());
                vo.setInsertTime(item.getInsertTime());
                vo.setStatusName(getProductStatusName(productNew.getStatus()));
                vo.setNo(productNew.getNo());
                vo.setModelName(product.getName());
                vo.setBatchNo(batch.getBatchNo());
                vo.setBatchId(batch.getId());
                
                if (!classification.containsKey(batch.getBatchNo())) {
                    List<DeliverProductNewInfoVo> deliverProductNewInfos = new ArrayList<>();
                    classification.put(batch.getBatchNo(), deliverProductNewInfos);
                }
                
                List<DeliverProductNewInfoVo> deliverProductNewInfos = classification.get(batch.getBatchNo());
                deliverProductNewInfos.add(vo);
                
            });
            
            for (List<DeliverProductNewInfoVo> item : classification.values()) {
                result.add(item);
            }
        }
        
        return R.ok(result);
    }
    
    
    @Override
    public Deliver queryByNo(String no) {
        if (StringUtils.isBlank(no)) {
            return null;
        }
        return this.baseMapper.selectOne(new QueryWrapper<Deliver>().eq("no", no));
    }
    
    
    private R saveWareHouseDetails(Deliver deliver, Long wareHouseIdStart, Long wareHouseIdEnd) {
        if ((wareHouseIdStart != null || wareHouseIdEnd != null) && deliver.getProduct() != null && deliver.getQuantity() != null) {
            
            Long inventoryFlowBillStatus = null;
            if (wareHouseIdStart != null) {
                if (wareHouseIdEnd != null) {
                    inventoryFlowBillStatus = InventoryFlowBill.TYPE_CALL_DELIVERY;
                } else {
                    inventoryFlowBillStatus = InventoryFlowBill.TYPE_SALES_DELIVERY;
                }
            }
            
            if (wareHouseIdStart != null && Objects.equals(deliver.getCityType(), 2) && ObjectUtils.isNull(warehouseService.getById(wareHouseIdStart))) {
                return R.fail("未查询到起点仓库");
            }
            if (wareHouseIdEnd != null && Objects.equals(deliver.getDestinationType(), 2) && ObjectUtils.isNull(warehouseService.getById(wareHouseIdEnd))) {
                return R.fail("未查询到终点仓库");
            }
            // product  型号
            // quantity  数量
            ArrayList<Integer> productIds = JSON.parseObject(deliver.getProduct(), ArrayList.class);
            ArrayList<String> quantityIds = JSON.parseObject(deliver.getQuantity(), ArrayList.class);
            if (productIds.size() == quantityIds.size()) {
                for (int i = 0; i < productIds.size(); i++) {
                    if (wareHouseIdStart != null && Objects.equals(deliver.getCityType(), 2)) {
                        WareHouseProductDetails wareHouseProductDetails = wareHouseProductDetailsMapper.selectOne(
                                new QueryWrapper<WareHouseProductDetails>().eq("ware_house_id", wareHouseIdStart).eq("product_id", productIds.get(i)));
                        if (ObjectUtils.isNotNull(wareHouseProductDetails)) {
                            
                            if (wareHouseProductDetails.getStockNum() - Integer.parseInt(quantityIds.get(i)) < 0) {
                                Product product = productService.getById(wareHouseProductDetails.getProductId());
                                return R.fail("仓库中【" + product.getName() + "】库存不足！库存余量：" + wareHouseProductDetails.getStockNum());
                            }
                            
                            if (deliver.getState() == 1) {
                                return null;
                            }
                            
                            wareHouseProductDetails.setStockNum(wareHouseProductDetails.getStockNum() - Integer.parseInt(quantityIds.get(i)));
                            wareHouseProductDetailsMapper.updateById(wareHouseProductDetails);
                            
                            InventoryFlowBill inventoryFlowBill = new InventoryFlowBill();
                            inventoryFlowBill.setNo(RandomUtil.randomString(10));
                            inventoryFlowBill.setType(inventoryFlowBillStatus);
                            inventoryFlowBill.setMarkNum("-" + quantityIds.get(i));
                            inventoryFlowBill.setSurplusNum(wareHouseProductDetails.getStockNum() + "");
                            inventoryFlowBill.setCreateTime(System.currentTimeMillis());
                            inventoryFlowBill.setWid(wareHouseProductDetails.getId());
                            
                            inventoryFlowBillService.save(inventoryFlowBill);
                        } else {
                            WareHouse wareHouseStart = warehouseService.getById(wareHouseIdStart);
                            Product product = productService.getById(productIds.get(i));
                            return R.fail("仓库【" + wareHouseStart.getWareHouses() + "】没有" + (product == null ? "【未知】" : "【" + product.getName() + "】") + "产品");
                        }
                    }
                    
                    if (wareHouseIdEnd != null && Objects.equals(deliver.getDestinationType(), 2)) {
                        WareHouseProductDetails wareHouseProductDetails = wareHouseProductDetailsMapper.selectOne(
                                new QueryWrapper<WareHouseProductDetails>().eq("ware_house_id", wareHouseIdEnd).eq("product_id", productIds.get(i)));
                        
                        if (ObjectUtils.isNotNull(wareHouseProductDetails)) {
                            wareHouseProductDetails.setStockNum(wareHouseProductDetails.getStockNum() + Integer.parseInt(quantityIds.get(i)));
                            wareHouseProductDetailsMapper.updateById(wareHouseProductDetails);
                        } else {
                            wareHouseProductDetails = new WareHouseProductDetails();
                            wareHouseProductDetails.setProductId(Long.valueOf(productIds.get(i)));
                            wareHouseProductDetails.setWareHouseId(wareHouseIdEnd);
                            wareHouseProductDetails.setStockNum(Long.valueOf(quantityIds.get(i)));
                            wareHouseProductDetailsMapper.insert(wareHouseProductDetails);
                        }
                        
                        InventoryFlowBill inventoryFlowBill = new InventoryFlowBill();
                        inventoryFlowBill.setNo(RandomUtil.randomString(10));
                        inventoryFlowBill.setType(InventoryFlowBill.TYPE_CALL_WAREHOUSING);
                        inventoryFlowBill.setMarkNum("+" + quantityIds.get(i));
                        inventoryFlowBill.setSurplusNum(wareHouseProductDetails.getStockNum() + "");
                        inventoryFlowBill.setCreateTime(System.currentTimeMillis());
                        inventoryFlowBill.setWid(wareHouseProductDetails.getId());
                        inventoryFlowBillService.save(inventoryFlowBill);
                    }
                    
                }
            } else {
                return R.fail("产品型号与数量个数不一致,将检查后重新提交");
            }
        }
        return null;
    }
    
    private String getpaymentMethodName(Integer method) {
        String methodStr = "";
        method = method == null ? 0 : method;
        switch (method) {
            case 1:
                methodStr = "月结";
                break;
            case 2:
                methodStr = "现结";
                break;
        }
        return methodStr;
    }
    
    private String getProductStatusName(Integer status) {
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
    
    private String getExpressNo(Integer status) {
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "待处理";
                break;
            case 2:
                statusStr = "处理中";
                break;
            case 3:
                statusStr = "已完成";
                break;
        }
        return statusStr;
    }
    
    private String getDeliverStatue(Integer status) {
        // 物流状态 1：未发货  2：已发货  3：已到达
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "未发货";
                break;
            case 2:
                statusStr = "已发货";
                break;
            case 3:
                statusStr = "已到达";
                break;
        }
        return statusStr;
    }
    
    @Override
    public Deliver getDeliverInfo(String sn) {
        if (StringUtils.isBlank(sn)) {
            return null;
        }
        Deliver deliver = deliverMapper.getDeliverInfo(sn);
            return deliver;
    }
}
