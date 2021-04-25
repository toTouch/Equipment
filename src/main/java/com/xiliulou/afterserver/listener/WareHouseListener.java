package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.export.WareHouseInfo;
import com.xiliulou.afterserver.service.WarehouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hardy
 * @date 2021/4/25 0025 15:50
 * @Description:
 */
@Slf4j
public class WareHouseListener extends AnalysisEventListener<WareHouseInfo> {
    private static final int BATCH_COUNT = 100;
    List<WareHouseInfo> list = new ArrayList<>();

    WarehouseService wareHouseService;
    public WareHouseListener(WarehouseService wareHouseService){
        this.wareHouseService = wareHouseService;
    }
    @Override
    public void invoke(WareHouseInfo wareHouseInfo, AnalysisContext analysisContext) {
        log.info("仓库商表导入=====解析到一条数据:{}", JSON.toJSONString(wareHouseInfo));
        list.add(wareHouseInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();

        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("仓库商表导入=====所有数据解析完成！");
    }


    /**
     * 入库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());
        ArrayList<WareHouse> arrayList = new ArrayList<>();
        list.forEach(item -> {
            WareHouse wareHouse = new WareHouse();
            BeanUtils.copyProperties(item, wareHouse);
            wareHouse.setCreateTime(System.currentTimeMillis());
            arrayList.add(wareHouse);
        });
        wareHouseService.saveBatch(arrayList);
        log.info("存储数据库成功！");
    }
}
