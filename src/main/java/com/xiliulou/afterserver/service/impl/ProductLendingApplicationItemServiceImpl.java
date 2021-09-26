package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.mapper.ProductIendingApplicationItemMapper;
import com.xiliulou.afterserver.mapper.ProductIendingApplicationMapper;
import com.xiliulou.afterserver.mapper.WareHouseProductDetailsMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationItemQuery;
import com.xiliulou.afterserver.web.query.ProductLendingApplicationQuery;
import com.xiliulou.afterserver.web.vo.ProductIendingApplicationVo;
import com.xiliulou.afterserver.web.vo.ProductLendingApplicationItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/9/22 9:13
 * @mood
 */
@Service
public class ProductLendingApplicationItemServiceImpl extends ServiceImpl<ProductIendingApplicationItemMapper, ProductLendingApplicationItem> implements ProductLendingApplicationItemService {

    @Autowired
    ProductIendingApplicationItemMapper productIendingApplicationItemMapper;

    @Autowired
    ProductIendingApplicationService  productIendingApplicationService;

    @Autowired
    ProductService productService;

    @Autowired
    WareHouseProductDetailsMapper wareHouseProductDetailsMapper;

    @Autowired
    InventoryFlowBillService inventoryFlowBillService;

    @Override
    public IPage getPage(Long offset, Long size, Long productLendingAppId) {

        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, productLendingAppId);

        List<ProductLendingApplicationItem> list = (List<ProductLendingApplicationItem>)page.getRecords();
        List<ProductLendingApplicationItemVo> pageList = new ArrayList<>();

        if(ObjectUtils.isNotNull(list) && !list.isEmpty()){
            list.forEach(item -> {
                ProductLendingApplicationItemVo productLendingApplicationItemVo = new ProductLendingApplicationItemVo();
                BeanUtils.copyProperties(item, productLendingApplicationItemVo);

                Product product = productService.getById(item.getProductId());
                if(ObjectUtils.isNotNull(product)){
                    productLendingApplicationItemVo.setProductName(product.getName());
                    productLendingApplicationItemVo.setProductNo(product.getCode());
                }

                pageList.add(productLendingApplicationItemVo);
            });
        }

