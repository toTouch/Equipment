package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.CameraInfo;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.IotCardInfo;
import com.xiliulou.afterserver.service.CameraService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2022/2/8 17:34
 * @mood
 */
@Slf4j
public class CameraListener extends AnalysisEventListener<CameraInfo> {
    private static final int BATCH_COUNT = 2000;
    List<CameraInfo> list = new ArrayList<>();

    HttpServletRequest request;
    CameraService cameraService;
    SupplierService supplierService;

    public CameraListener(CameraService cameraService, HttpServletRequest request, SupplierService supplierService){
        this.cameraService = cameraService;
        this.supplierService = supplierService;
        this.request = request;
    }

    @Override
    public void invoke(CameraInfo cameraInfo, AnalysisContext context) {
        log.info("摄像头表导入=====解析到一条数据:{}", JSON.toJSONString(cameraInfo));

        if(Objects.isNull(cameraInfo.getSerialNum())) {
            throw new RuntimeException("请填写摄像头序列号");
        }

        if(Objects.isNull(cameraInfo.getSupplier())){
            throw new RuntimeException("请填写摄像头厂商");
        }

        Supplier supplier = supplierService.getBaseMapper().selectOne(new QueryWrapper<Supplier>().eq("name", cameraInfo.getSupplier()));
        if(Objects.isNull(supplier)){
            throw new RuntimeException("未查询到相关摄像头厂商");
        }

        if(Objects.isNull(cameraInfo.getCameraCard())){
            throw new RuntimeException("请填写摄像头物联网卡号");
        }

        list.add(cameraInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("摄像头表导入=====所有数据解析完成！");
    }

    private void saveData(){
        log.info("{}条数据，开始存储数据库！", list.size());
        List<Camera> cameraList = new ArrayList<>();

        list.stream().forEach(item ->{
            Camera camera = new Camera();
            camera.setSerialNum(item.getSerialNum());
            Supplier supplier = supplierService.getBaseMapper().selectOne(new QueryWrapper<Supplier>().eq("name", item.getSupplier()));
            camera.setSupplierId(supplier.getId());
            camera.setCameraCard(item.getCameraCard());
            camera.setCreateTime(System.currentTimeMillis());
            camera.setUpdateTime(System.currentTimeMillis());
            camera.setDelFlag(Camera.DEL_NORMAL);

            cameraList.add(camera);
        });

        cameraService.saveBatch(cameraList);
    }
}
