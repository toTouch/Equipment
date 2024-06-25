package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.Material;
import com.xiliulou.afterserver.entity.MaterialBatch;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.MaterialBatchMapper;
import com.xiliulou.afterserver.mapper.MaterialOperationMapper;
import com.xiliulou.afterserver.mapper.MaterialTraceabilityMapper;
import com.xiliulou.afterserver.mapper.PartsMapper;
import com.xiliulou.afterserver.service.MaterialBatchService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.vo.MaterialBatchExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xiliulou.afterserver.entity.Material.BINDING;
import static com.xiliulou.afterserver.entity.Material.UN_DEL_FLAG;

/**
 * (MaterialBatch)表服务实现类
 *
 * @author zhangbozhi
 * @since 2024-06-19 15:06:15
 */
@Service("materialBatchService")
@Slf4j
public class MaterialBatchServiceImpl implements MaterialBatchService {
    
    @Resource
    private MaterialBatchMapper materialBatchMapper;
    
    @Resource
    private SupplierService supplierService;
    
    @Resource
    private MaterialTraceabilityMapper materialMapper;
    
    @Resource
    private PartsMapper partsMapper;
    
    @Autowired
    private UserService userService;
    
    
    @Autowired
    private MaterialOperationMapper materialOperationMapper;
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public MaterialBatch queryById(Integer id) {
        return this.materialBatchMapper.selectById(id);
    }
    
    /**
     * 分页查询
     *
     * @param materialBatch 筛选条件
     * @param offset        查询起始位置
     * @param size          查询条数
     * @return 查询结果
     */
    @Override
    public List<MaterialBatch> listByLimit(MaterialBatch materialBatch, Long offset, Long size) {
        
        return this.materialBatchMapper.selectPage(materialBatch, offset, size);
    }
    
    /**
     * 分页count
     *
     * @param materialBatch 筛选条件
     * @return 查询结果
     */
    @Override
    public Long count(MaterialBatch materialBatch) {
        return this.materialBatchMapper.count(materialBatch);
    }
    
    /**
     * 新增数据
     *
     * @param materialBatch 实例对象
     * @return 实例对象
     */
    @Override
    public R insert(MaterialBatch materialBatch) {
        R<Object> failMsg = insertOrUpdateBatchCheck(materialBatch);
        if (Objects.nonNull(failMsg)) {
            return failMsg;
        }
        materialBatch.setMaterialCount(0);
        materialBatch.setQualifiedCount(0);
        materialBatch.setUnqualifiedCount(0);
        materialBatch.setCreateTime(System.currentTimeMillis());
        materialBatch.setUpdateTime(System.currentTimeMillis());
        
        this.materialBatchMapper.insert(materialBatch);
        return R.ok();
    }
    
    private R<Object> insertOrUpdateBatchCheck(MaterialBatch materialBatch) {
        if (StringUtils.isNotBlank(materialBatch.getMaterialBatchNo()) && materialBatch.getMaterialBatchNo().length() > 30) {
            return R.failMsg("批次号不能超过30个字符");
        }
        if (StringUtils.isNotBlank(materialBatch.getMaterialBatchRemark()) && materialBatch.getMaterialBatchRemark().length() > 100) {
            return R.failMsg("备注不能超过100个字符");
        }
        
        if (Objects.isNull(partsMapper.queryById(materialBatch.getMaterialId()))) {
            return R.failMsg("物料型号不存在");
        }
        if (Objects.isNull(supplierService.queryById(materialBatch.getSupplierId()))) {
            return R.failMsg("供应商不存在");
        }
        if (this.materialBatchMapper.existsByPartsId(materialBatch.getMaterialId()) != null) {
            return R.failMsg("该物料已存在批次号");
        }
        return null;
    }
    
