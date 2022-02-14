package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.CameraMapper;
import com.xiliulou.afterserver.service.CameraService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.CameraQuery;
import com.xiliulou.afterserver.web.vo.CameraExportExcelVo;
import com.xiliulou.afterserver.web.vo.IotCardExportExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2022/2/8 16:40
 * @mood
 */
@Service
@Slf4j
public class CameraServiceImpl extends ServiceImpl<CameraMapper, Camera> implements CameraService {
    @Autowired
    CameraMapper cameraMapper;
    @Autowired
    SupplierService supplierService;

    @Override
    public R saveOne(CameraQuery cameraQuery) {
        Camera camera = new Camera();
        BeanUtils.copyProperties(cameraQuery, camera);

        if(Objects.isNull(camera.getSerialNum())) {
            return R.fail("请填写摄像头序列号");
        }

        if(Objects.isNull(camera.getSupplierId())){
            return R.fail("请填写摄像头厂商");
        }

        Supplier supplier = supplierService.getById(camera.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查到相关摄像头厂商");
        }

        if(Objects.isNull(camera.getCameraCard())){
            return R.fail("请填写摄像头物联网卡号");
        }

        camera.setId(null);
        camera.setCreateTime(System.currentTimeMillis());
        camera.setUpdateTime(System.currentTimeMillis());
        camera.setDelFlag(Camera.DEL_NORMAL);

        this.save(camera);
        return R.ok();
    }

    @Override
    public R updateOne(CameraQuery cameraQuery) {
        Camera camera = new Camera();
        BeanUtils.copyProperties(cameraQuery, camera);

        if(Objects.isNull(cameraQuery.getId())){
            return R.fail("请填写摄像头Id");
        }

        Camera cameraOld = this.getById(cameraQuery.getId());
        if(Objects.isNull(cameraOld)){
            return R.fail("未查询到相关摄像头信息");
        }

        if(Objects.isNull(camera.getSerialNum())) {
            return R.fail("请填写摄像头序列号");
        }

        if(Objects.isNull(camera.getSupplierId())){
            return R.fail("请填写摄像头厂商");
        }

        Supplier supplier = supplierService.getById(camera.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查到相关摄像头厂商");
        }

        if(Objects.isNull(camera.getCameraCard())){
            return R.fail("请填写摄像头物联网卡号");
        }

        camera.setUpdateTime(System.currentTimeMillis());
        this.updateById(camera);
        return R.ok();
    }

    @Override
    public R deleteOne(Long id) {
        this.removeById(id);
        return R.ok();
    }

    @Override
    public Page getPage(Long offset, Long size, CameraQuery cameraQuery) {
        Page page = PageUtil.getPage(offset, size);

        LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(cameraQuery.getSerialNum()), Camera::getSerialNum, cameraQuery.getSerialNum())
                .like(Objects.nonNull(cameraQuery.getSupplierId()), Camera::getSupplierId, cameraQuery.getSupplierId())
                .like(Objects.nonNull(cameraQuery.getCameraCard()), Camera::getCameraCard, cameraQuery.getCameraCard())
                .between(Objects.nonNull(cameraQuery.getCreateTimeStart()) && Objects.nonNull(cameraQuery.getCreateTimeEnd()), Camera::getCreateTime, cameraQuery.getCreateTimeStart(), cameraQuery.getCreateTimeEnd())
                .eq(Camera::getDelFlag, Camera.DEL_NORMAL)
                .orderByDesc(Camera::getCreateTime);


        return baseMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void exportExcel(CameraQuery cameraQuery, HttpServletResponse response) {
        LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(cameraQuery.getSerialNum()), Camera::getSerialNum, cameraQuery.getSerialNum())
                .like(Objects.nonNull(cameraQuery.getSupplierId()), Camera::getSupplierId, cameraQuery.getSupplierId())
                .like(Objects.nonNull(cameraQuery.getCameraCard()), Camera::getCameraCard, cameraQuery.getCameraCard())
                .between(Objects.nonNull(cameraQuery.getCreateTimeStart()) && Objects.nonNull(cameraQuery.getCreateTimeEnd()), Camera::getCreateTime, cameraQuery.getCreateTimeStart(), cameraQuery.getCreateTimeEnd())
                .eq(Camera::getDelFlag, Camera.DEL_NORMAL)
                .orderByDesc(Camera::getCreateTime);

        List<Camera> list = baseMapper.selectList(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            throw new CustomBusinessException("未找到摄像头信息!");
        }

        List<CameraExportExcelVo> cameraExportExcelVo = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        list.stream().forEach(item -> {
            CameraExportExcelVo cameraVo = new CameraExportExcelVo();

            cameraVo.setSerialNum(item.getSerialNum());
            cameraVo.setCameraCard(item.getCameraCard());
            cameraVo.setCreateTime(simpleDateFormat.format(new Date(item.getCreateTime())));

            Supplier supplier = supplierService.getById(item.getSupplierId());
            if(Objects.nonNull(supplier)){
                cameraVo.setSupplier(supplier.getName());
            }

            cameraExportExcelVo.add(cameraVo);
        });

        String fileName = "摄像头.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, CameraExportExcelVo.class).sheet("sheet").doWrite(cameraExportExcelVo);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }
}
