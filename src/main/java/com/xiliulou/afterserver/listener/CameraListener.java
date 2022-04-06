package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.CameraInfo;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.IotCardInfo;
import com.xiliulou.afterserver.service.CameraService;
import com.xiliulou.afterserver.service.IotCardService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

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
    IotCardService iotCardService;
    ProductNewService productNewService;

    public CameraListener(CameraService cameraService, HttpServletRequest request, SupplierService supplierService, IotCardService iotCardService, ProductNewService productNewService){
        this.cameraService = cameraService;
        this.supplierService = supplierService;
        this.iotCardService = iotCardService;
        this.productNewService = productNewService;
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

        if(StringUtils.isNotBlank(cameraInfo.getCameraCard())){
            IotCard iotCard = iotCardService.queryBySn(cameraInfo.getCameraCard());
            if(Objects.isNull(iotCard)){
                throw new RuntimeException("未查询到物联网卡号【"+ cameraInfo.getCameraCard() +"】，请检查");
            }else{
                List<Camera> cameraList = cameraService.getBaseMapper().selectList(new QueryWrapper<Camera>()
                        .eq("iot_card_id", iotCard.getId()).eq("del_flag", Camera.DEL_NORMAL));
                if(CollectionUtils.isNotEmpty(cameraList)){
                    throw new RuntimeException("卡号【"+ cameraInfo.getCameraCard() +"】，已被其他摄像头绑定");
                }

                List<ProductNew> productNewList =productNewService.getBaseMapper().selectList(new QueryWrapper<ProductNew>()
                        .eq("iot_card_id", iotCard.getId())
                        .eq("type", "M")
                        .eq("del_flag", ProductNew.DEL_NORMAL));

                if(CollectionUtils.isNotEmpty(productNewList)){
                    throw new RuntimeException("卡号【"+ cameraInfo.getCameraCard() +"】，已被其他柜机绑定");
                }
            }
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
            IotCard iotCard = iotCardService.queryBySn(item.getCameraCard());
            camera.setIotCardId(iotCard.getId());
            camera.setCreateTime(System.currentTimeMillis());
            camera.setUpdateTime(System.currentTimeMillis());
            camera.setDelFlag(Camera.DEL_NORMAL);

            cameraList.add(camera);
        });

        cameraService.saveBatch(cameraList);
    }
}
