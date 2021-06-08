package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.ProductFileMapper;
import com.xiliulou.afterserver.mapper.ProductMapper;
import com.xiliulou.afterserver.mapper.ProductSerialNumberMapper;
import com.xiliulou.afterserver.service.PointService;
import com.xiliulou.afterserver.service.ProductSerialNumberService;
import com.xiliulou.afterserver.service.ProductService;
import com.xiliulou.afterserver.service.WarehouseService;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.ProductSerialNumberQuery;
import com.xiliulou.afterserver.web.vo.ProductExcelVo;
import com.xiliulou.afterserver.web.vo.ProductSerialNumberExcelVo;
import com.xiliulou.afterserver.web.vo.ProductSerialNumberVo;
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
import java.util.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:08
 **/
@Service
@Slf4j
public class ProductSerialNumberServiceImpl extends ServiceImpl<ProductSerialNumberMapper, ProductSerialNumber> implements ProductSerialNumberService {

}
