package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.InventoryFlowBill;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.entity.WareHouseProductDetails;
import com.xiliulou.afterserver.mapper.WareHouseProductDetailsMapper;
import com.xiliulou.afterserver.service.InventoryFlowBillService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WareHouseProductDetailsService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.WareHouseProductDetailsQuery;
import com.xiliulou.afterserver.web.query.WorkOrderQuery;
import com.xiliulou.afterserver.web.vo.WareHouseProductDetailsVo;
import com.xiliulou.afterserver.web.vo.WorkOrderVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2021/9/17 18:19
 * @mood
 */
@Service
@Slf4j
public class WareHouseProductDetailsServiceImpl extends ServiceImpl<WareHouseProductDetailsMapper, WareHouseProductDetails> implements WareHouseProductDetailsService {
    @Autowired
    ProductService productService;

    @Autowired
    WarehouseService warehouseService;

    @Autowired
    WareHouseProductDetailsMapper  wareHouseProductDetailsMapper;

    @Autowired
    InventoryFlowBillService inventoryFlowBillService;

    @Override
    public IPage getPage(Long offset, Long size, WareHouseProductDetailsQuery wareHouseProductDetailsQuery) {

        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, wareHouseProductDetailsQuery);

        List<WareHouseProductDetails> list = (List<WareHouseProductDetails>)page.getRecords();
        List<WareHouseProductDetailsVo> pageList = new ArrayList<>(list.size());
        if (list.isEmpty()){
            return page;
        }

        list.forEach(resp -> {
            if(Objects.nonNull(resp.getProductId())){
                WareHouseProductDetailsVo wareHouseProductDetailsVo = new WareHouseProductDetailsVo();
                BeanUtils.copyProperties(resp, wareHouseProductDetailsVo);

                Product product = productService.getById(resp.getProductId());
                if(Objects.nonNull(product)){
                    wareHouseProductDetailsVo.setProductName(product.getName());
                    wareHouseProductDetailsVo.setProductCode(product.getCode());
                }

                pageList.add(wareHouseProductDetailsVo);
            }
        });

        return page.setRecords(pageList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insert(Long wareHouseId, List<WareHouseProductDetailsQuery> list){
        if(Objects.isNull(wareHouseId)){
            R.fail("请选择仓库");
        }

        WareHouse wareHouse = warehouseService.getById(wareHouseId);
        if(Objects.isNull(wareHouse)){
            R.fail("没有找到对应仓库");
        }

        if(CollectionUtils.isEmpty(list)){
            R.fail("请至少选择一个产品");
        }

        list.forEach(item -> {

            if(item.getStockNum() > 0){

                List<WareHouseProductDetails> wareHouseProductDetailsList = wareHouseProductDetailsMapper.selectList(
                        new QueryWrapper<WareHouseProductDetails>()
                                .eq("ware_house_id",wareHouseId)
                                .eq("product_id", item.getProductId()));

                if(wareHouseProductDetailsList != null && !wareHouseProductDetailsList.isEmpty()){
                    //仓库中有该产品时
                    if(wareHouseProductDetailsList.size() > 1){
                        R.fail("数据错误");
                    }
                    //更新库存
                    WareHouseProductDetails lodWareHouseProductDetails = wareHouseProductDetailsList.get(0);
                    lodWareHouseProductDetails.setStockNum(lodWareHouseProductDetails.getStockNum() + item.getStockNum());
                    this.updateById(lodWareHouseProductDetails);

                    //添加流动订单
                    InventoryFlowBill inventoryFlowBill = new InventoryFlowBill();
                    inventoryFlowBill.setNo(RandomUtil.randomString(10));
                    inventoryFlowBill.setType(InventoryFlowBill.TYPE_PURCHASE_WAREHOUSING);
                    inventoryFlowBill.setMarkNum("+" + item.getStockNum());
                    inventoryFlowBill.setSurplusNum(String.valueOf(lodWareHouseProductDetails.getStockNum()));
                    inventoryFlowBill.setCreateTime(System.currentTimeMillis());
                    inventoryFlowBill.setWid(lodWareHouseProductDetails.getId());
                    inventoryFlowBillService.save(inventoryFlowBill);
                }else {
                    //仓库中没有该产品时
                    Long productId = item.getProductId();
                    Product product = productService.getById(productId);
                    if(Objects.nonNull(product)){
                        //创建库存
                        WareHouseProductDetails wareHouseProductDetails = new WareHouseProductDetails();
                        BeanUtil.copyProperties(item, wareHouseProductDetails);
                        wareHouseProductDetails.setWareHouseId(wareHouseId);
                        this.save(wareHouseProductDetails);
                        //添加流动订单
                        InventoryFlowBill inventoryFlowBill = new InventoryFlowBill();
                        inventoryFlowBill.setNo(RandomUtil.randomString(10));
                        inventoryFlowBill.setType(InventoryFlowBill.TYPE_PURCHASE_WAREHOUSING);
                        inventoryFlowBill.setMarkNum("+" + item.getStockNum());
                        inventoryFlowBill.setSurplusNum(String.valueOf(item.getStockNum()));
                        inventoryFlowBill.setCreateTime(System.currentTimeMillis());
                        inventoryFlowBill.setWid(wareHouseProductDetails.getId());
                        inventoryFlowBillService.save(inventoryFlowBill);
                    }
                }

            }

        });
        return R.ok();
    }
}
