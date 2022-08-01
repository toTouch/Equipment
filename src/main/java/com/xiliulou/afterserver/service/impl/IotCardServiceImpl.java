package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.IotCardMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.CameraService;
import com.xiliulou.afterserver.service.IotCardService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.vo.IotCardExportExcelVo;
import com.xiliulou.afterserver.web.vo.IotCardVo;
import com.xiliulou.afterserver.web.vo.PointExcelVo;
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

@Service
@Slf4j
public class IotCardServiceImpl extends ServiceImpl<IotCardMapper, IotCard> implements IotCardService {

    @Autowired
    IotCardMapper iotCardMapper;
    @Autowired
    BatchService batchService;
    @Autowired
    SupplierService supplierService;
    @Autowired
    CameraService cameraService;
    @Autowired
    ProductNewMapper productNewMapper;

    @Override
    public R saveOne(IotCard iotCard) {

        if(Objects.isNull(iotCard.getSn())){
            return  R.fail("物联网卡号不能为空");
        }

        IotCard iotCardOld = this.queryBySn(iotCard.getSn());
        if(Objects.nonNull(iotCardOld)){
            return R.fail("已添加该物联网卡号");
        }

        Batch batch = batchService.queryByIdFromDB(iotCard.getBatchId());
        if(Objects.isNull(batch)){
            return R.fail("未查询到相关批次号");
        }

        Supplier supplier = supplierService.getById(iotCard.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查询到相关供应商");
        }

        if(Objects.isNull(iotCard.getActivationTime())){
            return R.fail("请传入激活时间");
        }

        if(Objects.isNull(iotCard.getTermOfAlidity())){
            return R.fail("请传入有效时间");
        }

        if(iotCard.getTermOfAlidity() % IotCard.TERM_OF_ALIDITY_UNIT != 0){
            return R.fail("有效时间必须是六的倍数");
        }

        if(Objects.isNull(iotCard.getExpirationTime())){
            return R.fail("请传入过期时间");
        }

        iotCard.setCreateTime(System.currentTimeMillis());
        iotCard.setUpdateTime(System.currentTimeMillis());
        iotCard.setDelFlag(IotCard.DEL_NORMAL);
        iotCard.setExpirationFlag(0);

        Integer len = iotCardMapper.saveOne(iotCard);
        if(len != null && len > 0){
            return  R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    public R updateOne(IotCard iotCard) {

        if(Objects.isNull(iotCard.getId())){
            return R.fail("请填写物联网卡Id");
        }

        IotCard iotCardOld = this.getById(iotCard.getId());
        if(Objects.isNull(iotCardOld)){
            return R.fail("未查询到相关物联网卡信息");
        }

        IotCard iotCardBySn = this.queryBySn(iotCard.getSn());
        if(Objects.isNull(iotCardBySn) && !Objects.equals(iotCard.getId(), iotCardBySn.getId())){
            return R.fail("已添加该物联网卡号");
        }

        if(Objects.isNull(iotCard.getSn())){
            return R.fail("物联网卡号不能为空");
        }

        Batch batch = batchService.queryByIdFromDB(iotCard.getBatchId());
        if(Objects.isNull(batch)){
            return R.fail("未查询到相关批次号");
        }

        Supplier supplier = supplierService.getById(iotCard.getSupplierId());
        if(Objects.isNull(supplier)){
            return R.fail("未查询到相关供应商");
        }

        if(Objects.isNull(iotCard.getActivationTime())){
            return R.fail("请传入激活时间");
        }

        if(Objects.isNull(iotCard.getTermOfAlidity())){
            return R.fail("请传入有效时间");
        }

        if(iotCard.getTermOfAlidity() % IotCard.TERM_OF_ALIDITY_UNIT != 0){
            return R.fail("有效时间必须是六的倍数");
        }

        if(Objects.isNull(iotCard.getExpirationTime())){
            return R.fail("请传入过期时间");
        }

        iotCard.setUpdateTime(System.currentTimeMillis());
        iotCard.setDelFlag(IotCard.DEL_NORMAL);

        Integer len = iotCardMapper.updateOne(iotCard);
        if(len != null && len > 0){
            return  R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    public R deleteOne(Long id) {
        Integer len = iotCardMapper.deleteById(id);
        if(len != null && len > 0){
            return R.ok();
        }
        return R.fail("数据库错误");
    }

    @Override
    public R getPage(Long offset, Long size, IotCard iotCard) {
        expirationHandle();
        notExpirationHandle();
        Page page = PageUtil.getPage(offset, size);
        IPage iPage = iotCardMapper.getPage(page, iotCard);

        List<IotCard> list = iPage.getRecords();
        List<IotCardVo> data = new ArrayList<>();

        if(!CollectionUtils.isEmpty(list)){
            list.stream().forEach(item -> {
                IotCardVo iotCardVo = new IotCardVo();
                BeanUtils.copyProperties(item, iotCardVo);

                Batch batch = batchService.queryByIdFromDB(item.getBatchId());
                if(Objects.nonNull(batch)){
                    iotCardVo.setBatchName(batch.getBatchNo());
                }

                Supplier supplier = supplierService.getById(item.getSupplierId());
                if(Objects.nonNull(supplier)){
                    iotCardVo.setSupplierName(supplier.getName());
                }

                data.add(iotCardVo);
            });
        }

        Map result = new HashMap(2);
        result.put("data", data);
        result.put("total", iPage.getTotal());
        return R.ok(result);
    }

    @Override
    public void exportExcel(IotCard iotCard, HttpServletResponse response) {
        List<IotCard> iotCardList = baseMapper.iotCardList(iotCard);
        if(CollectionUtils.isEmpty(iotCardList)){
            throw new CustomBusinessException("未找到物联网卡信息!");
        }

        List<IotCardExportExcelVo> iotCardExportExcelVoList = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        iotCardList.stream().forEach(item -> {
            IotCardExportExcelVo iot = new IotCardExportExcelVo();

              iot.setSn(item.getSn());

              if(Objects.isNull(item.getSupplierId())){
                  Supplier supplier = supplierService.getById(item.getSupplierId());
                  if(Objects.nonNull(supplier)){
                      iot.setSupplier(supplier.getName());
                  }
              }

              iot.setOperator(getOperatorName(item.getOperator()));

              if(Objects.isNull(item.getBatchId())){
                Batch batch = batchService.queryByIdFromDB(item.getBatchId());
                if(Objects.nonNull(batch)){
                    iot.setOperator(batch.getBatchNo());
                }
              }

              iot.setActivationTime(simpleDateFormat.format(new Date(item.getActivationTime())));

              iot.setPackages(item.getPackages());

              iot.setTermOfAlidity(item.getTermOfAlidity() + "个月");

              iot.setActivationTime(simpleDateFormat.format(new Date(item.getActivationTime())));

              iot.setCreateTime(simpleDateFormat.format(new Date(item.getCreateTime())));

            iotCardExportExcelVoList.add(iot);
        });

        String fileName = "物联网卡.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, IotCardExportExcelVo.class).sheet("sheet").doWrite(iotCardExportExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    @Override
    public IotCard queryBySn(String iotCard) {
        return iotCardMapper.selectOne(new QueryWrapper<IotCard>().eq("sn", iotCard));
    }

    @Override
    public R snLike(String sn) {
        List<IotCard> list = iotCardMapper.selectList(new LambdaQueryWrapper<IotCard>().like(!StringUtils.isBlank(sn), IotCard::getSn, sn).eq(IotCard::getDelFlag, IotCard.DEL_NORMAL));
        List<Map> result = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(item -> {
                Map<String, Object> vo = new HashMap<>(2);
                vo.put("id",item.getId());
                vo.put("sn", item.getSn());

                result.add(vo);
            });
        }
        return R.ok(result);
    }

    @Override
    public boolean checkBind(Long id){
        Map<String, Object> map = new HashMap<>(2);
        List<Camera> cameraList = cameraService.getBaseMapper().selectList(new QueryWrapper<Camera>()
                .eq("iot_card_id", id).eq("del_flag", Camera.DEL_NORMAL));
        if(CollectionUtils.isNotEmpty(cameraList)){
            return false;
        }

        List<ProductNew> productNewList = productNewMapper.selectList(new QueryWrapper<ProductNew>()
                .eq("iot_card_id", id)
                .eq("type", "M")
                .eq("del_flag", ProductNew.DEL_NORMAL));

        if(CollectionUtils.isNotEmpty(productNewList)){
            return false;
        }
        return true;
    }

    @Override
    public R pageIotcardLikeSn(Long offset, Long size, String sn) {
        Page<IotCard> page = PageUtil.getPage(offset, size);
        page = iotCardMapper.selectPage(page,
                new LambdaQueryWrapper<IotCard>()
                        .like(!StringUtils.isBlank(sn), IotCard::getSn, sn)
                        .eq(IotCard::getDelFlag, IotCard.DEL_NORMAL)
                        .orderByDesc(IotCard::getCreateTime));

        List<IotCard> records = page.getRecords();
        List<Map> data = new ArrayList<>();

        if(CollectionUtils.isNotEmpty(records)){
            records.forEach(item -> {
                Map<String, Object> vo = new HashMap<>(2);
                vo.put("id",item.getId());
                vo.put("sn", item.getSn());

                data.add(vo);
            });
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", page.getTotal());

        return R.ok(result);
    }

    private void expirationHandle(){
        iotCardMapper.expirationHandle(System.currentTimeMillis());
    }

    private void notExpirationHandle(){
        iotCardMapper.notExpirationHandle(System.currentTimeMillis());
    }

    private String getOperatorName(Integer operator){
        String operatorName = "";
        if(Objects.equals(IotCard.OPERATOR_MOVE, operator)){
            operatorName = "移动";
        }

        if(Objects.equals(IotCard.OPERATOR_TELECOM, operator)){
            operatorName = "电信";
        }

        if(Objects.equals(IotCard.OPERATOR_UNICOM, operator)){
            operatorName = "联通";
        }

        return operatorName;
    }
}
