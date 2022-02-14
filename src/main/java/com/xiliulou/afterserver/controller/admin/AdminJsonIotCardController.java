package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.IotCardInfo;
import com.xiliulou.afterserver.listener.DeliverListener;
import com.xiliulou.afterserver.listener.IotCardListener;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.IotCardService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import com.xiliulou.afterserver.web.query.InventoryFlowBillQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@RestController
@Slf4j
public class AdminJsonIotCardController extends BaseController {

    @Autowired
    IotCardService iotCardService;
    @Autowired
    BatchService batchService;
    @Autowired
    SupplierService supplierService;

    @GetMapping("admin/iotCard/list")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, IotCard iotCard) {
        return iotCardService.getPage(offset, size, iotCard);
    }

    @PostMapping("admin/iotCard")
    public R saveOne(@RequestBody IotCard iotCard){
        if(Objects.isNull(iotCard)){
            return R.fail("参数错误");
        }
        return iotCardService.saveOne(iotCard);
    }

    @PutMapping("admin/iotCard")
    public R updateOne(@RequestBody IotCard iotCard){
        if(Objects.isNull(iotCard)){
            return R.fail("参数错误");
        }
        return iotCardService.updateOne(iotCard);
    }

    @DeleteMapping("admin/iotCard/{id}")
    public R deleteOne(@PathVariable("id") Long id){
        return iotCardService.deleteOne(id);
    }

    @PostMapping("admin/iotCard/upload")
    public R upload(MultipartFile file, HttpServletRequest request){
        ExcelReader excelReader = null;
        try {
            excelReader = EasyExcel.read(file.getInputStream(), IotCardInfo.class,new IotCardListener(iotCardService,batchService, supplierService, request)).build();
        } catch (Exception e) {
            log.error("insert iotCard error", e);
            if (e.getCause() instanceof ExcelDataConvertException) {
                ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException) e.getCause();
                String cellMsg = "";
                CellData cellData = excelDataConvertException.getCellData();
                //这里有一个celldatatype的枚举值,用来判断CellData的数据类型
                CellDataTypeEnum type = cellData.getType();
                if (type.equals(CellDataTypeEnum.NUMBER)) {
                    cellMsg = cellData.getNumberValue().toString();
                } else if (type.equals(CellDataTypeEnum.STRING)) {
                    cellMsg = cellData.getStringValue();
                } else if (type.equals(CellDataTypeEnum.BOOLEAN)) {
                    cellMsg = cellData.getBooleanValue().toString();
                }
                String errorMsg = String.format("excel表格:第%s行,第%s列,数据值为:%s,该数据值不符合要求,请检验后重新导入!<span style=\"color:red\">请检查其他的记录是否有同类型的错误!</span>", excelDataConvertException.getRowIndex() + 1, excelDataConvertException.getColumnIndex(), cellMsg);
                log.error(errorMsg);
            }
        }
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }

    @GetMapping("admin/iotCard/exportExcel")
    public void exportExcel(IotCard iotCard, HttpServletResponse response) {
        iotCardService.exportExcel(iotCard, response);
    }
}
