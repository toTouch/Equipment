package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.ProductIendingApplicationMapper;
import com.xiliulou.afterserver.mapper.WareHouseProductDetailsMapper;
import com.xiliulou.afterserver.service.ProductIendingApplicationService;
import com.xiliulou.afterserver.service.ProductLendingApplicationItemService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationItemQuery;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationQuery;
import com.xiliulou.afterserver.web.vo.InventoryFlowBillVo;
import com.xiliulou.afterserver.web.vo.ProductIendingApplicationVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zgw
 * @date 2021/9/18 15:50
 * @mood
 */
@Service
public class ProductIendingApplicationServiceImpl extends ServiceImpl<ProductIendingApplicationMapper, ProductIendingApplication> implements ProductIendingApplicationService {

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    ProductService productService;

    @Autowired
    WareHouseProductDetailsMapper wareHouseProductDetailsMapper;

    @Autowired
    ProductLendingApplicationItemService productIendingApplicationItemService;

    @Override
    public IPage getPage(Long offset, Long size, ProductLendingApplicationQuery productLendingApplicationQuery) {
        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, productLendingApplicationQuery);

        List<ProductIendingApplication> list = (List<ProductIendingApplication>)page.getRecords();
        List<ProductIendingApplicationVo> pageList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        list.forEach((item) -> {
            ProductIendingApplicationVo productIendingApplication = new ProductIendingApplicationVo();
            BeanUtils.copyProperties(item, productIendingApplication);
            productIendingApplication.setCreateTimeStr(sdf.format(new Date(item.getCreateTime())));
            productIendingApplication.setReturnTimeStr(sdf.format(new Date(item.getReturnTime())));
            productIendingApplication.setStatusStr(this.getStatus(item.getStatus()));

            WareHouse warehouse = warehouseService.getById(item.getWareHouseId());
            if(ObjectUtils.isNotNull(warehouse)){
                productIendingApplication.setWareHouseName(warehouse.getWareHouses());
            }
            pageList.add(productIendingApplication);
        });

        if (list.isEmpty()){
            return page;
        }
        return page.setRecords(pageList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insert(ProductLendingApplicationQuery productLendingApplicationQuery, List<ProductLendingApplicationItemQuery> list) {
        WareHouse wareHouse = warehouseService.getById(productLendingApplicationQuery.getWareHouseId());
        if(Objects.isNull(wareHouse)){
            return R.fail("未查询到该仓库信息");
        }

        ProductIendingApplication productIendingApplication = new ProductIendingApplication();
        BeanUtils.copyProperties(productLendingApplicationQuery, productIendingApplication);
        productIendingApplication.setCreateTime(System.currentTimeMillis());
        productIendingApplication.setStatus(ProductIendingApplication.STATUS_PENDING_REVIEW);
        productIendingApplication.setNo(RandomUtil.randomString(10));

        this.save(productIendingApplication);


        for(ProductLendingApplicationItemQuery item : list){

            Product product = productService.getById(item.getProductId());
            if(Objects.isNull(product)){
                return R.fail("未查询到该产品信息");
            }

            List<WareHouseProductDetails> wareHouseProductDetailsList = wareHouseProductDetailsMapper.selectList(
                    new QueryWrapper<WareHouseProductDetails>()
                            .eq("product_id", item.getProductId())
                            .eq("ware_house_id", productLendingApplicationQuery.getWareHouseId()));

            if(wareHouseProductDetailsList == null || wareHouseProductDetailsList.isEmpty()){
                return R.fail("该仓库中没有此产品！");
            }

            if(wareHouseProductDetailsList.size() > 1){
                return R.fail("数据错误！");
            }

            WareHouseProductDetails wareHouseProductDetails = wareHouseProductDetailsList.get(0);
            if(wareHouseProductDetails.getStockNum()<item.getApplyNum()){
                Product p = productService.getById(wareHouseProductDetails.getProductId());
                return R.fail("您申请的商品【"+p.getName()+"】库存不足！");
            }

            ProductLendingApplicationItem productLendingApplicationItem = new ProductLendingApplicationItem();
            BeanUtils.copyProperties(item, productLendingApplicationItem);
            productLendingApplicationItem.setProductLendingAppId(productIendingApplication.getId());

            productIendingApplicationItemService.save(productLendingApplicationItem);
        }

        return R.ok();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateStatus(Long id, Long status){
        if(id == null || status == null){
            return R.fail("参数不合法");
        }
        ProductIendingApplication productIendingApplication = this.getById(id);
        if(ObjectUtils.isNotNull(productIendingApplication)){
            if(productIendingApplication.getStatus().equals(ProductIendingApplication.STATUS_EXECUTING) && ProductIendingApplication.STATUS_PENDING_REVIEW.equals(status)){
                return R.fail("该申请已经执行，不能修改为审核！");
            }
            productIendingApplication.setStatus(status);
            this.updateById(productIendingApplication);
            return R.ok();
        }
        return R.fail("未查询到该条记录");
    }

    private String getStatus(Long status){
        if(status == null){
            return "";
        }else if(status.equals(ProductIendingApplication.STATUS_PENDING_REVIEW)){
            return "待审核";
        }else if(status.equals(ProductIendingApplication.STATUS_EXECUTING)){
            return "执行中";
        }else if(status.equals(ProductIendingApplication.STATUS_CLOSED)){
            return "已完结";
        }else{
            return "";
        }
    }
}
