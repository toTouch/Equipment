package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.export.CameraInfo;
import com.xiliulou.afterserver.export.PartsInfo;
import com.xiliulou.afterserver.export.ServiceInfo;
import com.xiliulou.afterserver.listener.CameraListener;
import com.xiliulou.afterserver.listener.PartsListener;
import com.xiliulou.afterserver.listener.ServiceListener;
import com.xiliulou.afterserver.service.PartsService;
import com.xiliulou.afterserver.web.query.PartsQuery;
import com.xiliulou.core.web.R;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;

/**
 * (Parts)表控制层
 *
 * @author Hardy
 * @since 2022-12-15 15:02:06
 */
@RestController
@Slf4j
public class JsonAdminPartsController {
    /**
     * 服务对象
     */
    @Resource
    private PartsService partsService;

    @GetMapping("admin/parts")
    public R queryList(@RequestParam("size") Integer size,
                       @RequestParam("offset") Integer offset,
                       @RequestParam(value = "sn", required = false) String sn,
                       @RequestParam(value = "name", required = false) String name){
        return  partsService.queryList(size, offset, name, sn);
    }

    @PostMapping("admin/parts")
    public R saveOne(@RequestBody @Validated PartsQuery partsQuery){
        return  partsService.saveOne(partsQuery);
    }

    @PutMapping("admin/parts")
    public R updateOne(@RequestBody @Validated PartsQuery partsQuery){
        return  partsService.updateOne(partsQuery);
    }

    @DeleteMapping("admin/parts/{id}")
    public R deleteOne(@PathVariable("id")Long id){
        return  partsService.deleteOne(id);
    }

    @GetMapping("admin/parts/pull")
    public R queryPull(@RequestParam("size") Integer size,
        @RequestParam("offset") Integer offset,
        @RequestParam(value = "name", required = false) String name){
        if(Objects.isNull(offset) || offset < 0) {
            offset = 0;
        }

        if(Objects.isNull(size) || size < 0 || size > 50) {
            size = 50;
        }
        return  partsService.queryPull(size, offset, name);
    }

    /**
     * 导入
     */
    @PostMapping("admin/parts/upload")
    public R upload(MultipartFile file) throws IOException {
        ExcelReader excelReader = null;
        R result = R.ok();
        try {
            excelReader = EasyExcel.read(file.getInputStream(), PartsInfo.class, new PartsListener(partsService, result)).build();
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
        return result;
    }
    
    /**
     * 导出点位
     */
    @GetMapping("admin/parts/exportExcel")
    public R partsExportExcel( @RequestParam(value = "sn", required = false) String sn,
            @RequestParam(value = "name", required = false) String name,HttpServletResponse response){
        return this.partsService.partsExportExcel(sn,name,response);
    }
    
    /**
     * 删除
     */
    @DeleteMapping("admin/parts/delete")
    public R delete(@RequestParam(value = "sn", required = false) Long id){
        if (this.partsService.deleteById(id)) {
            return R.ok();
        }
       return R.fail("该物料已绑定批次，请先删除批次");
    }
}
