package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.Material;
import com.xiliulou.afterserver.entity.MaterialBatch;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.mapper.MaterialBatchMapper;
import com.xiliulou.afterserver.mapper.MaterialOperationMapper;
import com.xiliulou.afterserver.mapper.MaterialTraceabilityMapper;
import com.xiliulou.afterserver.mapper.PartsMapper;
import com.xiliulou.afterserver.service.MaterialBatchService;
import com.xiliulou.afterserver.service.MaterialTraceabilityService;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.vo.MaterialBatchExcelVo;
import com.xiliulou.afterserver.web.query.MaterialQuery;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xiliulou.afterserver.constant.CommonConstants.FAIL;
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
    
    @Autowired
    private MaterialTraceabilityService materialTraceabilityService;
    
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
        
        List<MaterialBatch> materialBatches = this.materialBatchMapper.selectPage(materialBatch, offset, size);
        if (CollectionUtils.isEmpty(materialBatches)) {
            return materialBatches;
        }
        
        Set<Long> supplierIds = materialBatches.stream().map(MaterialBatch::getSupplierId).collect(Collectors.toSet());
        List<Supplier> supplierList = supplierService.listBySupplierIds(supplierIds);
        
        // 获取批次ID
        List<String> nos = materialBatches.stream().map(MaterialBatch::getMaterialBatchNo).collect(Collectors.toList());
        List<Material> passingList = materialMapper.selectCountListByNos(nos, Material.PASSING);
        List<Material> unPassingList = materialMapper.selectCountListByNos(nos, Material.UN_PASSING);
        List<Material> countList = materialMapper.selectCountListByNos(nos, null);
        
        Map<String, Integer> batchIdMap = passingList.stream().collect(Collectors.toMap(Material::getMaterialBatchNo, Material::getCount, (k1, k2) -> k1));
        Map<String, Integer> unbatchIdMap = unPassingList.stream().collect(Collectors.toMap(Material::getMaterialBatchNo, Material::getCount, (k1, k2) -> k1));
        Map<String, Integer> countMap = countList.stream().collect(Collectors.toMap(Material::getMaterialBatchNo, Material::getCount, (k1, k2) -> k1));
        
        Map<Long, String> longStringMap = supplierList.stream().collect(Collectors.toMap(Supplier::getId, Supplier::getName, (k1, k2) -> k1));
        materialBatches.forEach(temp -> {
            temp.setSupplierName(longStringMap.get(temp.getSupplierId()));
            if (Objects.nonNull(temp.getId())) {
                temp.setQualifiedCount(Objects.isNull(batchIdMap.get(temp.getMaterialBatchNo())) ? 0 : batchIdMap.get(temp.getMaterialBatchNo()));
                temp.setUnqualifiedCount(Objects.isNull(unbatchIdMap.get(temp.getMaterialBatchNo())) ? 0 : unbatchIdMap.get(temp.getMaterialBatchNo()));
                temp.setMaterialCount(Objects.isNull(countMap.get(temp.getMaterialBatchNo())) ? 0 : countMap.get(temp.getMaterialBatchNo()));
            }
            
        });
        
        return materialBatches;
        
        
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
        MaterialBatch materialBatchSelect = this.materialBatchMapper.existsByBatchNo(materialBatch.getMaterialBatchNo());
        if (Objects.nonNull(materialBatchSelect)) {
            return R.failMsg("该物料已存在批次号");
        }
        materialBatch.setMaterialCount(0);
        materialBatch.setQualifiedCount(0);
        materialBatch.setUnqualifiedCount(0);
        materialBatch.setCreateTime(System.currentTimeMillis());
        materialBatch.setUpdateTime(System.currentTimeMillis());
        
        if (Objects.nonNull(materialBatch.getMaterialBatchRemark())) {
            materialBatch.setMaterialBatchRemark(materialBatch.getMaterialBatchRemark().trim());
        }
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
        
        if (shaded.org.apache.commons.lang3.StringUtils.isBlank(materialBatch.getMaterialBatchNo())) {
            return R.failMsg("批次号不能为空");
        }
        
        R<Object> failMsg = insertOrUpdateBatchCheck(materialBatch);
        // 批次号重复
        MaterialBatch materialBatchSelect = this.materialBatchMapper.existsByBatchNo(materialBatch.getMaterialBatchNo());
        if (Objects.nonNull(materialBatchSelect) && !Objects.equals(materialBatchSelect.getId(), materialBatch.getId())) {
            return R.failMsg("该物料已存在批次号");
        }
        
        if (Objects.nonNull(failMsg)) {
            return failMsg;
        }
        if (Objects.nonNull(materialBatch.getMaterialBatchRemark())) {
            materialBatch.setMaterialBatchRemark(materialBatch.getMaterialBatchRemark().trim());
        }
        this.materialBatchMapper.update(materialBatch);
        return R.ok();
    }
    
    /**
     * 物料批次通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public R deleteById(Long id) {
        MaterialBatch materialBatch = materialBatchMapper.selectById(Math.toIntExact(id));
        
        Material material = new Material();
        material.setMaterialBatchNo(materialBatch.getMaterialBatchNo());
        material.setBindingStatus(BINDING);
        Material byBindingStatus = this.materialMapper.exitsByBindingStatus(material);
        if (Objects.nonNull(byBindingStatus) && StringUtils.isNotBlank(byBindingStatus.getProductNo())) {
            return R.failMsg("该批次内已有物料绑定柜机，不可删除批次");
        }
        
        // 删除物料
        this.materialMapper.deleteByMaterialBatchNo(material);
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
        List<MaterialBatch> materialBatches = new ArrayList<>();
        Long count = count(materialBatchQueue);
        for (int i = 0; i < count / 200 + 1; i++) {
            materialBatches.addAll(listByLimit(materialBatchQueue, i * 200L, 200L));
        }
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
    public R materialExportUpload(List<MaterialQuery> materials, Long materialBatchId) {
        // 提取数据重复项
        List<Material> materialList = new ArrayList<>();
        R r = checkMaterialBatchDuplicate(materials, materialList);
        if (r.getCode() != 0) {
            return r;
        }
        
        MaterialBatch materialBatchQuery = materialBatchMapper.selectById(Math.toIntExact(materialBatchId));
        if (Objects.isNull(materialBatchQuery)) {
            return R.failMsg("批次号不存在");
        }
        
        materialList.forEach(material -> {
            material.setMaterialBatchNo(materialBatchQuery.getMaterialBatchNo());
            material.setMaterialId(materialBatchQuery.getMaterialId());
            material.setDelFlag(UN_DEL_FLAG);
            material.setBindingStatus(UN_DEL_FLAG);
            material.setCreateTime(System.currentTimeMillis());
            material.setUpdateTime(System.currentTimeMillis());
            material.setTenantId(0L);
        });
        // 批量入库
        materialMapper.insertBatch(materialList);
        
        
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
        return materialBatchMapper.updateByMaterialBatchs(materialBatchesQuery);
    }
    
    
    // 获取导入重复数据
    public R checkMaterialBatchDuplicate(List<MaterialQuery> materials, List<Material> materialList) {
        HashMap<String, Integer> imeiHashMap = new HashMap<String, Integer>();
        HashMap<String, Integer> atemlIDHashMap = new HashMap<String, Integer>();
        HashMap<String, Integer> snHashMap = new HashMap<String, Integer>();
        ArrayList<String> imeis = new ArrayList<>();
        ArrayList<String> atemlIDs = new ArrayList<>();
        ArrayList<String> sns = new ArrayList<>();
        
        for (MaterialQuery materialQuery : materials) {
            getMaterialBatchDuplicateData(imeiHashMap, materialQuery.getImei(), imeis);
            getMaterialBatchDuplicateData(atemlIDHashMap, materialQuery.getAtmelID(), atemlIDs);
            getMaterialBatchDuplicateData(snHashMap, materialQuery.getMaterialSn(), sns);
            
            Material material = new Material();
            // 时间格式校验
            if (StringUtils.isNotBlank(materialQuery.getTestTime())) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date parse = dateFormat.parse(materialQuery.getTestTime().trim());
                    material.setTestTime(parse.getTime());
                } catch (ParseException e) {
                    return R.failMsg("表格内测试日期非日期格式，请检查");
                }
            }
            BeanUtils.copyProperties(materialQuery, material);
            if (StringUtils.isBlank(materialQuery.getImei())) {
                material.setImei("");
            }
            if (StringUtils.isBlank(materialQuery.getAtmelID())) {
                material.setAtmelID("");
            }
            if (StringUtils.isBlank(materialQuery.getMaterialSn())) {
                material.setMaterialSn("");
            }
            materialList.add(material);
        }
        
        if (!imeis.isEmpty()) {
            return R.fail(String.valueOf(imeis), FAIL.toString(), "表格内IMEL code有重复，请检查");
        }
        if (!atemlIDs.isEmpty()) {
            return R.fail(String.valueOf(atemlIDs), FAIL.toString(), "表格内Atmel ID有重复，请检查");
        }
        if (!sns.isEmpty()) {
            return R.fail(String.valueOf(sns), FAIL.toString(), "表格内物料SN有重复，请检查");
        }
        
        if (snHashMap.size() != materials.size()) {
            return R.failMsg("表格中物料SN有未填项，请检查");
        }
        
        return dbDuplicateCheck(imeiHashMap.keySet(), atemlIDHashMap.keySet(), snHashMap.keySet());
    }
    
    private R dbDuplicateCheck(Set<String> imeis, Set<String> atemlIDs, Set<String> sns) {
        List<Material> imeiRepeat = new ArrayList<>();
        List<Material> atemlIDRepeat = new ArrayList<>();
        List<Material> snRepeat = new ArrayList<>();
        HashMap<Object, Object> resultMap = new HashMap<>();
        for (int i = 0; i < imeis.size() / 200 + 1; i++) {
            List<String> collect = imeis.stream().skip(i * 200).limit(200).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                imeiRepeat.addAll(materialMapper.listAllByImeis(collect));
            }
        }
        
        if (!imeiRepeat.isEmpty()) {
            List<String> collect = imeiRepeat.stream().map(Material::getImei).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            resultMap.put("codes", collect);
        }
        
        for (int i = 0; i < atemlIDs.size() / 200 + 1; i++) {
            List<String> collect = atemlIDs.stream().skip(i * 200).limit(200).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                atemlIDRepeat.addAll(materialMapper.ListAllByAtemlIDs(collect));
            }
        }
        if (!atemlIDRepeat.isEmpty()) {
            List<String> collect = atemlIDRepeat.stream().map(Material::getAtmelID).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                resultMap.put("ids", collect);
            }// return R.fail(String.valueOf(atemlIDRepeat), "Atmel ID已存在，请勿重复导入");
        }
        
        for (int i = 0; i < sns.size() / 200 + 1; i++) {
            List<String> collect = sns.stream().skip(i * 200).limit(200).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            if (!collect.isEmpty()) {
                snRepeat.addAll(materialMapper.ListAllBySns(collect));
            }
        }
        if (!snRepeat.isEmpty()) {
            List<String> collect = snRepeat.stream().map(Material::getMaterialSn).filter(StringUtils::isNotBlank).collect(Collectors.toList());
            resultMap.put("sns", collect);
            // return R.fail(String.valueOf(snRepeat), "物料SN已存在，请勿重复导入");
        }
        if (!resultMap.isEmpty()) {
            return R.fail(resultMap, FAIL.toString(), "表格内物料信息有重复，请检查");
        }
        return R.ok();
    }
    
    private void getMaterialBatchDuplicateData(HashMap<String, Integer> hashMap, String key, ArrayList<String> repeats) {
        if (hashMap.get(key) != null && StringUtils.isNotBlank(key)) {
            Integer value = hashMap.get(key);
            hashMap.put(key, value + 1);
            repeats.add(key);
        } else {
            hashMap.put(key, 1);
        }
    }
}
