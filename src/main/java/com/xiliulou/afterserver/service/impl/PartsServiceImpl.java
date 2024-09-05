package com.xiliulou.afterserver.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.MaterialBatch;
import com.xiliulou.afterserver.entity.Parts;
import com.xiliulou.afterserver.mapper.MaterialBatchMapper;
import com.xiliulou.afterserver.mapper.PartsMapper;
import com.xiliulou.afterserver.service.PartsService;
import com.xiliulou.afterserver.vo.PartsExcelVo;
import com.xiliulou.afterserver.web.query.PartsQuery;
import com.xiliulou.afterserver.web.vo.PartsVo;
import com.xiliulou.core.web.R;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
/**
 * (Parts)表服务实现类
 *
 * @author Hardy
 * @since 2022-12-15 15:02:05
 */
@Service("partsService")
@Slf4j
public class PartsServiceImpl extends ServiceImpl<PartsMapper, Parts> implements PartsService {
    @Resource
    private PartsMapper partsMapper;
    
    @Resource
    private MaterialBatchMapper materialBatchMapper;

    /**
     * 通过ID查询单条数据从DB
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public Parts queryByIdFromDB(Long id) {
        return this.partsMapper.queryById(id);
    }
    
        /**
     * 通过ID查询单条数据从缓存
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public  Parts queryByIdFromCache(Long id) {
        return null;
    }


    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<Parts> queryAllByLimit(int offset, int limit) {
        return this.partsMapper.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param parts 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Parts insert(Parts parts) {
        this.partsMapper.insertOne(parts);
        return parts;
    }

    /**
     * 修改数据
     *
     * @param parts 实例对象
     * @return 实例对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer update(Parts parts) {
       return this.partsMapper.update(parts);
       
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteById(Long id) {
        MaterialBatch materialBatch = materialBatchMapper.existsByPartsId(id);
        if(Objects.nonNull(materialBatch)) {
            return false;
        }
        return this.partsMapper.deleteById(id) > 0;
    }

    @Override
    public R queryList(Integer size, Integer offset, String name, String sn) {
        List<Parts> parts = this.partsMapper.queryList(size, offset, name, sn);
        Integer count = this.partsMapper.queryCount(size, offset, name, sn);

        Map<String, Object> result = new HashMap<>();
        result.put("data", parts);
        result.put("count", count);
        return R.ok(result);
    }

    @Override
    public R saveOne(PartsQuery partsQuery) {
        if (Objects.nonNull(partsQuery.getMaterialType()) && partsQuery.getMaterialType().length()>30) {
            return R.failMsg("物料类型不能超过30个字符");
        }
        if (Objects.nonNull(partsQuery.getMaterialAlias()) && partsQuery.getMaterialAlias().length()>50) {
            return R.failMsg("物料别名不能超过50个字符");
        }
        Parts partsBySn = queryBySn(partsQuery.getSn());
        if(Objects.nonNull(partsBySn)) {
            return R.fail("物料编码已存在，请检查");
        }

        Parts partsNameAndSpecification =  queryByNameAndSpecification(partsQuery.getName(), partsQuery.getSpecification());
        if(Objects.nonNull(partsNameAndSpecification)) {
            return R.fail("相同规格物料已存在，请检查");
        }

        Parts parts = new Parts();
        BeanUtils.copyProperties(partsQuery, parts);
        parts.setCreateTime(System.currentTimeMillis());
        parts.setUpdateTime(System.currentTimeMillis());
        parts.setDelFlag(Parts.DEL_NORMAL);
        this.partsMapper.insert(parts);
        return R.ok();
    }

    @Override
    public Parts queryByNameAndSpecification(String name, String specification) {
        return this.partsMapper.queryByNameAndSpecification(name, specification);
    }
    
    @Override
    public R listByName(String name, String sn) {
        List<Parts> parts = partsMapper.listByName(name,sn);
        return R.ok(parts);
    }
    
    @Override
    public R partsExportExcel(String sn, String name, HttpServletResponse response) {
        List<Parts> parts = this.partsMapper.queryList(3000, 0, name, sn);
        Integer count = this.partsMapper.queryCount(3000, 0, name, sn);
        
        List<PartsExcelVo> partsExcelVos = new ArrayList<>();
        parts.stream().forEach(materialInfo -> {
            PartsExcelVo partsExcelVo = new PartsExcelVo();
            BeanUtils.copyProperties(materialInfo, partsExcelVo);
            partsExcelVos.add(partsExcelVo);
        });
        
        String fileName = "配件列表.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            EasyExcel.write(outputStream, PartsExcelVo.class).sheet("sheet").doWrite(partsExcelVos);
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        return R.ok();
    }
    
    @Override
    public R updateOne(PartsQuery partsQuery) {
        if (Objects.nonNull(partsQuery.getMaterialType()) && partsQuery.getMaterialType().length()>30) {
            return R.failMsg("物料类型不能超过30个字符");
        }
        if (Objects.nonNull(partsQuery.getMaterialAlias()) && partsQuery.getMaterialAlias().length()>50) {
            return R.failMsg("物料别名不能超过50个字符");
        }
        
        Parts parts = queryByIdFromDB(partsQuery.getId());
        if(Objects.isNull(parts)) {
            return R.fail("未查询到相关物料");
        }

        Parts partsBySn = queryBySn(partsQuery.getSn());
        if(Objects.nonNull(partsBySn) && !Objects.equals(parts.getSn(), partsBySn.getSn())) {
            return R.fail("物料编码已存在，请检查");
        }

        Parts partsNameAndSpecification =  queryByNameAndSpecification(partsQuery.getName(), partsQuery.getSpecification());
        if(Objects.nonNull(partsNameAndSpecification) && !Objects.equals(partsNameAndSpecification.getId(), parts.getId())) {
            return R.fail("相同规格物料已存在，请检查");
        }
        
        // 物料批次
        MaterialBatch materialBatch = materialBatchMapper.selectByPartsId(partsQuery.getId());
        if (Objects.nonNull(materialBatch)) {
            R.fail("该物料已绑定批次，请先删除批次");
        }
        Parts updateParts = new Parts();
        BeanUtils.copyProperties(partsQuery, updateParts);
        parts.setUpdateTime(System.currentTimeMillis());
        this.partsMapper.update(updateParts);
        return R.ok();
    }

    @Override
    public R deleteOne(Long id) {
        MaterialBatch materialBatch = materialBatchMapper.existsByPartsId(id);
        if(Objects.nonNull(materialBatch)) {
            return R.fail("该物料已绑定批次，请先删除批次");
        }
        
        Parts updateParts = new Parts();
        updateParts.setId(id);
        updateParts.setUpdateTime(System.currentTimeMillis());
        updateParts.setDelFlag(Parts.DEL_DEL);
        return R.ok(update(updateParts));
    }

    @Override
    public R queryPull(Integer size, Integer offset, String name) {
        return R.ok(Optional.ofNullable(this.partsMapper.queryList(size, offset, name, null)).orElse(new ArrayList<>()).stream().map(item -> {
            PartsVo vo = new PartsVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList()));
    }

    @Override
    public Parts queryBySn(String sn) {
        return this.partsMapper.queryBySn(sn);
    }
}
