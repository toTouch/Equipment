package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.Material;
import com.xiliulou.afterserver.entity.MaterialBatch;
import com.xiliulou.afterserver.entity.MaterialDelRecord;
import com.xiliulou.afterserver.entity.MaterialOperation;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.MaterialBatchMapper;
import com.xiliulou.afterserver.mapper.MaterialDelRecordMapper;
import com.xiliulou.afterserver.mapper.MaterialOperationMapper;
import com.xiliulou.afterserver.mapper.MaterialTraceabilityMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.MaterialBatchService;
import com.xiliulou.afterserver.service.MaterialTraceabilityService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.DataUtil;
import com.xiliulou.afterserver.util.FieldCompare;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.vo.MaterialTraceabilityVO;
import com.xiliulou.afterserver.web.query.MaterialQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import static com.xiliulou.afterserver.entity.Material.BINDING;
import static com.xiliulou.afterserver.entity.Material.PASSING;
import static com.xiliulou.afterserver.entity.Material.TO_BE_INSPECTED;
import static com.xiliulou.afterserver.entity.Material.UN_BINDING;
import static com.xiliulou.afterserver.entity.Material.UN_PASSING;

/**
 * 物料追溯表(Material)表服务实现类
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */
@Service("materialTraceabilityService")
@Slf4j
public class MaterialTraceabilityServiceImpl implements MaterialTraceabilityService {
    
    private static final Long ADMIN_TENANT_ID = 0L;
    
    @Resource
    private MaterialTraceabilityMapper materialTraceabilityMapper;
    
    @Resource
    private ProductNewMapper productNewMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MaterialBatchService materialBatchService;
    
    @Autowired
    private MaterialDelRecordMapper materialDelRecordMapper;
    
    @Autowired
    private MaterialOperationMapper materialOperationMapper;
    
    @Autowired
    private MaterialBatchMapper materialBatchMapper;
    
    /**
     * 新增数据 PDA扫码录入
     *
     * @param materialQuery 实例对象
     * @return 实例对象
     */
    @Override
    public R insert(MaterialQuery materialQuery) throws Exception {
        Material materialParameter = new Material();
        materialParameter.setMaterialSn(materialQuery.getMaterialSn());

        Material materialFromQuery = this.materialTraceabilityMapper.selectByParameter(materialParameter);
        if (Objects.isNull(materialFromQuery)) {
            return R.failMsg("物料编码不存在");
        }
        if (UN_PASSING.equals(materialFromQuery.getMaterialState())) {
            return R.failMsg("物料不合格，请勿录入");
        }
        if (Objects.isNull(materialFromQuery.getMaterialState()) || TO_BE_INSPECTED.equals(materialFromQuery.getMaterialState())) {
            return R.failMsg("物料未质检，请勿录入");
        }
        if (BINDING.equals(materialFromQuery.getBindingStatus())) {
            return R.failMsg("物料已绑定柜机，请勿重复录入");
        }
        
        MaterialQuery query = new MaterialQuery();
        query.setMaterialSn(materialQuery.getMaterialSn());
        query.setProductNo(materialQuery.getProductNo());
        query.setId(materialFromQuery.getId());
        query.setMaterialState(materialFromQuery.getMaterialState());
        query.setRemark(materialFromQuery.getRemark());
        
        return updateData(query, materialFromQuery);
    }
    
    /**
     * 分页查询
     *
     * @param materialQuery 筛选条件
     * @return 查询结果
     */
    @Override
    public List<Material> queryByPage(MaterialQuery materialQuery, Long offset, Long size) {
        Material material = new Material();
        BeanUtils.copyProperties(materialQuery, material);
        
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (!Objects.equals(userById.getUserType(), User.AFTER_USER_ROLE)) {
            material.setTenantId(userById.getThirdId());
        }
        
        return this.materialTraceabilityMapper.selectListByLimit(material, offset, size);
    }
    
