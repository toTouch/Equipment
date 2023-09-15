package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xiliulou.afterserver.controller.BaseController;
import com.xiliulou.afterserver.entity.Supplier;
import com.xiliulou.afterserver.export.SupplierInfo;
import com.xiliulou.afterserver.listener.SupplierListener;
import com.xiliulou.afterserver.service.SupplierService;
import com.xiliulou.afterserver.util.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @program: XILIULOU
 * @description: 供应商列表
 * @author: Mr.YG
 * @create: 2021-01-29 09:13
 **/
@RestController
@Slf4j
public class AdminJsonSupplierController extends BaseController {


    @Autowired
    SupplierService supplierService;

    // 匹配只有一位字符且是0-9、a-z和A-Z的情况
    Pattern CODE_PATTERN = Pattern.compile("^[0-9a-zA-Z]$");

    @GetMapping("admin/supplier/page")
    public R getPage(@RequestParam("offset") Long offset, @RequestParam("size") Long size, Supplier supplier) {
        return R.ok(supplierService.getPage(offset, size, supplier));
    }


    @PostMapping("admin/supplier")
    public R insert(@RequestBody Supplier supplier) {
        if(StringUtils.isBlank(supplier.getArea())){
            return R.fail("请添加城市");
        }
        if(StringUtils.isBlank(supplier.getSimpleName())){
            return R.fail("请添加代码");
        }else{
            Matcher matcher = CODE_PATTERN.matcher(supplier.getSimpleName());
            if (!matcher.matches()) {
                return R.fail("代码格式不正确");
            }
        }
        BaseMapper<Supplier> baseMapper = supplierService.getBaseMapper();
        Supplier supplierOld = baseMapper.selectOne(new QueryWrapper<Supplier>().eq("name", supplier.getName()));
        if(Objects.nonNull(supplierOld)){
            return R.fail("供应商列表已存在【" + supplier.getName()  + "】");
        }
        supplierOld = baseMapper.selectOne(new QueryWrapper<Supplier>().eq("simple_name", supplier.getSimpleName()));
        if(Objects.nonNull(supplierOld)){
            return R.fail("供应商代码已存在【" + supplier.getSimpleName()  + "】");
        }

        supplier.setCreateTime(System.currentTimeMillis());
        return R.ok(supplierService.save(supplier));
    }

    @PutMapping("admin/supplier")
    public R update(@RequestBody Supplier supplier) {
        if(StringUtils.isBlank(supplier.getArea())){
            return R.fail("请添加城市");
        }
        if(StringUtils.isBlank(supplier.getSimpleName())){
            return R.fail("请添加代码");
        }else{
            Matcher matcher = CODE_PATTERN.matcher(supplier.getSimpleName());
            if (!matcher.matches()) {
                return R.fail("代码格式不正确");
            }
        }
        BaseMapper<Supplier> baseMapper = supplierService.getBaseMapper();
        Supplier supplierOld = baseMapper.selectOne(new QueryWrapper<Supplier>().eq("name", supplier.getName()));
        if (Objects.nonNull(supplierOld) && !Objects.equals(supplier.getId(), supplierOld.getId())) {
            return R.fail("供应商列表已存在【" + supplier.getName() + "】");
        }
        supplierOld = baseMapper.selectOne(new QueryWrapper<Supplier>().eq("simple_name", supplier.getSimpleName()));
        if (Objects.nonNull(supplierOld) && !Objects.equals(supplier.getId(), supplierOld.getId())) {
            return R.fail("供应商代码已存在【" + supplier.getSimpleName() + "】");
        }
        return R.ok(supplierService.updateById(supplier));
    }

    @DeleteMapping("admin/supplier")
    public R delete(@RequestParam("id") Long id) {
        this.supplierService.removeById(id);
        return R.ok();
    }

    @GetMapping("admin/supplier/list")
    public R list() {
        return R.ok(supplierService.list(Wrappers.<Supplier>lambdaQuery().orderByDesc(Supplier::getCreateTime)));
    }

    /**
     * 导入
     */
    @PostMapping("admin/supplier/upload")
    public R upload(MultipartFile file) throws IOException {

        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), SupplierInfo.class,new SupplierListener(supplierService)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }


    @GetMapping("admin/supplier/search")
    public R supplierSearch(@RequestParam("offset")Long offset, @RequestParam("size")Long size,
        @RequestParam(value = "name", required = false)String name) {
        if(Objects.isNull(offset) || offset < 0) {
            offset = 0L;
        }
        if(Objects.isNull(size) || size < 0 || size > 20) {
            size = 20L;
        }

        return supplierService.supplierSearch(offset, size, name);
    }
}
