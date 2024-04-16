package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.Material;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.MaterialTraceabilityMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.MaterialTraceabilityService;
import com.xiliulou.afterserver.service.UserService;
import com.xiliulou.afterserver.util.DataUtil;
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
import java.util.List;
import java.util.Objects;

import static com.xiliulou.afterserver.entity.Material.BINDING;
import static com.xiliulou.afterserver.entity.Material.UN_BINDING;
import static com.xiliulou.afterserver.entity.Material.UN_DEL_FLAG;

/**
 * 物料追溯表(Material)表服务实现类
 *
 * @author makejava
 * @since 2024-03-21 11:33:12
 */
@Service("materialTraceabilityService")
@Slf4j
public class MaterialTraceabilityServiceImpl implements MaterialTraceabilityService {
    
    @Resource
    private MaterialTraceabilityMapper materialTraceabilityMapper;
    
    @Resource
    private ProductNewMapper productNewMapper;
    
    @Autowired
    private UserService userService;
    
    /**
     * 新增数据 PDA扫码录入
     *
     * @param materialQuery 实例对象
     * @return 实例对象
     */
    @Override
    public R insert(MaterialQuery materialQuery) {
        if (StringUtils.isBlank(materialQuery.getMaterialSn())) {
            return R.failMsg("物料编码不可以为空");
        }
        if (StringUtils.isBlank(materialQuery.getProductNo())) {
            return R.failMsg("资产编码不可以为空");
        }
        if (Objects.equals(materialQuery.getMaterialSn(), materialQuery.getProductNo())) {
            return R.failMsg("请勿重复扫描柜机资产编码");
        }
        ProductNew queryByMeta = productNewMapper.queryByNo(materialQuery.getMaterialSn());
        if (Objects.nonNull(queryByMeta)) {
            return R.failMsg("柜机不可作为物料");
        }
        ProductNew productNew = productNewMapper.queryByNo(materialQuery.getProductNo());
        if (Objects.isNull(productNew)) {
            return R.failMsg("资产编码不存在，请检查");
        }
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (!Objects.equals(userById.getThirdId(), productNew.getSupplierId())) {
            return R.failMsg("当前柜机无权限操作");
        }
        
        Material material = new Material();
        material.setMaterialSn(materialQuery.getMaterialSn());
        material.setUpdateTime(System.currentTimeMillis());
        material.setDelFlag(UN_DEL_FLAG);
        Material materialFromQuery = this.materialTraceabilityMapper.selectByParameter(material);
        material.setBindingStatus(BINDING);
        
        if (Objects.nonNull(materialFromQuery) && StringUtils.isNotBlank(materialFromQuery.getProductNo())) {
            return R.failMsg("物料已录入，禁止重复录入");
        } else if (Objects.nonNull(materialFromQuery) && StringUtils.isBlank(materialFromQuery.getProductNo())) {
            material.setId(materialFromQuery.getId());
            material.setProductNo(materialQuery.getProductNo());
            materialTraceabilityMapper.update(material);
            return R.ok("物料编码: " + material.getMaterialSn() + "绑定成功");
        }
        
        material.setCreateTime(System.currentTimeMillis());
        material.setProductNo(materialQuery.getProductNo());
        material.setTenantId(userById.getThirdId());
        int insert = this.materialTraceabilityMapper.insert(material);
        if (insert < 0) {
            return R.failMsg("物料编码绑定失败，请重新扫码");
        }
        return R.ok("物料编码: " + material.getMaterialSn() + "录入成功");
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
    public R update(MaterialQuery materialQuery) {
        if (Objects.isNull(materialQuery.getId())) {
            return R.failMsg("物料ID不可以为空");
        }
        if (StringUtils.isBlank(materialQuery.getMaterialSn())) {
            return R.failMsg("物料编码不可以为空");
        }
        
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (StringUtils.isNotBlank(materialQuery.getProductNo())) {
            ProductNew productNew = productNewMapper.queryByNo(materialQuery.getProductNo());
            if (Objects.isNull(productNew)) {
                return R.failMsg("资产编码不存在，请检查");
            }
            
            if (!Objects.equals(userById.getUserType(), User.AFTER_USER_ROLE) && !Objects.equals(userById.getThirdId(), productNew.getSupplierId())) {
                return R.failMsg("当前柜机无权限操作");
            }
        }
        
        Material materialParamet = new Material();
        materialParamet.setMaterialSn(materialQuery.getMaterialSn());
        Material materialFromQuery = this.materialTraceabilityMapper.selectByParameter(materialParamet);
        if (Objects.nonNull(materialFromQuery) && !Objects.equals(materialFromQuery.getId(), materialQuery.getId())) {
            return R.failMsg("物料已录入，禁止重复录入");
        }
        
        Material material = new Material();
        BeanUtils.copyProperties(materialQuery, material);
        material.setUpdateTime(System.currentTimeMillis());
        material.setTenantId(userById.getThirdId());
        if (Objects.nonNull(materialQuery.getProductNo())) {
            material.setBindingStatus(BINDING);
        } else {
            material.setBindingStatus(UN_BINDING);
        }
        
        int i = this.materialTraceabilityMapper.update(material);
        return R.ok(i);
    }
    
    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Long id) {
        return this.materialTraceabilityMapper.deleteById(id) > 0;
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
    public R materialUnbundling(MaterialQuery materialQuery) {
        Material material = new Material();
        material.setBindingStatus(UN_BINDING);
        material.setId(materialQuery.getId());
        material.setUpdateTime(System.currentTimeMillis());
        material.setProductNo("");
        
        User userById = userService.getUserById(SecurityUtils.getUserInfo().getUid());
        if (!Objects.equals(userById.getUserType(), User.AFTER_USER_ROLE)) {
            material.setTenantId(userById.getThirdId());
        }
        
        int i = this.materialTraceabilityMapper.materialUnbundling(material);
        if (i < 0) {
            return R.failMsg("物料解绑失败，请重试");
        }
        return R.ok("物料解绑成功");
    }
    
    @Override
    public R exportExcel(MaterialQuery materialQuery, HttpServletResponse response) {
        Material material = new Material();
        BeanUtils.copyProperties(materialQuery, material);
        
        // 导出时间限制一个月
        if (Objects.nonNull(materialQuery.getEndTime()) && Objects.nonNull(materialQuery.getStartTime()) && (materialQuery.getEndTime() - materialQuery.getStartTime()
                > 31 * 24 * 60 * 60 * 1000L)) {
            return R.failMsg("导出时间范围超过一个月，请重新筛选");
        }
        
        List<Material> materialList = this.queryByPage(materialQuery, 0L, 1000L);
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
    
}
