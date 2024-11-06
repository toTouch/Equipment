package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.xiliulou.afterserver.entity.CommonOperationRecord;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.CommonOperationRecordMapper;
import com.xiliulou.afterserver.service.CommonOperationRecordService;
import com.xiliulou.afterserver.web.vo.PointExcelVo;
import com.xiliulou.afterserver.web.vo.PointVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * (CommonOperationRecord)表服务实现类
 *
 * @author zhangbozhi
 * @since 2024-11-06 17:21:10
 */
@Service("commonOperationRecordService")
@Slf4j
public class CommonOperationRecordServiceImpl implements CommonOperationRecordService {
    
    @Resource
    private CommonOperationRecordMapper commonOperationRecordMapper;
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public CommonOperationRecord queryById(Integer id) {
        return this.commonOperationRecordMapper.selectById(id);
    }
    
    /**
     * 分页查询
     *
     * @param commonOperationRecord 筛选条件
     * @param offset                查询起始位置
     * @param size                  查询条数
     * @return 查询结果
     */
    @Override
    public List<CommonOperationRecord> listByLimit(CommonOperationRecord commonOperationRecord, Long offset, Long size) {
        return this.commonOperationRecordMapper.selectPage(commonOperationRecord, offset, size);
    }
    
    /**
     * 分页count
     *
     * @param commonOperationRecord 筛选条件
     * @return 查询结果
     */
    @Override
    public Long count(CommonOperationRecord commonOperationRecord) {
        return this.commonOperationRecordMapper.count(commonOperationRecord);
    }
    
    /**
     * 新增数据
     *
     * @param commonOperationRecord 实例对象
     * @return 实例对象
     */
    @Override
    public CommonOperationRecord insert(CommonOperationRecord commonOperationRecord) {
        this.commonOperationRecordMapper.insert(commonOperationRecord);
        return commonOperationRecord;
    }
    
    /**
     * 修改数据
     *
     * @param commonOperationRecord 实例对象
     * @return 实例对象
     */
    @Override
    public CommonOperationRecord update(CommonOperationRecord commonOperationRecord) {
        this.commonOperationRecordMapper.update(commonOperationRecord);
        return this.queryById(commonOperationRecord.getId());
    }
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.commonOperationRecordMapper.deleteById(id) > 0;
    }
    
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean removeById(Integer id) {
        return this.commonOperationRecordMapper.removeById(id) > 0;
    }
    
    /**
     * 导出报表
     * @param commonOperationRecord
     * @param response
     */
    @Override
    public void exportExcel(CommonOperationRecord commonOperationRecord, HttpServletResponse response) {
        List<CommonOperationRecord> commonOperationRecords = this.commonOperationRecordMapper.selectPage(commonOperationRecord, 0L, 3000L);
        if (ObjectUtil.isEmpty(commonOperationRecords)) {
            throw new CustomBusinessException("未找到操作记录信息!");
        }
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (CommonOperationRecord o : commonOperationRecords) {
            o.setOperationTimeExcel(simpleDateFormat.format(o.getOperationTime()));
        }
        
        String fileName = "操作记录.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, CommonOperationRecord.class).sheet("sheet").doWrite(commonOperationRecords);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
        
    }
}
