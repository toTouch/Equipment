package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xiliulou.afterserver.entity.Deliver;
import com.xiliulou.afterserver.export.DeliverInfo;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.listener.DeliverListener;
import com.xiliulou.afterserver.listener.PointListener;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.service.DeliverService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 10:55
 **/
@RestController
@Slf4j
public class AdminJsonDeliverController {


    @Autowired
    DeliverService deliverService;
    @Autowired
    SupplierService supplierService;
    @Autowired
    CustomerService customerService;


    @GetMapping("admin/deliver/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, DeliverQuery deliver) {
        return R.ok(deliverService.getPage(offset, size, deliver));
    }

    @PostMapping("admin/deliver")
    public R insert(@RequestBody Deliver deliver, HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        if (Objects.isNull(uid)){
            return R.fail("用户为空");
        }
        deliver.setCreateUid(uid);
        deliver.setCreateTime(System.currentTimeMillis());
        return R.ok(deliverService.save(deliver));
    }

    @PutMapping("/admin/deliver/update/status")
    public R updateStatusFromBatch(@RequestParam(value = "ids",required = false) List<Long> ids,
                                   @RequestParam("status") Integer status){
        if (ids.isEmpty()){
            return R.fail("id不能为空");
        }

        return deliverService.updateStatusFromBatch(ids,status);

    }

    @PutMapping("admin/deliver")
    public R update(@RequestBody Deliver deliver) {
        return R.ok(deliverService.updateById(deliver));
    }

    @DeleteMapping("admin/deliver")
    public R delete(@RequestParam("id") Long id) {
        return R.ok(deliverService.removeById(id));
    }

    @GetMapping("admin/deliver/exportExcel")
    public void exportExcel(Deliver deliver, HttpServletResponse response) {
        deliverService.exportExcel(deliver, response);
    }

    /**
     * 导入
     */
    @PostMapping("admin/deliver/upload")
    public R upload(MultipartFile file,HttpServletRequest request){

        ExcelReader excelReader = null;
        try {
            try {
                excelReader = EasyExcel.read(file.getInputStream(), DeliverInfo.class,new DeliverListener(deliverService,customerService,supplierService,request)).build();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ExcelAnalysisException e) {
            e.printStackTrace();
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
}
