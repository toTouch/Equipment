package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.xiliulou.afterserver.entity.Batch;
import com.xiliulou.afterserver.entity.Point;
import com.xiliulou.afterserver.entity.Product;
import com.xiliulou.afterserver.entity.ProductFile;
import com.xiliulou.afterserver.entity.ProductNew;
import com.xiliulou.afterserver.entity.ProductSerialNumber;
import com.xiliulou.afterserver.entity.WareHouse;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.PointMapper;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.mapper.ProductMapper;
import com.xiliulou.afterserver.mapper.ProductNewMapper;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.service.BatchService;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.service.ProductSerialNumberService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import com.xiliulou.afterserver.web.vo.PageSearchVo;
import com.xiliulou.afterserver.web.vo.ProductExcelVo;
import com.xiliulou.afterserver.web.vo.ProductSerialNumberExcelVo;
import com.xiliulou.afterserver.web.vo.ProductSerialNumberVo;
import com.xiliulou.afterserver.web.vo.productModelPullVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:08
 **/
@Service
@Slf4j
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Autowired
    ProductSerialNumberMapper productSerialNumberMapper;
    
    @Autowired
    ProductSerialNumberService productSerialNumberService;
    
    @Autowired
    ProductService productService;
    
    @Autowired
    WarehouseService warehouseService;
    
    @Autowired
    PointService pointService;
    
    @Autowired
    BatchService batchService;
    
    @Autowired
    ProductMapper productMapper;
    
    @Autowired
    PointMapper pointMapper;
    
    @Autowired
    ProductFileMapper productFileMapper;
    
    @Autowired
    ProductNewMapper productNewMapper;
    
    @Override
    public IPage getPage(Long offset, Long size, Integer shelfStatus, Product product) {
        Page page = PageUtil.getPage(offset, size);
        Page selectPage = baseMapper.selectPage(page,
                new LambdaQueryWrapper<Product>().eq(Objects.nonNull(product.getProductSeries()), Product::getProductSeries, product.getProductSeries())
                        .like(StringUtils.isNotBlank(product.getName()), Product::getName, product.getName())
                        .eq(Objects.nonNull(product.getHasScreen()), Product::getHasScreen, product.getHasScreen())
                        .eq(Objects.nonNull(product.getFireFightingType()), Product::getFireFightingType, product.getFireFightingType())
                        .like(StringUtils.isNotBlank(product.getCode()), Product::getCode, product.getCode())
                        .like(StringUtils.isNotBlank(product.getRemarks()), Product::getRemarks, product.getRemarks())
                        .eq(Objects.nonNull(shelfStatus), Product::getShelfStatus, shelfStatus).orderByDesc(Product::getId));
        return selectPage;
    }
    
    @Override
    public IPage getPage(Long offset, Long size, String name) {
        Page page = PageUtil.getPage(offset, size);
        Page selectPage = baseMapper.selectPage(page, new LambdaQueryWrapper<Product>().like(Product::getName, name).orderByDesc(Product::getId));
        return selectPage;
    }
    
    @Override
    public IPage getAllByName(String name) {
        List<Product> product = baseMapper.selectList(new LambdaQueryWrapper<Product>().like(Product::getName, name).orderByDesc(Product::getId));
        Integer count = baseMapper.selectCount(new LambdaQueryWrapper<Product>().like(Product::getName, name));
        Page<Product> selectPage = new Page();
        selectPage.setRecords(product);
        selectPage.setTotal(count);
        selectPage.setSize(count);
        return selectPage;
    }
    
    @Override
    public void exportExcel(Product product, HttpServletResponse response) {
        List<Product> productList = baseMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Objects.nonNull(product.getProductSeries()), Product::getProductSeries, product.getProductSeries())
                        .like(StringUtils.isNotBlank(product.getName()), Product::getName, product.getName())
                        .eq(Objects.nonNull(product.getHasScreen()), Product::getHasScreen, product.getHasScreen())
                        .eq(Objects.nonNull(product.getFireFightingType()), Product::getFireFightingType, product.getFireFightingType())
                        .like(StringUtils.isNotBlank(product.getCode()), Product::getCode, product.getCode())
                        .like(StringUtils.isNotBlank(product.getRemarks()), Product::getRemarks, product.getRemarks()).orderByDesc(Product::getId));
        if (ObjectUtil.isEmpty(productList)) {
            throw new CustomBusinessException("没有查询到产品型号!无法导出！");
        }
        List<ProductExcelVo> productExcelVos = new ArrayList<>(productList.size());
        fillProductExcelVos(productList, productExcelVos);
        // 当前时间
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = "产品型号" + date + ".xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, ProductExcelVo.class).sheet("sheet").doWrite(productExcelVos);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }
    
    private void fillProductExcelVos(List<Product> productList, List<ProductExcelVo> productExcelVos) {
        for (Product p : productList) {
            ProductExcelVo productExcelVo = new ProductExcelVo();
            BeanUtil.copyProperties(p, productExcelVo);
            if (Objects.nonNull(p.getProductSeries())) {
                switch (p.getProductSeries()) {
                    case 1:
                        productExcelVo.setProductSeries("取餐柜");
                        break;
                    case 2:
                        productExcelVo.setProductSeries("餐厅柜");
                        break;
                    case 3:
                        productExcelVo.setProductSeries("换电柜");
                        break;
                    case 4:
                        productExcelVo.setProductSeries("充电柜");
                        break;
                    case 5:
                        productExcelVo.setProductSeries("寄存柜");
                        break;
                    case 6:
                        productExcelVo.setProductSeries("生鲜柜");
                        break;
                    default:
                        productExcelVo.setProductSeries("");
                }
            }
            
            Optional.ofNullable(p.getBuyType()).ifPresent(type -> {
                productExcelVo.setBuyType(p.getBuyType() == 1 ? "集采" : "非集采");
            });
            
            Optional.ofNullable(p.getHasScreen()).ifPresent(type -> {
                productExcelVo.setHasScreen(type == 1 ? "有屏" : "无屏");
            });
            Optional.ofNullable(p.getFireFightingType()).ifPresent(type -> {
                productExcelVo.setFireFightingType(type == 1 ? "气溶胶消防" : "水消防");
            });
            
            productExcelVos.add(productExcelVo);
        }
    }
    
    @Override
    public void serialNumberExportExcel(ProductSerialNumberQuery productSerialNumberQuery, HttpServletResponse response) {
        List<ProductSerialNumberVo> productSerialNumberMapperSerialNumberList = productSerialNumberMapper.getSerialNumberList(productSerialNumberQuery);
        if (ObjectUtil.isEmpty(productSerialNumberMapperSerialNumberList)) {
            throw new CustomBusinessException("没有查询到序列号!无法导出！");
        }
        List<ProductSerialNumberExcelVo> productSerialNumberExcelVoArrayList = new ArrayList<>(productSerialNumberMapperSerialNumberList.size());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ProductSerialNumberVo p : productSerialNumberMapperSerialNumberList) {
            ProductSerialNumberExcelVo productSerialNumberExcelVo = new ProductSerialNumberExcelVo();
            productSerialNumberExcelVo.setSerialNumber(p.getSerialNumber());
            productSerialNumberExcelVo.setCreateTimeStr(simpleDateFormat.format(new Date(p.getCreateTime())));
            productSerialNumberExcelVo.setProductName(p.getProductName());
            productSerialNumberExcelVo.setPrice(p.getPrice());
            productSerialNumberExcelVoArrayList.add(productSerialNumberExcelVo);
        }
        
        String fileName = "产品序列号.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, ProductSerialNumberExcelVo.class).sheet("sheet").doWrite(productSerialNumberExcelVoArrayList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public R insertSerialNumber(ProductSerialNumberQuery productSerialNumberQuery) {
        
        Product product = getById(productSerialNumberQuery.getProductId());
        if (Objects.isNull(product)) {
            return R.failMsg("未找到产品型号!");
        }
        if (productSerialNumberQuery.getRightInterval() < productSerialNumberQuery.getLeftInterval() && productSerialNumberQuery.getRightInterval() <= 9999
                && productSerialNumberQuery.getLeftInterval() >= 0) {
            return R.failMsg("请设置合适的编号区间!");
        }
        DecimalFormat decimalFormat = new DecimalFormat("0000");
        
        for (Long i = productSerialNumberQuery.getLeftInterval(); i <= productSerialNumberQuery.getRightInterval(); i++) {
            
            ProductSerialNumber productSerialNumber = new ProductSerialNumber();
            productSerialNumber.setSerialNumber(productSerialNumberQuery.getPrefix() + "_" + decimalFormat.format(i));
            productSerialNumber.setProductId(productSerialNumberQuery.getProductId());
            productSerialNumber.setPrice(product.getPrice());
            productSerialNumber.setCreateTime(System.currentTimeMillis());
            productSerialNumberService.save(productSerialNumber);
            
            if (productSerialNumberQuery.getFileId() != null) {
                ProductFile productFile = productFileMapper.selectById(productSerialNumberQuery.getFileId());
                productFile.setProductId(productSerialNumber.getId());
                productFileMapper.updateById(productFile);
            }
            
        }
        return R.ok();
    }
    
    @Override
    public R getSerialNumberPage(Long offset, Long size, ProductSerialNumberQuery productSerialNumber) {
        List<ProductNew> data = productNewMapper.selectNoPull();
        Map records = new HashMap();
        records.put("records", data);
        return R.ok(records);
    }
    
    @Override
    public Integer getByDateQuery(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years, mouths);
        return count;
    }
    
    @Override
    public Integer getMouth(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years, mouths);
        return count;
    }
    
    @Override
    public Integer getGeneral(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years, mouths);
        return count;
    }
    
    @Override
    public Integer getTotal(Map<String, Object> params) {
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years, mouths);
        return count;
    }
    
    @Override
    public Integer getRepairCount(Map<String, Object> params) {
        
        String years = (String) params.get("years");
        String mouths = (String) params.get("mouths");
        Integer count = this.baseMapper.getMouth(years, mouths);
        return count;
    }
    
    @Override
    public R productList() {
        List<Product> products = this.baseMapper.selectList(null);
        return R.ok(products);
    }
    
    
    @Override
    public R productSerialNumber(ProductSerialNumberQuery productSerialNumberQuery) {
        Product product = this.baseMapper.selectById(productSerialNumberQuery.getProductId());
        if (Objects.isNull(product)) {
            return R.fail("产品型号有误，请检查");
        }
        
        Long leftInterval = productSerialNumberQuery.getLeftInterval();
        Long rightInterval = productSerialNumberQuery.getRightInterval();
        String code = product.getCode();
        
        for (int i = Integer.parseInt(leftInterval.toString()); i <= Integer.parseInt(rightInterval.toString()); i++) {
            
            ProductSerialNumber productSerialNumber = new ProductSerialNumber();
            BeanUtil.copyProperties(productSerialNumberQuery, productSerialNumber);
            productSerialNumber.setSerialNumber(code + "_" + i);
            productSerialNumber.setCreateTime(System.currentTimeMillis());
            productSerialNumberMapper.insert(productSerialNumber);
            
            if (productSerialNumberQuery.getFileId() != null && productSerialNumberQuery.getFileId() != "") {
                ProductFile productFile = productFileMapper.selectById(productSerialNumberQuery.getFileId());
                productFile.setProductId(productSerialNumber.getId());
                productFileMapper.updateById(productFile);
            }
            
        }
        
        return R.ok();
    }
    
    @Override
    public R productSerialNumberInfo(Long id) {
        ProductSerialNumber productSerialNumber = productSerialNumberMapper.selectById(id);
        ProductSerialNumberVo productSerialNumberVo = new ProductSerialNumberVo();
        BeanUtil.copyProperties(productSerialNumber, productSerialNumberVo);
        
        if (Objects.nonNull(productSerialNumber)) {
            if (productSerialNumberVo.getStatus().equals(ProductSerialNumber.PRODUCTION.toString()) || productSerialNumberVo.getStatus()
                    .equals(ProductSerialNumber.TO_BE_REPAIRED.toString())) {
                WareHouse wareHouse = warehouseService.getById(productSerialNumber.getPointId());
                if (Objects.nonNull(wareHouse)) {
                    productSerialNumberVo.setAddr(wareHouse);
                }
            }
            
            if (productSerialNumberVo.getStatus().equals(ProductSerialNumber.IN_USE.toString())) {
                Point point = pointService.getById(productSerialNumberVo.getPointId());
                if (Objects.nonNull(point)) {
                    productSerialNumberVo.setAddr(point);
                }
            }
            
            LambdaQueryWrapper<ProductFile> eq = new LambdaQueryWrapper<ProductFile>().eq(ProductFile::getProductId, id);
            List<ProductFile> productFiles = productFileMapper.selectList(eq);
            productSerialNumberVo.setProductFileList(productFiles);
        }
        
        return R.ok(productSerialNumberVo);
    }
    
    @Override
    public R updateProductSerialNumberInfo(ProductSerialNumberQuery productSerialNumberQuery) {
        int i = productSerialNumberMapper.updateById(productSerialNumberQuery);
        if (i > 0) {
            
            return R.ok();
        }
        return R.fail("数据库错误");
    }
    
    @Override
    public R getProductSerialNumListByPid(Long id) {
        //        LambdaQueryWrapper<ProductSerialNumber> productSerialNumberLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //        productSerialNumberLambdaQueryWrapper.eq(ProductSerialNumber::getProductId, id);
        //        List<ProductSerialNumber> productSerialNumbers = productSerialNumberMapper.selectList(productSerialNumberLambdaQueryWrapper);
        LambdaQueryWrapper<ProductNew> productNew = new LambdaQueryWrapper<>();
        productNew.eq(ProductNew::getModelId, id);
        return R.ok(productNewMapper.selectList(productNew));
    }
    
    @Override
    public Product getByName(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>().eq(Product::getName, name);
        return this.baseMapper.selectOne(wrapper);
    }
    
    @Override
    public R queryProductModelPull(String name) {
        List<productModelPullVo> list = this.baseMapper.queryProductModelPull(name);
        return R.ok(list);
    }
    
    @Override
    public R productSearch(Long offset, Long size, String name) {
        List<PageSearchVo> listVo = this.baseMapper.productSearch(offset, size, name);
        return R.ok(listVo);
    }
    
    /**
     * 删除增加判断逻辑 是否绑定资产编码 是否绑定点位
     */
    @Override
    public R removeProductById(Long id) {
        Product byId = this.getById(id);
        if (Objects.isNull(byId)) {
            return R.failMsg("产品型号有误，请检查");
        }
        
        // 删除增加判断逻辑
        // 是否绑定批次
        List<Batch> batches = batchService.ListBatchByProductId(id);
        if (CollectionUtils.isNotEmpty(batches)) {
            return R.failMsg("该产品已绑定批次，请先删除批次");
        }
        
        // 判断是否绑定点位
        if (SqlHelper.retBool(pointMapper.countPointByProductId(String.valueOf(id)))) {
            return R.failMsg("该产品已绑定点位，请先删除点位");
        }
        return R.ok(this.removeById(id));
    }
}
