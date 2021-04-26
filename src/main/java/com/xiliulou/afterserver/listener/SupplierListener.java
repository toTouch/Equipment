package com.xiliulou.afterserver.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sun.deploy.ui.DialogTemplate;
import com.xiliulou.afterserver.config.SpringContextUtil;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.SysAreaCodeEntity;
import com.xiliulou.afterserver.export.CustomerInfo;
import com.xiliulou.afterserver.export.SupplierInfo;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.spring.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/4/25 0025 14:51
 * @Description: 供应商
 */
@Slf4j
public class SupplierListener extends AnalysisEventListener<SupplierInfo> {

    private static final int BATCH_COUNT = 100;
    List<SupplierInfo> list = new ArrayList<>();

    private SupplierService supplierService;
    private CityService cityService;
    public SupplierListener(SupplierService supplierService,CityService cityService){
        this.supplierService = supplierService;
        this.cityService = cityService;
    }



    @Override
    public void invoke(SupplierInfo supplier, AnalysisContext analysisContext) {
        log.info("供应商表导入=====解析到一条数据:{}", JSON.toJSONString(supplier));
        list.add(supplier);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();

        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("供应商表导入=====所有数据解析完成！");
    }

    /**
     * 入库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());
        ArrayList<Supplier> arrayList = new ArrayList<>();
        list.forEach(item -> {
            Supplier supplier = new Supplier();
            BeanUtils.copyProperties(item, supplier);
            supplier.setCreateTime(System.currentTimeMillis());

            if (StrUtil.isNotEmpty(item.getCity())) {
                LambdaQueryWrapper<SysAreaCodeEntity> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(SysAreaCodeEntity::getName, item.getCity());
                String code = cityService.getBaseMapper().selectOne(wrapper).getCode();
                supplier.setCity(code);
            }
            arrayList.add(supplier);
        });
        supplierService.saveBatch(arrayList);
        log.info("存储数据库成功！");
    }
}
