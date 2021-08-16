package com.xiliulou.afterserver.controller.admin;

import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.util.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * (Batch)表控制层
 *
 * @author Hardy
 * @since 2021-08-16 15:52:08
 */
@RestController
public class AdminJsonBatchController {
    /**
     * 服务对象
     */
    @Resource
    private BatchService batchService;

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/admin/batch/selectOne")
    public Batch selectOne(Long id) {
        return this.batchService.queryByIdFromDB(id);
    }

    /**
     * 保存
     */
    @PostMapping("/admin/batch")
    public R saveBatch(@RequestBody Batch batch){
        return R.ok(this.batchService.insert(batch));
    }

    /**
     * 修改
     */
    @PutMapping("/admin/batch")
    public R updateBatch(@RequestBody Batch batch){
        return R.ok(this.batchService.update(batch));
    }

    /**
     * 列表
     */
    @GetMapping("/admin/batch/list")
    public R selectOne(@RequestParam(value = "batchNo",required = false) String batchNo,
                       @RequestParam(value = "offset") int offset,
                       @RequestParam(value = "limit") int limit) {

        return R.ok(this.batchService.queryAllByLimit(batchNo,offset,limit));
    }
}