    @Override
    public long queryByPageCount(MaterialQuery materialQuery) {
        Material material = new Material();
        BeanUtils.copyProperties(materialQuery, material);
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (!Objects.equals(userById.getUserType(), User.AFTER_USER_ROLE)) {
            material.setTenantId(userById.getThirdId());
        }
        
        return this.materialTraceabilityMapper.count(material);
    }
    
    @Override
    public R changeMaterialState(List<Long> ids, Integer confirm, Integer status, String remark) throws Exception {
        if (CollectionUtils.isEmpty(ids)) {
            return R.failMsg("请选择物料");
        }
        
        List<Material> materials = materialTraceabilityMapper.selectListByIds(ids);
        HashMap<String, Integer> batchNoToCountMap = new HashMap<>();
        for (Material material : materials) {
            batchNoToCountMap.put(material.getMaterialBatchNo(), batchNoToCountMap.getOrDefault(material.getMaterialBatchNo(), 0) + 1);
        }
        
        List<MaterialBatch> materialBatchesQuery = materialBatchService.queryByNos(batchNoToCountMap.keySet());
      
        // 更新物料状态
        List<Material> tempMaterials = materialTraceabilityMapper.selectListByIds(ids);
        materialTraceabilityMapper.updateMaterialStateByIds(ids, System.currentTimeMillis(), remark, status);
        
        // 添加记录
        for (Material oldMaterial : tempMaterials) {
            Material newMaterial = new Material();
            newMaterial.setId(oldMaterial.getId());
            newMaterial.setRemark(remark);
            newMaterial.setMaterialState(status);
            
            record(oldMaterial, newMaterial);
        }
        return R.ok();
    }
    
    @Override
    public R queryByList(MaterialQuery materialQuery, Long offset, Long size) {
        Material material = new Material();
        BeanUtils.copyProperties(materialQuery, material);
        return R.ok(this.materialTraceabilityMapper.selectListByLimit(material, offset, size));
    }
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Material queryById(Long id) {
        return this.materialTraceabilityMapper.selectById(id);
    }
    
    
    /**
     * 修改数据
     *
     * @param materialQuery 实例对象
     * @return 实例对象
     */
    @Override
    public R update(MaterialQuery materialQuery) throws Exception {
        Material materialParameter = new Material();
        materialParameter.setMaterialSn(materialQuery.getMaterialSn());
        materialParameter.setId(materialQuery.getId());
        Material materialFromQuery = this.materialTraceabilityMapper.selectByParameter(materialParameter);
        if (Objects.isNull(materialFromQuery)) {
            return R.failMsg("物料编码不存在");
        }
        if (StringUtils.isBlank(materialQuery.getRemark()) || materialQuery.getRemark().length() > 50) {
            return R.failMsg("备注不能为空或备注长度大于50");
        }
        materialQuery.setRemark(materialQuery.getRemark().trim());
        if (Objects.equals(UN_PASSING, materialQuery.getMaterialState())
                || Objects.equals(PASSING, materialQuery.getMaterialState())) {
            return updateData(materialQuery, materialFromQuery);
        }
        return R.failMsg("合格物料不能改为待检");
        
    }
    
    
    public R updateData(MaterialQuery materialQuery, Material materialFromQuery) throws Exception {
        // 假设materialFromQuery是从PDA读取到的二维码内容
        if (Objects.isNull(materialQuery.getId())) {
            return R.failMsg("物料ID不可以为空");
        }
        if (StringUtils.isBlank(materialQuery.getMaterialSn())) {
            return R.failMsg("物料编码不可以为空");
        }
        
        String materialSN = extractMaterialSN(materialQuery.getMaterialSn());
        materialQuery.setMaterialSn(materialSN);
        
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        Material materialUpdate = new Material();
        BeanUtils.copyProperties(materialQuery, materialUpdate);
        Long thirdId = userById.getThirdId();
        if (StringUtils.isNotBlank(materialQuery.getProductNo())) {
            ProductNew productNew = productNewMapper.queryByNo(materialQuery.getProductNo());
            if (Objects.isNull(productNew)) {
                return R.failMsg("资产编码不存在，请检查");
            }
            
            if (!Objects.equals(userById.getUserType(), User.AFTER_USER_ROLE) && !Objects.equals(thirdId, productNew.getSupplierId())) {
                return R.failMsg("当前柜机无权限操作");
            }
            
            materialUpdate.setBindingStatus(BINDING);
            materialUpdate.setTenantId(productNew.getSupplierId());
            
        } else {
            materialUpdate.setBindingStatus(UN_BINDING);
            materialUpdate.setTenantId(Long.valueOf(UN_BINDING));
        }
        
        if (Objects.equals(materialQuery.getMaterialState(), UN_PASSING)) {
            materialUpdate.setBindingStatus(UN_PASSING);
            materialUpdate.setProductNo(null);
        }
        materialUpdate.setUpdateTime(System.currentTimeMillis());
        materialUpdate.setMaterialState(materialQuery.getMaterialState());
        if (Objects.isNull(materialFromQuery.getTenantId()) || Objects.equals(materialFromQuery.getTenantId(), ADMIN_TENANT_ID)) {
            thirdId = null;
        }
        
        int i = this.materialTraceabilityMapper.update(materialUpdate, thirdId);
        
        record(materialFromQuery, materialUpdate);
        
        return R.ok(i);
    }
    
