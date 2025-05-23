package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.IotCard;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.CameraMapper;
import com.xiliulou.afterserver.service.CameraService;
import com.xiliulou.afterserver.service.ProductNewService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.CameraQuery;
import com.xiliulou.afterserver.web.vo.CameraExportExcelVo;
import com.xiliulou.afterserver.web.vo.IotCardExportExcelVo;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    IotCardServiceImpl iotCardService;
    @Autowired
    ProductNewService productNewService;

    @Override
    public R saveOne(CameraQuery cameraQuery) {
        Camera camera = new Camera();
        BeanUtils.copyProperties(cameraQuery, camera);

        if(Objects.isNull(camera.getSerialNum())) {
            return R.fail("请填写摄像头序列号");
        }

        Camera cameraOld = this.queryBySn(camera.getSerialNum());
        if(Objects.nonNull(cameraOld)){
            return R.fail("列表中已存在摄像头序列号");
        }

        if(Objects.isNull(camera.getSupplierId())){
            return R.fail("请填写摄像头厂商");
        }

        Supplier supplier = supplierService.getById(camera.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查到相关摄像头厂商");
        }

        if(Objects.nonNull(cameraQuery.getIotCardId())){
            List<Camera> cameraList = baseMapper.selectList(new QueryWrapper<Camera>()
                    .eq("iot_card_id", cameraQuery.getIotCardId()).eq("del_flag", Camera.DEL_NORMAL));
            if(CollectionUtils.isNotEmpty(cameraList)){
                return R.fail("物联网卡号已被其他摄像头绑定");
            }

            List<ProductNew> productNewList = productNewService.getBaseMapper().selectList(new QueryWrapper<ProductNew>()
                    .eq("iot_card_id", cameraQuery.getIotCardId())
                    .eq("type", "M")
                    .eq("del_flag", ProductNew.DEL_NORMAL));

            if(CollectionUtils.isNotEmpty(productNewList)){
                return R.fail("物联网卡号已被其他柜机绑定");
            }
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

        Camera cameraBySn = this.queryBySn(camera.getSerialNum());
        if(Objects.nonNull(cameraBySn) && !Objects.equals(cameraBySn.getId(), cameraQuery.getId())){
            return R.fail("列表中已存在摄像头序列号");
        }

        if(Objects.isNull(camera.getSupplierId())){
            return R.fail("请填写摄像头厂商");
        }

        Supplier supplier = supplierService.getById(camera.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查到相关摄像头厂商");
        }

        if(Objects.nonNull(cameraQuery.getIotCardId())){
            List<Camera> cameraList = baseMapper.selectList(new QueryWrapper<Camera>()
                    .eq("iot_card_id", cameraQuery.getIotCardId()).eq("del_flag", Camera.DEL_NORMAL).notIn("id",cameraQuery.getId()));
            if(CollectionUtils.isNotEmpty(cameraList)){
                return R.fail("物联网卡号已被其他摄像头绑定");
            }

            List<ProductNew> productNewList = productNewService.getBaseMapper().selectList(new QueryWrapper<ProductNew>()
                    .eq("iot_card_id", cameraQuery.getIotCardId())
                    .eq("type", "M")
                    .eq("del_flag", ProductNew.DEL_NORMAL));

            if(CollectionUtils.isNotEmpty(productNewList)){
                return R.fail("物联网卡号已被其他柜机绑定");
            }
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
    public R getPage(Long offset, Long size, CameraQuery cameraQuery) {
        Page<Camera> page = PageUtil.getPage(offset, size);

        LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(cameraQuery.getSerialNum()), Camera::getSerialNum, cameraQuery.getSerialNum())
                .eq(Objects.nonNull(cameraQuery.getSupplierId()), Camera::getSupplierId, cameraQuery.getSupplierId())
                .between(Objects.nonNull(cameraQuery.getCreateTimeStart()) && Objects.nonNull(cameraQuery.getCreateTimeEnd()), Camera::getCreateTime, cameraQuery.getCreateTimeStart(), cameraQuery.getCreateTimeEnd())
                .eq(Camera::getDelFlag, Camera.DEL_NORMAL)
                .orderByDesc(Camera::getCreateTime);

        page = baseMapper.selectPage(page, queryWrapper);

        if(CollectionUtils.isNotEmpty(page.getRecords())){
            page.getRecords().stream().forEach(item -> {
                Supplier supplier = supplierService.getById(item.getSupplierId());
                if(Objects.nonNull(supplier)){
                    item.setSupplierName(supplier.getName());
                }

                IotCard iotCard = iotCardService.getById(item.getIotCardId());
                if(Objects.nonNull(iotCard)){
                    item.setIotCardNo(iotCard.getSn());
                }
            });
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", page.getRecords());
        result.put("total", page.getTotal());
        return R.ok(result);
    }

    @Override
    public void exportExcel(CameraQuery cameraQuery, HttpServletResponse response) {
        LambdaQueryWrapper<Camera> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Objects.nonNull(cameraQuery.getSerialNum()), Camera::getSerialNum, cameraQuery.getSerialNum())
                .eq(Objects.nonNull(cameraQuery.getSupplierId()), Camera::getSupplierId, cameraQuery.getSupplierId())
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
            cameraVo.setCreateTime(simpleDateFormat.format(new Date(item.getCreateTime())));

            IotCard iotCard = iotCardService.getById(item.getIotCardId());
            if(Objects.nonNull(iotCard)){
                cameraVo.setCameraCard(iotCard.getSn());
            }

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

    @Override
    public Camera queryBySerialNum(String serialNum) {
        return this.getBaseMapper().selectOne(new QueryWrapper<Camera>()
                .eq("serial_num", serialNum)
                .eq("del_flag", Camera.DEL_NORMAL));
    }

    @Override
    public List<Camera> likeCameraBySerialNum(String serialNum) {
        return this.getBaseMapper().selectList(new LambdaQueryWrapper<Camera>()
                .like(StringUtils.isNotBlank(serialNum), Camera::getSerialNum, serialNum)
                .eq(Camera::getDelFlag, Camera.DEL_NORMAL));
    }

    @Override
    public R likeSerialNumPull(String serialNum) {
        List<Camera> list = likeCameraBySerialNum(serialNum);
        List<Map> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item -> {
                Map<String, Object> vo = new HashMap<>();
                vo.put("id", item.getId());
                vo.put("serialNum", item.getSerialNum());

                result.add(vo);
            });
        }

        return R.ok(result);
    }

    private  Camera queryBySn(String sn){
        return this.getBaseMapper().selectOne(new QueryWrapper<Camera>()
                .eq("serial_num", sn)
                .eq("del_flag", Camera.DEL_NORMAL));
    }

    @Override
    public R cameraSnLike(Long offset, Long size, String sn) {
        Page<Camera> page = PageUtil.getPage(offset, size);
        page = this.getBaseMapper().selectPage(page, new LambdaQueryWrapper<Camera>()
                        .like(!StringUtils.isBlank(sn),Camera::getSerialNum, sn)
                        .eq(Camera::getDelFlag, Camera.DEL_NORMAL)
                        .orderByDesc(Camera::getCreateTime));

        List<Camera> records = page.getRecords();
        List<Map> data = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(records)){
            records.forEach(item -> {
                Map<String, Object> vo = new HashMap<>();
                vo.put("id", item.getId());
                vo.put("sn", item.getSerialNum());

                data.add(vo);
            });
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", page.getTotal());
        return R.ok(result);
    }

    @Override
    public R queryCameraIotCardBySn(String sn) {
        Camera camera = this.baseMapper.selectOne(new QueryWrapper<Camera>()
                .eq("serial_num", sn)
                .eq("del_flag", Camera.DEL_NORMAL));

        Map<String, Object> result = new HashMap<>(2);
        if(Objects.nonNull(camera)){
            IotCard iotCard = iotCardService.getById(camera.getIotCardId());
            if(Objects.nonNull(iotCard)){
                result.put("cameraIotCardId", iotCard.getId());
                result.put("cameraIotCardSn", iotCard.getSn());
            }

        }
        return R.ok(result);
    }

    @Override
    public Camera getBySn(String value) {
        return this.baseMapper.selectOne(new QueryWrapper<Camera>()
                .eq("serial_num", value)
                .eq("del_flag", Camera.DEL_NORMAL));
    }
}
