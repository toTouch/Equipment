package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.xiliulou.afterserver.entity.MaterialTraceability;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.User;
import com.xiliulou.afterserver.mapper.MaterialTraceabilityMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.service.MaterialTraceabilityService;
import com.xiliulou.afterserver.util.DataUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.util.SecurityUtils;
import com.xiliulou.afterserver.vo.MaterialTraceabilityVO;
import com.xiliulou.afterserver.web.query.MaterialTraceabilityQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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

import static com.xiliulou.afterserver.entity.MaterialTraceability.BINDING;
import static com.xiliulou.afterserver.entity.MaterialTraceability.UN_BINDING;
import static com.xiliulou.afterserver.entity.MaterialTraceability.UN_DEL_FLAG;

/**
 * 物料追溯表(MaterialTraceability)表服务实现类
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
    
    /**
     * 新增数据 PDA扫码录入
     *
     * @param materialTraceabilityQuery 实例对象
     * @return 实例对象
     */
    @Override
    public R insert(MaterialTraceabilityQuery materialTraceabilityQuery) {
        if (StringUtils.isBlank(materialTraceabilityQuery.getMaterialSn())) {
            return R.failMsg("物料编码不可以为空");
        }
        if (StringUtils.isBlank(materialTraceabilityQuery.getProductNo())) {
            return R.failMsg("资产编码不可以为空");
        }
        if (Objects.equals(materialTraceabilityQuery.getMaterialSn(), materialTraceabilityQuery.getProductNo())) {
            return R.failMsg("请勿重复扫描柜机资产编码");
        }
        ProductNew queryByMeta = productNewMapper.queryByNo(materialTraceabilityQuery.getMaterialSn());
        if (Objects.nonNull(queryByMeta)) {
            return R.failMsg("柜机不可作为物料");
        }
        ProductNew productNew = productNewMapper.queryByNo(materialTraceabilityQuery.getProductNo());
        if (Objects.isNull(productNew)) {
            return R.failMsg("资产编码不存在，请检查");
        }
        if (!Objects.equals(SecurityUtils.getUserInfo().getUid(), productNew.getSupplierId())) {
            return R.failMsg("当前柜机无权限操作");
        }
        
        MaterialTraceability materialTraceability = new MaterialTraceability();
        materialTraceability.setMaterialSn(materialTraceabilityQuery.getMaterialSn());
        materialTraceability.setBindingStatus(BINDING);
        MaterialTraceability materialTraceabilityFromQuery = this.materialTraceabilityMapper.selectByParameter(materialTraceability);
        if (Objects.nonNull(materialTraceabilityFromQuery)) {
            return R.failMsg("物料已录入，禁止重复录入");
        }
        
        materialTraceability.setProductNo(materialTraceabilityQuery.getProductNo());
        materialTraceability.setTenantId(SecurityUtils.getUid());
        materialTraceability.setCreateTime(System.currentTimeMillis());
        materialTraceability.setUpdateTime(System.currentTimeMillis());
        materialTraceability.setDelFlag(UN_DEL_FLAG);
        int insert = this.materialTraceabilityMapper.insert(materialTraceability);
        if (insert < 0) {
            return R.failMsg("物料编码绑定失败，请重新扫码");
        }
        return R.ok("物料编码: " + materialTraceability.getMaterialSn() + "录入成功");
    }
    
    /**
     * 分页查询
     *
     * @param materialTraceabilityQuery 筛选条件
     *
     * @return 查询结果
     */
    @Override
    public List<MaterialTraceability> queryByPage(MaterialTraceabilityQuery materialTraceabilityQuery, Long offset, Long size) {
        MaterialTraceability materialTraceability = new MaterialTraceability();
        BeanUtils.copyProperties(materialTraceabilityQuery, materialTraceability);
        materialTraceability.setTenantId(SecurityUtils.getUid());
        
        return this.materialTraceabilityMapper.selectListByLimit(materialTraceability, offset, size);
    }
    
    @Override
    public long queryByPageCount(MaterialTraceabilityQuery materialTraceabilityQuery) {
        MaterialTraceability materialTraceability = new MaterialTraceability();
        BeanUtils.copyProperties(materialTraceabilityQuery, materialTraceability);
        materialTraceability.setTenantId(SecurityUtils.getUid());
        return this.materialTraceabilityMapper.count(materialTraceability);
    }
    
    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public MaterialTraceability queryById(Long id) {
        return this.materialTraceabilityMapper.selectById(id);
    }
    
    
    /**
     * 修改数据
     *
     * @param materialTraceabilityQuery 实例对象
     * @return 实例对象
     */
    @Override
    public R update(MaterialTraceabilityQuery materialTraceabilityQuery) {
        if (Objects.isNull(materialTraceabilityQuery.getId())) {
            return R.failMsg("物料ID不可以为空");
        }
        if (StringUtils.isBlank(materialTraceabilityQuery.getMaterialSn())) {
            return R.failMsg("物料编码不可以为空");
        }
        ProductNew productNew = productNewMapper.queryByNo(materialTraceabilityQuery.getProductNo());
        if (Objects.isNull(productNew)) {
            return R.failMsg("资产编码不存在，请检查");
        }
        if (!Objects.equals(SecurityUtils.getUserInfo().getUid(), productNew.getSupplierId())) {
            return R.failMsg("当前柜机无权限操作");
        }
        
        MaterialTraceability materialTraceability = new MaterialTraceability();
        BeanUtils.copyProperties(materialTraceabilityQuery, materialTraceability);
        materialTraceability.setUpdateTime(System.currentTimeMillis());
        materialTraceability.setTenantId(SecurityUtils.getUid());
        if (Objects.nonNull(materialTraceabilityQuery.getProductNo())) {
            materialTraceability.setBindingStatus(BINDING);
        }
        int i = this.materialTraceabilityMapper.update(materialTraceability);
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
        if (!Objects.equals(SecurityUtils.getUserInfo().getUid(), productNew.getSupplierId())) {
            return R.failMsg("当前柜机无权限操作");
        }
        return R.ok("柜机资产编码扫描成功");
    }
    
    /**
     * 物料解绑
     *
     * @param materialTraceabilityQuery 实例对象
     * @return 实例对象
     */
    @Override
    public R materialUnbundling(MaterialTraceabilityQuery materialTraceabilityQuery) {
        MaterialTraceability materialTraceability = new MaterialTraceability();
        materialTraceability.setBindingStatus(UN_BINDING);
        materialTraceability.setId(materialTraceabilityQuery.getId());
        materialTraceability.setUpdateTime(System.currentTimeMillis());
        materialTraceability.setProductNo("");
        materialTraceability.setTenantId(SecurityUtils.getUid());
        int i = this.materialTraceabilityMapper.update(materialTraceability);
        if (i < 0) {
            return R.failMsg("物料解绑失败，请重试");
        }
        return R.ok("物料解绑成功");
    }
    
    @Override
    public R exportExcel(MaterialTraceabilityQuery materialTraceabilityQuery, HttpServletResponse response) {
        MaterialTraceability materialTraceability = new MaterialTraceability();
        BeanUtils.copyProperties(materialTraceabilityQuery, materialTraceability);
        
        long size = queryByPageCount(materialTraceabilityQuery);
        if (size > 1000) {
            return R.failMsg("导出数量超过1000条，请重新筛选");
        }
        
        this.materialTraceabilityMapper.count(materialTraceability);
        List<MaterialTraceability> materialTraceabilityList = this.queryByPage(materialTraceabilityQuery, 0L, 1000L);
        List<MaterialTraceabilityVO> materialTraceabilityVOS = new ArrayList<>();
        materialTraceabilityList.stream().forEach(materialInfo -> {
            MaterialTraceabilityVO materialTraceabilityVO = new MaterialTraceabilityVO();
            materialTraceabilityVO.setId(materialInfo.getId());
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