    public String extractMaterialSN(String qrCodeContent) {
        // 使用分号作为分隔符，split方法会返回一个包含所有子字符串的数组
        String[] parts = qrCodeContent.split(";");
        
        // 检查是否有至少两个分号，以确保可以提取到位于第一个和第二个分号之间的内容
        if (parts.length >= 3) {
            // 第一个分号和第二个分号之间的内容是数组的第二项
            return parts[1];
        } else {
            // 如果分号数量不足，可以处理错误情况，比如返回null或者抛出异常
            return null; // 或者 throw new IllegalArgumentException("QR code content does not contain enough semicolons.");
        }
    }
    
    
    /**
     * 通过主键删除数据
     *
     * @param ids 主键列表
     * @return 是否成功
     */
    @Override
    public R deleteByIds(List<Long> ids) throws Exception {
        Material material = this.materialTraceabilityMapper.exitsByBindingStatusList(ids);
        if (Objects.nonNull(material) && ids.size() == 1) {
            return R.failMsg("物料已绑定柜机，不可删除");
        }
        if (Objects.nonNull(material)) {
            return R.failMsg("已选择项中有物料已绑定柜机，请重新选择后操作");
        }
        
        List<Material> tempMaterials = materialTraceabilityMapper.selectListByIds(ids);
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        
        for (Material oldMaterial : tempMaterials) {
            Material newMaterial = new Material();
            BeanUtils.copyProperties(oldMaterial, newMaterial);
            MaterialDelRecord materialDelRecord = new MaterialDelRecord();
            materialDelRecord.setMaterialSn(oldMaterial.getMaterialSn());
            materialDelRecord.setMaterialName(oldMaterial.getName());
            materialDelRecord.setMaterialNo(oldMaterial.getSn());
            materialDelRecord.setTenantName(userById.getUserName());
            materialDelRecord.setCreateTime(System.currentTimeMillis());
            materialDelRecordMapper.insert(materialDelRecord);
            
        }
        
        this.materialTraceabilityMapper.deleteByIds(ids,System.currentTimeMillis());
        return R.ok();
    }
    
    /**
     * 柜机 sn 校验
     *
     * @param sn
     * @return
     */
    @Override
    public R checkSn(String sn) {
        if (!Objects.equals(SecurityUtils.getUserInfo().getType(), User.TYPE_FACTORY)) {
            return R.fail("登陆用户非工厂类型");
        }
        
        ProductNew productNew = productNewMapper.queryByNo(sn);
        if (Objects.isNull(productNew)) {
            return R.failMsg("资产编码不存在，请检查");
        }
        
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (!Objects.equals(userById.getThirdId(), productNew.getSupplierId())) {
            return R.failMsg("当前柜机无权限操作");
        }
        
        List<String> materials = materialTraceabilityMapper.selectMaterialSnListBySN(sn, 0L, 100L);
        
        return R.ok(materials);
    }
    
