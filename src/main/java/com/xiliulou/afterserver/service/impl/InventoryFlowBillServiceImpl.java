package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.InventoryFlowBill;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.WareHouseProductDetails;
import com.xiliulou.afterserver.mapper.InventoryFlowBillMapper;
import com.xiliulou.afterserver.service.InventoryFlowBillService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WareHouseProductDetailsService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;
import com.xiliulou.afterserver.web.vo.InventoryFlowBillVo;
import com.xiliulou.afterserver.web.vo.WareHouseProductDetailsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author zgw
 * @date 2021/9/18 13:56
 * @mood
 */
@Service
@Slf4j
public class InventoryFlowBillServiceImpl extends ServiceImpl<InventoryFlowBillMapper, InventoryFlowBill> implements InventoryFlowBillService {

    @Autowired
    WareHouseProductDetailsService wareHouseProductDetailsService;

    @Autowired
    ProductService productService;

    @Override
    public IPage getPage(Long offset, Long size, InventoryFlowBillQuery inventoryFlowBillQuery) {
        Page page = PageUtil.getPage(offset, size);
        page = baseMapper.getPage(page, inventoryFlowBillQuery);

        List<InventoryFlowBill> list = (List<InventoryFlowBill>)page.getRecords();
        List<InventoryFlowBillVo> pageList = new ArrayList<>();
        if (list.isEmpty()){
            return page;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        list.forEach(item -> {
            InventoryFlowBillVo inventoryFlowBillVo = new InventoryFlowBillVo();
            BeanUtils.copyProperties(item, inventoryFlowBillVo);

            //productName;
            WareHouseProductDetails wareHouseProductDetails = wareHouseProductDetailsService.getById(item.getWid());
            if(Objects.nonNull(wareHouseProductDetails)){
                Product product = productService.getById(wareHouseProductDetails.getProductId());
                if(Objects.nonNull(product)){
                    inventoryFlowBillVo.setProductName(product.getName());
                }
            }

            if(Objects.nonNull(item.getCreateTime())){
                String dateStr = sdf.format(new Date(item.getCreateTime()));
                inventoryFlowBillVo.setCreateTimeStr(dateStr);
            }

            if(Objects.nonNull(item.getType())){
                inventoryFlowBillVo.setTypeName(getStatusStr(item.getType()));
            }

            pageList.add(inventoryFlowBillVo);
        });

        return page.setRecords(pageList);
    }

    private String getStatusStr(Long status){

        if(InventoryFlowBill.TYPE_SALES_DELIVERY.equals(status)){
            return "销售出库";
        }else if(InventoryFlowBill.TYPE_TAKE_DELIVERY.equals(status)){
            return "领用出库";
        }else if(InventoryFlowBill.TYPE_CALL_DELIVERY.equals(status)){
            return "调拨出库";
        }else if(InventoryFlowBill.TYPE_PURCHASE_WAREHOUSING.equals(status)){
            return "采购入库";
        }else if(InventoryFlowBill.TYPE_RETURN_WAREHOUSING.equals(status)){
            return "归还入库";
        }else if(InventoryFlowBill.TYPE_CALL_WAREHOUSING.equals(status)){
            return "调拨入库";
        }else{
            return "未知类型";
        }

    }


}
