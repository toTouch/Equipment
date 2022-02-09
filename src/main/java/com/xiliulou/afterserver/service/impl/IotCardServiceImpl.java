package com.xiliulou.afterserver.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.mapper.IotCardMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.IotCardService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class IotCardServiceImpl extends ServiceImpl<IotCardMapper, IotCard> implements IotCardService {

    @Autowired
    IotCardMapper iotCardMapper;
    @Autowired
    BatchService batchService;
    @Autowired
    SupplierService supplierService;

    @Override
    public R saveOne(IotCard iotCard) {

        if(Objects.isNull(iotCard.getSn())){
            return  R.fail("物联网卡号不能为空");
        }

        Batch batch = batchService.queryByIdFromDB(iotCard.getBatchId());
        if(Objects.isNull(batch)){
            return R.fail("未查询到相关批次号");
        }

        Supplier supplier = supplierService.getById(iotCard.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查询到相关供应商");
        }

        if(Objects.isNull(iotCard.getActivationTime())){
            return R.fail("请传入激活时间");
        }

        if(Objects.isNull(iotCard.getTermOfAlidity())){
            return R.fail("请传入有效时间");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(iotCard.getActivationTime());
        calendar.add(Calendar.YEAR, iotCard.getTermOfAlidity().intValue());
        iotCard.setExpirationTime(calendar.getTimeInMillis());

        iotCard.setCreateTime(System.currentTimeMillis());
        iotCard.setUpdateTime(System.currentTimeMillis());
        iotCard.setDelFlag(IotCard.DEL_NORMAL);
        iotCard.setExpirationFlag(0);

        Integer len = iotCardMapper.saveOne(iotCard);
        if(len != null && len > 0){
            return  R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    public R updateOne(IotCard iotCard) {

        if(Objects.isNull(iotCard.getId())){
            return R.fail("请填写物联网卡Id");
        }

        IotCard iotCardOld = this.getById(iotCard.getId());
        if(Objects.isNull(iotCardOld)){
            return R.fail("未查询到相关物联网卡信息");
        }

        if(Objects.isNull(iotCard.getSn())){
            return R.fail("物联网卡号不能为空");
        }

        Batch batch = batchService.queryByIdFromDB(iotCard.getBatchId());
        if(Objects.isNull(batch)){
            return R.fail("未查询到相关批次号");
        }

        Supplier supplier = supplierService.getById(iotCard.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查询到相关供应商");
        }

        if(Objects.isNull(iotCard.getActivationTime())){
            return R.fail("请传入激活时间");
        }

        if(Objects.isNull(iotCard.getTermOfAlidity())){
            return R.fail("请传入有效时间");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(iotCard.getActivationTime());
        calendar.add(Calendar.YEAR, iotCard.getTermOfAlidity().intValue());
        iotCard.setExpirationTime(calendar.getTimeInMillis());

        iotCard.setUpdateTime(System.currentTimeMillis());
        iotCard.setDelFlag(IotCard.DEL_NORMAL);

        Integer len = iotCardMapper.updateOne(iotCard);
        if(len != null && len > 0){
            return  R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    public R deleteOne(Long id) {
        Integer len = iotCardMapper.deleteById(id);
        if(len != null && len > 0){
            return R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    public R getPage(Long offset, Long size, IotCard iotCard) {
        expirationHandle();
        Page page = PageUtil.getPage(offset, size);
        IPage iPage = iotCardMapper.getPage(page, iotCard);

        Map result = new HashMap(2);
        result.put("data", iPage.getRecords());
        result.put("total", iPage.getTotal());
        return R.ok(result);
    }

    private void expirationHandle(){
        iotCardMapper.expirationHandle(System.currentTimeMillis());
    }
}
