package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.export.CameraInfo;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.IotCardInfo;
import com.xiliulou.afterserver.service.CameraService;
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

    public CameraListener(CameraService cameraService, HttpServletRequest request){
        this.cameraService = cameraService;
        this.request = request;
    }

    @Override
    public void invoke(CameraInfo cameraInfo, AnalysisContext context) {
        log.info("摄像头表导入=====解析到一条数据:{}", JSON.toJSONString(cameraInfo));

        if(Objects.isNull(cameraInfo.getSerialNum())) {
            throw new RuntimeException("请填写摄像头序列号");
        }

        if(Objects.isNull(cameraInfo.getManufactor())){
            throw new RuntimeException("请填写摄像头厂商");
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
            camera.setManufactor(item.getManufactor());
            camera.setCameraCard(item.getCameraCard());
            camera.setCreateTime(System.currentTimeMillis());
            camera.setUpdateTime(System.currentTimeMillis());
            camera.setDelFlag(Camera.DEL_NORMAL);

            cameraList.add(camera);
        });

        cameraService.saveBatch(cameraList);
    }
}