    /**
     * 修改数据
     *
     * @param materialBatch 实例对象
     * @return 实例对象
     */
    @Override
    public R update(MaterialBatch materialBatch) {
        if (Objects.nonNull(materialBatch.getQualityReportUrl())) {
            MaterialBatch updateUrl = new MaterialBatch();
            updateUrl.setId(materialBatch.getId());
            updateUrl.setQualityReportUrl(materialBatch.getQualityReportUrl());
            this.materialBatchMapper.update(materialBatch);
            return R.ok();
        }
        
        R<Object> failMsg = insertOrUpdateBatchCheck(materialBatch);
        if (Objects.nonNull(failMsg)) {
            return failMsg;
        }
        this.materialBatchMapper.update(materialBatch);
        return R.ok();
    }
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public R deleteById(Long id) {
        Material material = new Material();
        material.setId(id);
        material.setBindingStatus(BINDING);
        if (Objects.nonNull(this.materialMapper.exitsByBindingStatus(material))) {
            return R.failMsg("该批次内已有物料绑定柜机，不可删除批次");
        }
        
        return R.ok(this.materialBatchMapper.deleteByBatcherId(id));
    }
    
    
    /**
     * 通过主键逻辑删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean removeById(Integer id) {
        return this.materialBatchMapper.removeById(id) > 0;
    }
    
    /**
     * 导出Excel
     *
     * @param materialBatchQueue 参数
     * @param response           响应
     */
    @Override
    public R materialBatchExportExcel(MaterialBatch materialBatchQueue, HttpServletResponse response) {
        List<MaterialBatch> materialBatches = materialBatchMapper.selectPage(materialBatchQueue, 0L, 3000L);
        IPage page = supplierService.getPage(0L, 3000L, new Supplier());
        List<Supplier> records = page.getRecords();
        Map<Long, String> longStringMap = records.stream().collect(Collectors.toMap(Supplier::getId, Supplier::getName));
        
        List<MaterialBatchExcelVo> materialBatchExcelVos = new ArrayList<>();
        materialBatches.forEach(materialBatch -> {
            MaterialBatchExcelVo materialBatchExcelVo = new MaterialBatchExcelVo();
            BeanUtils.copyProperties(materialBatch, materialBatchExcelVo);
            materialBatchExcelVo.setSupplierName(longStringMap.get(materialBatch.getSupplierId()));
            materialBatchExcelVos.add(materialBatchExcelVo);
        });
        
        String fileName = "物料批次列表.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            EasyExcel.write(outputStream, MaterialBatchExcelVo.class).sheet("sheet").doWrite(materialBatchExcelVos);
        } catch (IOException e) {
            log.error("导出报表失败！", e);
            return R.failMsg("导出报表失败！");
        }
        
