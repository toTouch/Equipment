package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.IotCardInfo;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Hardy
 * @date 2022/2/8 11:01
 * @mood
 */
@Slf4j
public class IotCardListener extends AnalysisEventListener<IotCardInfo> {

    private static final int BATCH_COUNT = 2000;
    List<IotCardInfo> list = new ArrayList<>();

    private IotCardService iotCardService;
    private BatchService batchService;
    private SupplierService supplierService;
    private HttpServletRequest request;

    public IotCardListener(IotCardService iotCardService, BatchService batchService, SupplierService supplierService, HttpServletRequest request){
        this.iotCardService = iotCardService;
        this.batchService = batchService;
        this.supplierService = supplierService;
        this.request = request;
    }


    @Override
    public void invoke(IotCardInfo iotCardInfo, AnalysisContext context) {
        log.info("物联网卡导入=====解析到一条数据:{}", JSON.toJSONString(iotCardInfo));

        if(Objects.isNull(iotCardInfo.getSn())){
            throw new RuntimeException("物联网卡号不能为空，请核对物联网卡号");
        }

        if(Objects.nonNull(iotCardInfo.getBatchName())){
            Batch batch = batchService.queryByName(iotCardInfo.getBatchName());
            if(Objects.isNull(batch)){
                throw new RuntimeException("未查询到相关批次号");
            }
        }else{
            throw new RuntimeException("批次号不能为空，请核对批次号");
        }

        if(Objects.nonNull(iotCardInfo.getSupplierName())){
            Supplier Supplier = supplierService.querySupplierName(iotCardInfo.getSupplierName());
            if(Objects.isNull(Supplier)){
                throw new RuntimeException("未查询到相关供应商");
            }
        }else{
            throw new RuntimeException("供应商不能为空，请核对供应商");
        }

        if(Objects.isNull(iotCardInfo.getActivationTime())){
            throw new RuntimeException("请传入激活时间");
        }

        if(Objects.isNull(iotCardInfo.getTermOfAlidity())){
            throw new RuntimeException("请传入有效时间");
        }
        if(iotCardInfo.getTermOfAlidity() % IotCard.TERM_OF_ALIDITY_UNIT != 0){
            throw new RuntimeException("有效时间必须为六的倍数");
        }
        list.add(iotCardInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("物联网卡导入=====所有数据解析完成！");
    }

    private void saveData(){
        log.info("{}条数据，开始存储数据库！", list.size());
        List<IotCard> iotCardList = new ArrayList<>();

        list.stream().forEach(item -> {
            IotCard iotCard = new IotCard();

            iotCard.setSn(item.getSn());

            Batch batch = batchService.queryByName(item.getBatchName());
            iotCard.setBatchId(batch.getId());

            Supplier Supplier = supplierService.querySupplierName(item.getSupplierName());
            iotCard.setSupplierId(Supplier.getId());

            iotCard.setOperator(getOperator(item.getOperatorName()));

            try {
                long l = dateToStamp(item.getActivationTime());
                iotCard.setActivationTime(l);

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(l);
                calendar.add(Calendar.MONTH, item.getTermOfAlidity().intValue());
                iotCard.setExpirationTime(calendar.getTimeInMillis());
            } catch (ParseException e) {
                log.error("IOT CARD INSERT TIME FORMAT ERROR!",e);
            }

            iotCard.setPackages(item.getPackages());

            iotCard.setTermOfAlidity(item.getTermOfAlidity());

            iotCard.setCreateTime(System.currentTimeMillis());
            iotCard.setUpdateTime(System.currentTimeMillis());
            iotCard.setDelFlag(IotCard.DEL_NORMAL);

            iotCardList.add(iotCard);
        });

        iotCardService.saveBatch(iotCardList);
        log.info("存储数据库成功！");
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }


    private Integer getOperator(String operator){
        if("1".equals(operator) || "中国移动".equals(operator)){
            return  1;
        }else if("2".equals(operator) || "中国电信".equals(operator)){
            return  2;
        }else if("3".equals(operator) || "中国联通".equals(operator)){
            return  3;
        }
        return null;
    }
}