        if (list.isEmpty()){
            return page;
        }
        return page.setRecords(pageList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R edit(Long id, Long takeNum, Long returnNum){
        if(id == null || takeNum < 0 || returnNum < 0){
            return R.fail("参数异常，请重新输入！");
        }
        if(takeNum < returnNum){
            return R.fail("归还数不可大于领取数，请重新输入！");
        }
        ProductLendingApplicationItem productLendingApplicationItem =  this.getById(id);
        if(ObjectUtils.isNotNull(productLendingApplicationItem)){

            //获取状态
            Long productLendingAppId = productLendingApplicationItem.getProductLendingAppId();
            ProductIendingApplication productIendingApplication =  productIendingApplicationService.getById(productLendingAppId);
            if(ObjectUtils.isNotNull(productIendingApplication)){
                Long status = productIendingApplication.getStatus();

                if(ProductIendingApplication.STATUS_PENDING_REVIEW.equals(status)){
                    return R.fail("该申请还在审核中，不可编辑！");
                }

                if(ProductIendingApplication.STATUS_CLOSED.equals(status)){
                    return R.fail("该申请已完结，不可编辑！");
                }
            }

            if(takeNum > productLendingApplicationItem.getApplyNum()){
                return  R.fail("领取数不能大于申请数！");
            }

            Long oldTakeNum = productLendingApplicationItem.getTakeNum();
            if(oldTakeNum != null && oldTakeNum != 0L && !oldTakeNum.equals(takeNum)){
                return R.fail("已领取数已设置，不可更改");
            }

            if(oldTakeNum != null && oldTakeNum != 0L && oldTakeNum.equals(takeNum)){
                productLendingApplicationItem.setTakeNum(takeNum);
                productLendingApplicationItem.setReturnNum(returnNum);
                this.updateById(productLendingApplicationItem);
                return R.ok();
            }

            //获取库存
            List<WareHouseProductDetails> wareHouseProductDetailsList = wareHouseProductDetailsMapper.selectList(
                    new QueryWrapper<WareHouseProductDetails>()
                    .eq("product_id", productLendingApplicationItem.getProductId())
                    .eq("ware_house_id", productIendingApplication.getWareHouseId()));

            if(ObjectUtils.isNotNull(wareHouseProductDetailsList) && !wareHouseProductDetailsList.isEmpty()){
                if(wareHouseProductDetailsList.size() > 1){
                    return R.fail("数据库错误！");
                }

                WareHouseProductDetails wareHouseProductDetails = wareHouseProductDetailsList.get(0);

                if(wareHouseProductDetails.getStockNum() < takeNum){
                    return R.fail("仓库库存不足！");
                }


                /*//库存量
                Long stockNum  = wareHouseProductDetails.getStockNum();
                Long oldTakeNum =  productLendingApplicationItem.getTakeNum() == null ? 0 : productLendingApplicationItem.getTakeNum();
                Long oldReturnNum = productLendingApplicationItem.getReturnNum() == null ? 0 : productLendingApplicationItem.getReturnNum();

                //借用柜子数
                Long currTakeNum = (takeNum - oldTakeNum > 0 ?  takeNum - oldTakeNum : 0) + (oldReturnNum - returnNum > 0 ? oldReturnNum - returnNum : 0);

                //归还柜子数
                Long currReturnNum = (returnNum - oldReturnNum > 0 ? returnNum - oldReturnNum : 0) + (oldTakeNum - takeNum > 0 ? oldTakeNum - takeNum : 0);

                //总结果数
                Long resultNum = currReturnNum - currTakeNum;

                if(resultNum > stockNum){
                    return R.fail("库存不足");
                }

                //还柜子的流水单
                if(currReturnNum > 0){
                    InventoryFlowBill inventoryFlowBill = new InventoryFlowBill();
                    inventoryFlowBill.setNo(RandomUtil.randomString(10));
                    inventoryFlowBill.setType(InventoryFlowBill.TYPE_RETURN_WAREHOUSING);
                    inventoryFlowBill.setMarkNum("+" + currReturnNum);
                    inventoryFlowBill.setSurplusNum(wareHouseProductDetails.getStockNum() + currReturnNum + "");
                    inventoryFlowBill.setCreateTime(System.currentTimeMillis());
                    inventoryFlowBill.setWid(wareHouseProductDetails.getId());

                    wareHouseProductDetails.setStockNum(wareHouseProductDetails.getStockNum() + currReturnNum);
                    inventoryFlowBillService.save(inventoryFlowBill);
                }*/

                //借用柜子的流水单

                InventoryFlowBill inventoryFlowBill = new InventoryFlowBill();
                inventoryFlowBill.setNo(RandomUtil.randomString(10));
                inventoryFlowBill.setType(InventoryFlowBill.TYPE_TAKE_DELIVERY);
                inventoryFlowBill.setMarkNum("-" + takeNum);
                inventoryFlowBill.setSurplusNum(wareHouseProductDetails.getStockNum() - takeNum + "");
                inventoryFlowBill.setCreateTime(System.currentTimeMillis());
                inventoryFlowBill.setWid(wareHouseProductDetails.getId());
                inventoryFlowBillService.save(inventoryFlowBill);

                wareHouseProductDetails.setStockNum(wareHouseProductDetails.getStockNum() - takeNum);
                wareHouseProductDetailsMapper.updateById(wareHouseProductDetails);

                productLendingApplicationItem.setTakeNum(takeNum);
                productLendingApplicationItem.setReturnNum(returnNum);
                this.updateById(productLendingApplicationItem);

                return R.ok();
            }
        }
        return R.fail("未查到该条申请记录");
    }
}