        return R.ok();
    }
    
    // 导入Excel
    @Override
    public R materialExportUpload(List<Material> materials, Long materialBatchId) {
        // 提取数据重复项
        R r = checkMaterialBatchDuplicate(materials);
        if (r.getCode() != 0) {
            return r;
        }
        // 批量入库
        MaterialBatch materialBatchQuery = materialBatchMapper.selectById(Math.toIntExact(materialBatchId));
        if (Objects.isNull(materialBatchQuery)) {
            return R.failMsg("批次号不存在");
        }
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        materials.forEach(material -> {
            material.setMaterialBatchNo(materialBatchQuery.getMaterialBatchNo());
            material.setCreateTime(System.currentTimeMillis());
            material.setUpdateTime(System.currentTimeMillis());
            material.setTenantId(userById.getThirdId());
            material.setDelFlag(UN_DEL_FLAG);
            // material.setMaterialState(PASSING);
            material.setBindingStatus(BINDING);
        });
        for (int i = 0; i < materials.size() / 200 + 1; i++) {
            materialMapper.insertBatch(materials);
        }
        
        // 更新数据
        MaterialBatch materialBatch = new MaterialBatch();
        materialBatch.setMaterialCount(materials.size());
        materialBatch.setId(Math.toIntExact(materialBatchId));
        materialBatchMapper.update(materialBatch);
        return R.ok();
    }
    
    @Override
    public List<MaterialBatch> queryByNos(Set<String> nos) {
        if (CollectionUtils.isEmpty(nos)) {
            return null;
        }
        return materialBatchMapper.queryByNos(nos);
    }
    
    @Override
    public Integer updateByMaterialBatchs(List<MaterialBatch> materialBatchesQuery) {
        materialBatchMapper.updateByMaterialBatchs(materialBatchesQuery);
        return null;
    }
    
    // 获取导入重复数据
    public R checkMaterialBatchDuplicate(List<Material> materials) {
        HashMap<String, Integer> imeiHashMap = new HashMap<String, Integer>();
        HashMap<String, Integer> atemlIDHashMap = new HashMap<String, Integer>();
        HashMap<String, Integer> snHashMap = new HashMap<String, Integer>();
        ArrayList<String> imeis = new ArrayList<>();
        ArrayList<String> atemlIDs = new ArrayList<>();
        ArrayList<String> sns = new ArrayList<>();
        
        for (Material material : materials) {
            getMaterialBatchDuplicateData(imeiHashMap, material.getImei(), imeis);
            getMaterialBatchDuplicateData(atemlIDHashMap, material.getAtmelID(), atemlIDs);
            getMaterialBatchDuplicateData(snHashMap, material.getMaterialSn(), sns);
            
            // 时间格式校验
            if (StringUtils.isNotBlank(material.getTestTime())) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    dateFormat.parse(material.getTestTime().trim());
                } catch (ParseException e) {
                    return R.failMsg("表格内测试日期非日期格式，请检查");
                }
            }
        }
        
        if (!imeis.isEmpty()) {
            return R.fail(String.valueOf(imeis), "表格内IMEL code有重复，请检查");
        }
        if (!atemlIDs.isEmpty()) {
            return R.fail(String.valueOf(atemlIDs), "表格内Atmel ID有重复，请检查");
        }
        if (!sns.isEmpty()) {
            return R.fail(String.valueOf(sns), "表格内物料SN有重复，请检查");
        }
        if (imeiHashMap.size() != materials.size()) {
            return R.failMsg("表格中物料SN有未填项，请检查");
        }
        
        return dbDuplicateCheck(imeiHashMap.keySet(), atemlIDHashMap.keySet(), snHashMap.keySet());
    }
    
    private R dbDuplicateCheck( Set<String> imeis, Set<String> atemlIDs, Set<String> sns) {
        List<Material> imeiRepeat = new ArrayList<>();
        List<Material> atemlIDRepeat = new ArrayList<>();
        List<Material> snRepeat = new ArrayList<>();
        
        for (int i = 0; i < imeis.size() / 200 + 1; i++) {
            List<String> collect = imeis.stream().skip(i * 200).limit(200).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                imeiRepeat.addAll(materialMapper.listAllByImeis(collect));
            }
        }
        if (!imeiRepeat.isEmpty()) {
            return R.fail(String.valueOf(imeiRepeat), "IMEI code已存在，请勿重复导入");
        }
        
        for (int i = 0; i < atemlIDs.size() / 200 + 1; i++) {
            List<String> collect = atemlIDs.stream().skip(i * 200).limit(200).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                atemlIDRepeat.addAll(materialMapper.ListAllByAtemlIDs(collect));
            }
        }
        if (!atemlIDRepeat.isEmpty()) {
            return R.fail(String.valueOf(atemlIDRepeat), "Atmel ID已存在，请勿重复导入");
        }
        
        for (int i = 0; i < sns.size() / 200 + 1; i++) {
            List<String> collect = sns.stream().skip(i * 200).limit(200).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                snRepeat.addAll(materialMapper.ListAllBySns(collect));
            }
        }
        if (!snRepeat.isEmpty()) {
            return R.fail(String.valueOf(snRepeat), "物料SN已存在，请勿重复导入");
        }
        return R.ok();
    }
    
    private void getMaterialBatchDuplicateData(HashMap<String, Integer> hashMap, String key, ArrayList<String> repeats) {
        if (hashMap.get(key) != null) {
            Integer value = hashMap.get(key);
            hashMap.put(key, value + 1);
            repeats.add(key);
        } else {
            hashMap.put(key, 1);
        }
    }
}
