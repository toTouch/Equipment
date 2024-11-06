package com.xiliulou.afterserver.controller;

import com.xiliulou.afterserver.entity.CommonOperationRecord;
import com.xiliulou.afterserver.service.CommonOperationRecordService;

import com.xiliulou.afterserver.util.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * (CommonOperationRecord)表控制层
 *
 * @author zhangbozhi
 * @since 2024-11-06 17:21:05
 */
@RestController
@RequestMapping("commonOperationRecord")
public class CommonOperationRecordController {
    
    /**
     * 服务对象
     */
    @Resource
    private CommonOperationRecordService commonOperationRecordService;
    
    /**
     * 分页查询
     *
     * @param commonOperationRecord 筛选条件
     * @param offset                查询起始位置
     * @param size                  查询条数
     * @return 查询结果
     */
    @GetMapping("/page")
    public R queryByPage(CommonOperationRecord commonOperationRecord, @RequestParam("offset")Long offset, @RequestParam("size") Long size) {
        return R.ok(this.commonOperationRecordService.listByLimit(commonOperationRecord, offset, size));
    }
    
    /**
     * 分页计数
     *
     * @param commonOperationRecord 筛选条件
     * @return 查询结果
     */
    @GetMapping("/count")
    public R queryCount(CommonOperationRecord commonOperationRecord) {
        return R.ok(this.commonOperationRecordService.count(commonOperationRecord));
    }
    
    /**
     * 导出
     * @param commonOperationRecord
     * @return
     */
    @GetMapping("/exportExcel")
    public void exportExcel(CommonOperationRecord commonOperationRecord, HttpServletResponse response) {
        this.commonOperationRecordService.exportExcel(commonOperationRecord,response);
    }
   
}

