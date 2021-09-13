package com.xiliulou.afterserver.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.exception.CustomBusinessException;
import com.xiliulou.afterserver.mapper.DeliverMapper;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.PageUtil;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.DeliverQuery;
import com.xiliulou.afterserver.web.vo.DeliverExcelVo;
import com.xiliulou.afterserver.web.vo.DeliverExportExcelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 19:05
 **/
@Service
@Slf4j
public class DeliverServiceImpl extends ServiceImpl<DeliverMapper, Deliver> implements DeliverService {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ServerService serverService;
    @Autowired
    private SupplierService supplierService;
    @Autowired
    private ProductService productService;


    @Override
    public IPage getPage(Long offset, Long size, Deliver deliver) {

        Page page = PageUtil.getPage(offset, size);
        Page selectPage = baseMapper.selectPage(page,
                new LambdaQueryWrapper<Deliver>()
                        .eq(Objects.nonNull(deliver.getState()), Deliver::getState, deliver.getState())
                        .eq(Objects.nonNull(deliver.getCreateUid()),Deliver::getCreateUid,deliver.getCreateUid())
                        .like(Objects.nonNull(deliver.getExpressNo()),Deliver::getExpressNo,deliver.getExpressNo())
                        .like(Objects.nonNull(deliver.getExpressCompany()),Deliver::getExpressCompany,deliver.getExpressCompany())
                        .orderByDesc(Deliver::getCreateTime)
                        .like(Objects.nonNull(deliver.getCity()),Deliver::getCity,deliver.getCity())
                        .like(Objects.nonNull(deliver.getDestination()),Deliver::getDestination,deliver.getDestination()));
        List<Deliver> list = (List<Deliver>) selectPage.getRecords();
        if (list.isEmpty()){
            return selectPage;
        }



        list.forEach(records -> {

            Map map = new HashMap();

            if("null".equals(records.getQuantity()) || null == records.getQuantity()){
                records.setQuantity(JSON.toJSONString(new String[]{null}));
            }

            if (Objects.nonNull(records.getCreateUid())){
                User userById = userService.getUserById(records.getCreateUid());
                if (Objects.nonNull(userById)){
                    records.setUserName(userById.getUserName());
                }
               records.setUserName(userById.getUserName());
            }

//            第三方类型 1：客户 2：供应商 3:服务商';
            if (Objects.nonNull(records.getThirdCompanyType())){
                String name = "";
                if (records.getThirdCompanyType() == 1){
                    Customer byId = customerService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)){
                        name = byId.getName();
                    }
                }
                if (records.getThirdCompanyType() == 2){
                    Supplier byId = supplierService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)){
                        name = byId.getName();
                    }
                }
                if (records.getThirdCompanyType() == 3){
                    Server byId = serverService.getById(records.getThirdCompanyId());
                    if (Objects.nonNull(byId)){
                        name = byId.getName();
                    }
                }
                records.setThirdCompanyName(name);
            }

            if(StrUtil.isEmpty(records.getProduct())) {
                records.setProduct(JSONUtil.toJsonStr(new ArrayList<>()));
            }

            if(ObjectUtils.isNotNull(records.getProduct())
                    && !"[]".equals(records.getProduct())
                    && ObjectUtils.isNotNull(records.getQuantity())
                    && !"[null]".equals(records.getQuantity())){

                ArrayList<Integer> products = JSON.parseObject(records.getProduct(), ArrayList.class);
                ArrayList<Integer> quantitys = JSON.parseObject(records.getQuantity(), ArrayList.class);

                for(int i = 0; i < products.size() && ( quantitys.size() == products.size() ); i++){
                    Product p = productService.getById(products.get(i));
                    if(ObjectUtils.isNotNull(p)){
                        map.put(p.getName(), quantitys.get(i));
                    }
                }
                records.setDetails(map);
            }

        });

        return selectPage.setRecords(list);
    }

    //导出excel
    @Override
    public void exportExcel(DeliverQuery query, HttpServletResponse response) {
        List<Deliver> deliverList = baseMapper.orderList(query);

        if (ObjectUtil.isEmpty(deliverList)) {
            throw new CustomBusinessException("没有查询到产品型号!无法导出！");
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DeliverExportExcelVo> deliverExcelVoList = new ArrayList<>(deliverList.size());
        for (Deliver d : deliverList) {
            DeliverExportExcelVo deliverExcelVo = new DeliverExportExcelVo();
            BeanUtil.copyProperties(d, deliverExcelVo);
            //thirdCompanyName
            if(ObjectUtil.isNotNull(d.getThirdCompanyId())){
                Customer customer = customerService.getById(d.getThirdCompanyId());
                if(ObjectUtil.isNotNull(customer)){
                    deliverExcelVo.setThirdCompanyName(customer.getName());
                }
            }
            //deliverTime
            if(ObjectUtil.isNotNull(d.getDeliverTime())){
                deliverExcelVo.setDeliverTime(simpleDateFormat.format(new Date(d.getDeliverTime())));
            }
            //createUName
            if(ObjectUtil.isNotNull(d.getCreateUid())){
                User user = userService.getById(d.getCreateUid());
                if(ObjectUtil.isNotNull(user)){
                    deliverExcelVo.setCreateUName(user.getUserName());
                }
            }
            //stateStr
            if(ObjectUtil.isNotNull(d.getState())){
                deliverExcelVo.setStateStr(getDeliverStatue(d.getState()));
            }
            //customerName
            if(ObjectUtil.isNotNull(d.getCustomerId())){
                Customer customer = customerService.getById(d.getCustomerId());
                if(ObjectUtil.isNotNull(customer)){
                    deliverExcelVo.setCreateUName(customer.getName());
                }
            }
            deliverExcelVoList.add(deliverExcelVo);
        }

        String fileName = "发货管理.xls";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, DeliverExportExcelVo.class).sheet("sheet").doWrite(deliverExcelVoList);
            return;
        } catch (IOException e) {
            log.error("导出报表失败！", e);
        }
        throw new CustomBusinessException("导出报表失败！请联系客服！");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public R updateStatusFromBatch(List<Long> ids, Integer status) {
        int row = this.baseMapper.updateStatusFromBatch(ids,status);
        if(row == 0){
            return R.fail("未修改数据");
        }
        return R.ok();
    }

    private String getExpressNo(Integer status) {
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "待处理";
                break;
            case 2:
                statusStr = "处理中";
                break;
            case 3:
                statusStr = "已完成";
                break;
        }
        return statusStr;
    }

    private String getDeliverStatue(Integer status){
        //物流状态 1：未发货  2：已发货  3：已到达
        String statusStr = "";
        switch (status) {
            case 1:
                statusStr = "未发货";
                break;
            case 2:
                statusStr = "已发货";
                break;
            case 3:
                statusStr = "已到达";
                break;
        }
        return statusStr;
    }
}
