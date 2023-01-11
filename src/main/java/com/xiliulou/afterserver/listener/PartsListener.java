package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.export.CameraInfo;
import com.xiliulou.afterserver.export.IotCardInfo;
import com.xiliulou.afterserver.export.PartsInfo;
import com.xiliulou.afterserver.service.PartsService;
import com.xiliulou.core.web.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zgw
 * @date 2022/12/27 16:13
 * @mood
 */
@Slf4j
public class PartsListener extends AnalysisEventListener<PartsInfo> {

    private PartsService partsService;

    private static final int BATCH_COUNT = 2000;

    List<PartsInfo> list = new ArrayList<>();

    List<String> repeatSn = new ArrayList<>();

    R r = null;

    public PartsListener(PartsService partsService, R r){
        this.partsService = partsService;
        this.r = r;
    }

    @Override
    public void invoke(PartsInfo partsInfo, AnalysisContext context) {
        log.info("物料表导入=====解析到一条数据:{}", JSON.toJSONString(partsInfo));
        ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)context;

        Parts parts = partsService.queryBySn(partsInfo.getSn());
        if(Objects.nonNull(parts)) {
            repeatSn.add("第"+ excelDataConvertException.getRowIndex() +"行:" + partsInfo.getSn());
            return;
        }

        Parts partsByNameAndSpecification = partsService.queryByNameAndSpecification(partsInfo.getName(), partsInfo.getSpecification());
        if(Objects.nonNull(partsByNameAndSpecification)) {
            repeatSn.add("第"+ excelDataConvertException.getRowIndex() +"行:" + partsInfo.getSn());
            return;
        }

        list.add(partsInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    private void saveData() {
        List<Parts> partsList = new ArrayList<>();
        list.parallelStream().forEach(item -> {
            Parts parts = new Parts();
            parts.setName(item.getName());
            parts.setSn(item.getSn());
            parts.setPurchasePrice(item.getPurchasePrice());
            parts.setSellPrice(item.getSellPrice());
            parts.setCreateTime(System.currentTimeMillis());
            parts.setUpdateTime(System.currentTimeMillis());
            parts.setDelFlag(Parts.DEL_NORMAL);
            parts.setSpecification(item.getSpecification());
            partsList.add(parts);
        });

        partsService.saveBatch(partsList);
        if(CollectionUtils.isEmpty(repeatSn)) {
            return;
        }

        r.setCode(1);
        r.setData(repeatSn);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("物料表导入=====所有数据解析完成！");
    }

    public List<String> getRepeatSn(){
        return  this.repeatSn;
    }
}