    /**
     * 物料解绑
     *
     * @param materialQuery 实例对象
     * @return 实例对象
     */
    @Override
    public R materialUnbundling(MaterialQuery materialQuery) throws Exception {
        Material material = new Material();
        material.setId(materialQuery.getId());
        Material byParameter = materialTraceabilityMapper.selectByParameter(material);
        if (Objects.isNull(byParameter)) {
            return R.failMsg("物料不存在");
        }
        
        Material updateMaterial = new Material();
        updateMaterial.setBindingStatus(UN_BINDING);
        updateMaterial.setId(materialQuery.getId());
        updateMaterial.setUpdateTime(System.currentTimeMillis());
        updateMaterial.setProductNo("");
        
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (!Objects.equals(userById.getUserType(), User.AFTER_USER_ROLE)) {
            updateMaterial.setTenantId(userById.getThirdId());
        }
        if (!Objects.equals(byParameter.getTenantId(), userById.getThirdId())) {
            return R.failMsg("无权操作");
        }
        
        int i = this.materialTraceabilityMapper.materialUnbundling(updateMaterial);
        if (i <= 0) {
            return R.failMsg("物料解绑失败，请重试");
        }
        
        byParameter.setRemark(null);
        byParameter.setMaterialState(null);
        record(byParameter, updateMaterial);
        return R.ok("物料解绑成功");
    }
    
    // todo 返回数据就行
    @Override
    public R exportExcel(MaterialQuery materialQuery, HttpServletResponse response) {
        Material material = new Material();
        BeanUtils.copyProperties(materialQuery, material);
        
        // 导出时间限制一个月
        if (Objects.nonNull(materialQuery.getEndTime()) && Objects.nonNull(materialQuery.getStartTime()) && (materialQuery.getEndTime() - materialQuery.getStartTime()
                > 31 * 24 * 60 * 60 * 1000L)) {
            return R.failMsg("导出时间范围超过一个月，请重新筛选");
        }
        
        List<Material> materialList = this.queryByPage(materialQuery, 0L, 2000L);
        List<MaterialTraceabilityVO> materialTraceabilityVOS = new ArrayList<>();
        materialList.stream().forEach(materialInfo -> {
            MaterialTraceabilityVO materialTraceabilityVO = new MaterialTraceabilityVO();
            materialTraceabilityVO.setMaterialSn(materialInfo.getMaterialSn());
            materialTraceabilityVO.setProductNo(materialInfo.getProductNo());
            materialTraceabilityVO.setCreateTime(DataUtil.getDate(materialInfo.getCreateTime()));
            materialTraceabilityVOS.add(materialTraceabilityVO);
        });
        
        String fileName = "物料追溯记录.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            EasyExcel.write(outputStream, MaterialTraceabilityVO.class).sheet("sheet").doWrite(materialTraceabilityVOS);
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        return R.ok();
    }
    
    public void record(Material oldValue, Material newValue) throws Exception {
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (Objects.isNull(newValue)) {
            MaterialOperation materialOperation = new MaterialOperation();
            materialOperation.setMaterialId(oldValue.getId());
            materialOperation.setOperationTime(System.currentTimeMillis());
            materialOperation.setOperationContent(oldValue.getMaterialSn() + "物料已删除");
            materialOperation.setOperationAccount(userById.getUserName());
            materialOperationMapper.insert(materialOperation);
            return;
        }
        if (Objects.isNull(oldValue)) {
            return;
        }
        
        List<String> compare = FieldCompare.materialFieldCompare(newValue, oldValue);
        if (compare.isEmpty()) {
            return;
        }
        StringJoiner operationContent = new StringJoiner(",");
        compare.forEach(operationContent::add);
        
        MaterialOperation materialOperation = new MaterialOperation();
        materialOperation.setMaterialId(oldValue.getId());
        materialOperation.setOperationTime(System.currentTimeMillis());
        materialOperation.setOperationContent(operationContent.toString());
        materialOperation.setOperationAccount(userById.getUserName());
        materialOperationMapper.insert(materialOperation);
    }
}
