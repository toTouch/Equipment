package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.DeliverMapper;
import com.xiliulou.afterserver.mapper.WareHouseProductDetailsMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import com.xiliulou.afterserver.web.vo.DeliverExcelVo;
import com.xiliulou.afterserver.web.vo.DeliverExportExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Override
    public IPage getPage(Long offset, Long size, DeliverQuery deliver) {

        Page page = PageUtil.getPage(offset, size);
        Page selectPage = baseMapper.selectPage(page,
                new LambdaQueryWrapper<Deliver>()
                        .eq(Objects.nonNull(deliver.getState()), Deliver::getState, deliver.getState())
                        .eq(Objects.nonNull(deliver.getCreateUid()), Deliver::getCreateUid, deliver.getCreateUid())
                        .like(Objects.nonNull(deliver.getExpressNo()), Deliver::getExpressNo, deliver.getExpressNo())
                        .like(Objects.nonNull(deliver.getExpressCompany()), Deliver::getExpressCompany, deliver.getExpressCompany())
                        .orderByDesc(Deliver::getCreateTime)
                        .ge(Objects.nonNull(deliver.getCreateTimeStart()), Deliver::getDeliverTime, deliver.getCreateTimeStart())
                        .le(Objects.nonNull(deliver.getCreateTimeEnd()), Deliver::getDeliverTime, deliver.getCreateTimeEnd())
                        .like(Objects.nonNull(deliver.getCity()),Deliver::getCity,deliver.getCity())
                        .like(Objects.nonNull(deliver.getDestination()),Deliver::getDestination,deliver.getDestination()));
        List<Deliver> list = (List<Deliver>) selectPage.getRecords();
        if (list.isEmpty()) {
            return selectPage;
        }



        list.forEach(records -> {

            Map map = new HashMap();

            if("null".equals(records.getQuantity()) || null == records.getQuantity()){
                records.setQuantity(JSON.toJSONString(new String[]{null}));
            }


            if (Objects.nonNull(records.getCreateUid())){
                User userById = userService.getUserById(records.getCreateUid());
                if (Objects.nonNull(userById)){
                    records.setUserName(userById.getUserName());
                }
               records.setUserName(userById.getUserName());
            }

//            第三方类型 1：客户 2：供应商 3:服务商';
            if (Objects.nonNull(records.getThirdCompanyType())){
                String name = "";
                if (records.getThirdCompanyType() == 1){
                    Customer byId = customerService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)){
                        name = byId.getName();
                    }
                }
                if (records.getThirdCompanyType() == 2){
                    Supplier byId = supplierService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)){
                        name = byId.getName();
                    }
                }
                if (records.getThirdCompanyType() == 3){
                    Server byId = serverService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)){
                        name = byId.getName();
                    }
                }
                records.setThirdCompanyName(name);
            }

            if(StrUtil.isEmpty(records.getProduct())) {
                records.setProduct(JSONUtil.toJsonStr(new ArrayList<>()));
            }

            if(ObjectUtils.isNotNull(records.getProduct())
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
            }

        });

        return selectPage.setRecords(list);
    }

    //导出excel
    @Override
    public void exportExcel(DeliverQuery query, HttpServletResponse response) {
        List<Deliver> deliverList = baseMapper.orderList(query);

        if (ObjectUtil.isEmpty(deliverList)) {
            throw new CustomBusinessException("没有查询到产品型号!无法导出！");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DeliverExportExcelVo> deliverExcelVoList = new ArrayList<>(deliverList.size());
        for (Deliver d : deliverList) {
            DeliverExportExcelVo deliverExcelVo = new DeliverExportExcelVo();
            BeanUtil.copyProperties(d, deliverExcelVo);
            //thirdCompanyName
            if(ObjectUtil.isNotNull(d.getThirdCompanyId())){
                Customer customer = customerService.getById(d.getThirdCompanyId());
                if(ObjectUtil.isNotNull(customer)){
                    deliverExcelVo.setThirdCompanyName(customer.getName());
                }
            }
            //deliverTime
            if(ObjectUtil.isNotNull(d.getDeliverTime())){
                deliverExcelVo.setDeliverTime(simpleDateFormat.format(new Date(d.getDeliverTime())));
            }
            //createUName
            if(ObjectUtil.isNotNull(d.getCreateUid())){
                User user = userService.getById(d.getCreateUid());
                if(ObjectUtil.isNotNull(user)){
                    deliverExcelVo.setCreateUName(user.getUserName());
                }
            }
            //stateStr
            if(ObjectUtil.isNotNull(d.getState())){
                deliverExcelVo.setStateStr(getDeliverStatue(d.getState()));
            }
            //customerName
            if(ObjectUtil.isNotNull(d.getCustomerId())){
                Customer customer = customerService.getById(d.getCustomerId());
                if(ObjectUtil.isNotNull(customer)){
                    deliverExcelVo.setCreateUName(customer.getName());
                }
            }
            //productAndNum
            if(StrUtil.isEmpty(d.getProduct())
                    && !d.getProduct().equals("null")
                    && StrUtil.isEmpty(d.getProduct())
                    && !d.getQuantity().equals("null")
                    && !d.getQuantity().equals("[null]")){

                ArrayList<Integer> products = JSON.parseObject(d.getProduct(), ArrayList.class);
                ArrayList<Integer> quantitys = JSON.parseObject(d.getQuantity(), ArrayList.class);
                StringBuilder sb = new StringBuilder();

                if( quantitys.size() == products.size() && products.size() != 0 && quantitys.size() != 0 && products.get(0) != null && quantitys.get(0) != null ){
                    for(int i = 0; i < products.size(); i++){
                        Product p = productService.getById(products.get(i));
                        if(ObjectUtils.isNotNull(p)){
                            sb.append(p.getName()).append(" -- ").append(quantitys.get(i));
                            if(i < quantitys.size() - 1){
                                sb.append(" ,\n");
                            }
                        }
                    }
                }

                deliverExcelVo.setProductAndNum(sb.toString());
            }
            deliverExcelVoList.add(deliverExcelVo);
        }

        String fileName = "发货管理.xls";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateStatusFromBatch(List<Long> ids, Integer status) {
        int row = this.baseMapper.updateStatusFromBatch(ids,status);
        if(row == 0){
            return R.fail("未修改数据");
        }
        return R.ok();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public R insert(Deliver deliver,  Long wareHouseIdStart, Long wareHouseIdEnd) {
        R r = saveWareHouseDetails(deliver, wareHouseIdStart, wareHouseIdEnd);
        if(r == null){
            deliver.setCreateTime(System.currentTimeMillis());
            r = R.ok(this.save(deliver));
        }
        return r;
    }



    @Override
    public R updateDeliver(Deliver deliver, Long wareHouseIdStart, Long wareHouseIdEnd) {

        R r = null;

        Deliver oldDeliver = this.getById(deliver.getId());

        if(ObjectUtils.isNotNull(oldDeliver)){
            WareHouse wareHouseStart = warehouseService.getOne(new QueryWrapper<WareHouse>().eq("ware_houses", oldDeliver.getCity()));
            WareHouse wareHouseEnd = warehouseService.getOne(new QueryWrapper<WareHouse>().eq("ware_houses", oldDeliver.getDestination()));

            if(ObjectUtils.isNotNull(wareHouseStart)){
                wareHouseIdStart = Long.valueOf(wareHouseStart.getId());
            }

            if(ObjectUtils.isNotNull(wareHouseIdEnd)){
                wareHouseIdEnd = Long.valueOf(wareHouseEnd.getId());
            }

            if(Integer.valueOf(2).equals(oldDeliver.getState())
                    || Integer.valueOf(3).equals(oldDeliver.getState())){
                //product 型号
                //quantity 数量
                if(!oldDeliver.getProduct().equals(deliver.getProduct())
                        || !oldDeliver.getQuantity().equals(deliver.getQuantity())){

                    return R.fail("已发货或已到达的订单不可改变产品型号和数量");
                }

                if(Integer.valueOf(1).equals(deliver.getState())){
                    return R.fail("已发货或已到达的订单不可改变物流状态为未发货");
                }
            }else{
                r = saveWareHouseDetails(deliver, wareHouseIdStart, wareHouseIdEnd);
            }
        }
        if(r == null){
            deliver.setCreateTime(System.currentTimeMillis());
            r = R.ok(updateById(deliver));
        }
        return r;
    }

    private R saveWareHouseDetails(Deliver deliver,  Long wareHouseIdStart, Long wareHouseIdEnd){
        if( (wareHouseIdStart != null || wareHouseIdEnd != null)
                && deliver.getProduct() != null
                && deliver.getQuantity() != null){

            Long inventoryFlowBillStatus = null;
            if(wareHouseIdStart != null){
                if(wareHouseIdEnd != null){
                    inventoryFlowBillStatus = InventoryFlowBill.TYPE_CALL_DELIVERY;
                }else{
                    inventoryFlowBillStatus = InventoryFlowBill.TYPE_SALES_DELIVERY;
                }
            }

            if(wareHouseIdStart != null && ObjectUtils.isNull(warehouseService.getById(wareHouseIdStart))){
                return R.fail("未查询到起点仓库");
            }
            if(wareHouseIdEnd != null && ObjectUtils.isNull(warehouseService.getById(wareHouseIdEnd))){
                return R.fail("未查询到终点仓库");
            }
            //product  型号
            //quantity  数量
            ArrayList<Integer> productIds = JSON.parseObject(deliver.getProduct(), ArrayList.class);
            ArrayList<String> quantityIds = JSON.parseObject(deliver.getQuantity(), ArrayList.class);
            if(productIds.size() == quantityIds.size()){
                for(int i = 0; i < productIds.size(); i++){
                    if(wareHouseIdStart != null){
                        WareHouseProductDetails wareHouseProductDetails = wareHouseProductDetailsMapper.selectOne(
                                new QueryWrapper<WareHouseProductDetails>()
                                        .eq("ware_house_id", wareHouseIdStart)
                                        .eq("product_id", productIds.get(i)));
                        if(ObjectUtils.isNotNull(wareHouseProductDetails)){

                            if(wareHouseProductDetails.getStockNum() - Integer.parseInt(quantityIds.get(i)) < 0){
                                Product product = productService.getById(wareHouseProductDetails.getProductId());
                                return R.fail("仓库中【"+product.getName()+"】库存不足！库存余量：" + wareHouseProductDetails.getStockNum());
                            }

                            if(deliver.getState() == 1){
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
                            return R.fail("仓库【" + wareHouseStart.getWareHouses() + "】没有"+ (product == null ? "【未知】" : "【"+ product.getName() +"】") + "产品");
                        }
                    }

                    if(wareHouseIdEnd != null){
                        WareHouseProductDetails wareHouseProductDetails = wareHouseProductDetailsMapper.selectOne(
                                new QueryWrapper<WareHouseProductDetails>()
                                        .eq("ware_house_id", wareHouseIdEnd)
                                        .eq("product_id", productIds.get(i)));

                        if(ObjectUtils.isNotNull(wareHouseProductDetails)){
                            wareHouseProductDetails.setStockNum(wareHouseProductDetails.getStockNum() + Integer.parseInt(quantityIds.get(i)));
                            wareHouseProductDetailsMapper.updateById(wareHouseProductDetails);
                        }else{
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
            }else{
                return R.fail("产品型号与数量个数不一致,将检查后重新提交");
            }
        }
        return null;
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

    private String getDeliverStatue(Integer status){
        //物流状态 1：未发货  2：已发货  3：已到达
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
}
