package com.xiliulou.afterserver.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.export.PointUpdateInfo;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.AmapUtil;
import com.xiliulou.afterserver.vo.PointNewInfoVo;
import com.xiliulou.afterserver.web.query.GeoCodeQuery;
import com.xiliulou.afterserver.web.query.GeoCodeResultQuery;
import jdk.net.SocketFlow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class PointUpdateListener extends AnalysisEventListener<PointUpdateInfo> {
    private static final int BATCH_COUNT = 2000;
    List<PointUpdateInfo> list = new ArrayList<>();

    private PointNewService pointService;
    private CustomerService customerService;
    private CityService cityService;
    private HttpServletRequest request;
    private UserService userService;
    private SupplierService supplierService;

    public PointUpdateListener(PointNewService pointService, CustomerService customerService, CityService cityService, HttpServletRequest request, UserService userService, SupplierService supplierService) {
        this.pointService = pointService;
        this.cityService = cityService;
        this.customerService = customerService;
        this.userService = userService;
        this.supplierService = supplierService;
        this.request = request;
    }

    @Override
    public void invoke(PointUpdateInfo pointInfo, AnalysisContext analysisContext) {
        log.info("点位导入=====解析到一条数据:{}", JSON.toJSONString(pointInfo));

        PointNew pointNew = pointService.getById(pointInfo.getId());
        if(Objects.isNull(pointNew)){
            log.error("PointNew Update Upload Error! Not Find Point! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("点位id【" + pointInfo.getId() +"】未查询到点位，请确保id正确");
        }

        if(Objects.isNull(pointInfo.getName())){
            log.error("PointNew Update Upload Error! Name is entry! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("点位id【"+ pointInfo.getName() + "】请添加正确产品系列");
        }

        if(getProductSeries(pointInfo.getProductSeries()) == null){
            log.error("PointNew Update Upload Error! Not Find supplier! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("点位【"+ pointInfo.getName() + "】请添加正确产品系列");
        }

        if(Objects.nonNull(pointInfo.getCity())){
            City city = getCity(pointInfo.getCity());
            if(Objects.isNull(city)){
                log.error("PointNew Update Upload Error! Not Find city! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
                throw new RuntimeException("点位【"+pointInfo.getName() + "】未查询到城市信息");
            }
        }else{
            log.error("PointNew Update Upload Error! city is entry! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("点位【"+pointInfo.getName() + "】请添加城市信息");
        }


        if(Objects.nonNull(pointInfo.getCustomer())){
            Customer customer = getCustomer(pointInfo.getCustomer());
            if(Objects.isNull(customer)){
                log.error("PointNew Update Upload Error! Not Find customer! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
                throw new RuntimeException("点位【"+pointInfo.getName() + "】未查询到客户信息");
            }
        }else{
            log.error("PointNew Update Upload Error! customer is entry! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("点位【"+pointInfo.getName() + "】请添加客户信息");
        }

        Integer status = getStatus(pointInfo.getStatus());
        if(status.equals(-1)){
            log.error("PointNew Update Upload Error! Not Find status! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("点位【"+pointInfo.getName() + "】请添加正确点位状态");
        }

        if(Objects.nonNull(pointInfo.getInstallType())){
            Integer installType = getInstallType(pointInfo.getInstallType());
            if(installType.equals(-1)){
                log.error("PointNew Update Upload Error! Not Find installType! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
                throw new RuntimeException("点位【"+pointInfo.getName() + "】请添加正确安装类型");
            }
        }

        /*if(Objects.isNull(pointInfo.getInstallTime())){
            log.error("PointNew Update Upload Error!  CreateTime is required value! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("CreateTime is required value");
        }*/

        Integer flagDel = getFlagDel(pointInfo.getDelFlag());
        if(flagDel.equals(-1)){
            log.error("PointNew Update Upload Error! Not Find flagDel! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
            throw new RuntimeException("点位【"+pointInfo.getName() + "】请添加正确删除信息");
        }


        if(Objects.nonNull(pointInfo.getCreateUid())){
            User user = getUser(pointInfo.getCreateUid());
            if(Objects.isNull(user)){
                log.error("PointNew Update Upload Error! Not Find user! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
                throw new RuntimeException("点位【"+pointInfo.getName() + "】未查询到创建人");
            }
        }

        if(Objects.nonNull(pointInfo.getIsEntry())){
            Integer isEntry = getIsEntry(pointInfo.getIsEntry());
            if(isEntry.equals(-1)){
                log.error("PointNew Update Upload Error! Not Find isEntry! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
                throw new RuntimeException("点位【"+pointInfo.getName() + "】请添加正确入账信息");
            }
        }

        if(Objects.nonNull(pointInfo.getIsAcceptance())){
            Integer isAcceptance = getIsAcceptance(pointInfo.getIsAcceptance());
            if(isAcceptance.equals(-1)){
                log.error("PointNew Update Upload Error! Not Find isAcceptance! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
                throw new RuntimeException("点位【"+pointInfo.getName() + "】请添加正确验收信息");
            }
        }

        if(Objects.nonNull(pointInfo.getCardSupplier())){
            Supplier supplier = supplierService.getOne(new QueryWrapper<Supplier>().eq("name", pointInfo.getCardSupplier()));
            if(Objects.isNull(supplier)){
                log.error("PointNew Update Upload Error! Not Find supplier! id={},数据={}", pointInfo.getId(), JSON.toJSONString(pointInfo));
                throw new RuntimeException("点位【"+pointInfo.getName() + "】物联网卡供应商不存在");
            }
        }

        /*if( Objects.isNull(pointInfo.getInstallTime())){
            log.error("insert PointInfo error! not calculation InstallTime pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "没有添加安装时间");
        }
        if(Objects.isNull(pointInfo.getAddress())){
            log.error("insert PointInfo error! InstallType is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "请填写详细地址");
        }


        GeoCodeResultQuery geoCodeResultQuery = null;
        try {
            geoCodeResultQuery = AmapUtil.getLonLat(URLEncoder.encode(pointInfo.getAddress(), "UTF-8"));
        } catch (Exception e) {
            log.error("insert PointInfo error! CALL GEO CODE RESULT QUERY API ERROR pointName={}",pointInfo.getName(), e);
            throw new RuntimeException("调用高德逆地理编码失败，请联系管理员");
        }

        if(Objects.isNull(geoCodeResultQuery)) {
            log.error("insert PointInfo error! geoCodeResultQuery is entry pointName={}",pointInfo.getName());
            throw new RuntimeException("获取点位" + pointInfo.getName() + "逆地理编码为空，请检查");
        }

        if(Objects.equals(geoCodeResultQuery.getStatus(), GeoCodeResultQuery.STATUS_FAIL)) {
            log.error("insert PointInfo error! GEO CODE RESULT QUERY FAIL pointName={}",pointInfo.getName());
            throw new RuntimeException("获取点位" + pointInfo.getName() + "逆地理编码失败，请联系管理员");
        }

        if(CollectionUtils.isEmpty(geoCodeResultQuery.getGeocodes())) {
            log.error("insert PointInfo error! GEO CODES IS ENTRY pointName={}",pointInfo.getName());
            throw new RuntimeException("获取点位" + pointInfo.getName() + "逆地理编码为空，请检查");
        }

        if( Objects.isNull(pointInfo.getWarrantyPeriod())){
            log.error("insert PointInfo error! is null WarrantyPeriod pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "没有添加质保有效期");
        }
        if( Objects.isNull(pointInfo.getIsAcceptance())){
            log.error("insert PointInfo error! is null IsAcceptance pointName={}",pointInfo.getName());
            throw new RuntimeException("点位" + pointInfo.getName() + "没有添加是否验收");
        }*/

        list.add(pointInfo);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        saveData();
        log.info("客户表导入=====所有数据解析完成！");
    }



    /**
     * 入库
     */
    private void saveData() {
        log.info("{}条数据，开始存储数据库！", list.size());

        List<PointNew> pointList = new ArrayList<>();
        this.list.forEach(item -> {
            PointNew point = new PointNew();
            PointNew pointOld = pointService.getById(item.getId());
            //BeanUtils.copyProperties(item, point);
            point.setId(item.getId());
            point.setName(item.getName());

            if(Objects.nonNull(item.getCustomer())){
                Customer customer = getCustomer(item.getCustomer());
                point.setCustomerId(customer.getId());
            }

            point.setStatus(getStatus(item.getStatus()));
            point.setCityId(getCity(item.getCity()).getId());

            if(Objects.nonNull(item.getInstallType())){
                Integer installType = getInstallType(item.getInstallType());
                point.setInstallType(installType);
            }

            point.setAddress(item.getAddress());
            /*GeoCodeResultQuery geoCodeResultQuery = null;
            try {
                geoCodeResultQuery = AmapUtil.getLonLat(URLEncoder.encode(item.getAddress(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("insert PointInfo error! CALL GEO CODE RESULT QUERY API ERROR pointName={}",item.getName(), e);
                throw new RuntimeException("调用高德逆地理编码失败，请联系管理员");
            }
            GeoCodeQuery geoCodeQuery = geoCodeResultQuery.getGeocodes().get(0);
            String[] lonlat = geoCodeQuery.getLocation().split(",");
            point.setCoordX(new BigDecimal(lonlat[1]));
            point.setCoordY(new BigDecimal(lonlat[0]));*/

            point.setCameraCount(item.getCameraCount());
            point.setCanopyCount(item.getCanopyCount());
            if(StringUtils.isEmpty(pointOld.getSnNo())) {
                point.setSnNo(item.getSnNo());
            } else {
                point.setSnNo(pointOld.getSnNo());
            }
            point.setCardNumber(item.getCardNumber());
            point.setCardSupplier(item.getCardSupplier());

            Long installTime = 0L;
            if (item.getInstallTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getInstallTime());
                    installTime = l;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setInstallTime(l);
            }else {
                installTime = System.currentTimeMillis();
                point.setInstallTime(installTime);
            }

            if(Objects.nonNull(item.getWarrantyPeriod())) {
                point.setWarrantyPeriod(Integer.parseInt(item.getWarrantyPeriod()));
                point.setWarrantyTime(Integer.parseInt(item.getWarrantyPeriod()) * 3600000L * 24 * 360 + installTime);
            }

            point.setLogisticsInfo(item.getLogisticsInfo());

            if (item.getCreateTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getCreateTime());
                } catch (ParseException e) {

                }
                point.setCreateTime(l);
            }

            point.setDelFlag(getFlagDel(item.getDelFlag()));


            point.setRemarks(item.getRemarks());

            if(Objects.nonNull(item.getCreateUid())){
                point.setCreateUid(getUser(item.getCreateUid()).getId());
            }

            if (item.getCompletionTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getCompletionTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setCompletionTime(l);
            }

            point.setIsEntry(getIsEntry(item.getIsEntry()) == -1 ? null : getIsEntry(item.getIsEntry()));
            point.setIsAcceptance(getIsAcceptance(item.getIsAcceptance()) == -1 ? null : getIsAcceptance(item.getIsAcceptance()));
            point.setProductSeries(getProductSeries(item.getProductSeries()));

            if (item.getOrderTime() != null){
                long l = 0;
                try {
                    l = dateToStamp(item.getOrderTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                point.setOrderTime(l);
            }

            point.setOperator(item.getOperator());
            point.setLogisticsInfo(item.getLogisticsInfo());
            point.setAuditStatus(PointNew.AUDIT_STATUS_WAIT);
            pointList.add(point);
        });

        pointService.updateMany(pointList);
        log.info("存储数据库成功！");
    }

    /**
     * 将时间转换为时间戳
     */
    public static long dateToStamp(String s) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        return ts;
    }


    public Integer getProductSeries(String productSeries){
        if("1".equals(productSeries) || "取餐柜".equals(productSeries)){
            return 1;
        }
        if("2".equals(productSeries) || "餐厅柜".equals(productSeries)){
            return 2;
        }
        if("3".equals(productSeries) || "换电柜".equals(productSeries)){
            return 3;
        }
        if("4".equals(productSeries) || "充电柜".equals(productSeries)){
            return 4;
        }
        if("5".equals(productSeries) || "寄存柜".equals(productSeries)){
            return 5;
        }
        if("6".equals(productSeries) || "生鲜柜".equals(productSeries)){
            return 6;
        }
        return null;
    }


    public Integer getIsAcceptance(String isAcceptance){
        Integer statusInt = -1;
        if("0".equals(isAcceptance) || "否".equals(isAcceptance)){
            statusInt = 0;
        } else if("1".equals(isAcceptance) || "是".equals(isAcceptance)){
            statusInt = 1;
        }
        return statusInt;
    }

    public Integer getIsEntry(String isEntry){
        Integer statusInt = -1;
        if("0".equals(isEntry) || "否".equals(isEntry)){
            statusInt = 0;
        } else if("1".equals(isEntry) || "是".equals(isEntry)){
            statusInt = 1;
        }
        return statusInt;
    }
    /**
     * 删除 0 -- 1
     */
    public static Integer getFlagDel(String flagDel){
        Integer statusInt = -1;
        if("0".equals(flagDel) || "正常".equals(flagDel)){
            statusInt = 0;
        } else if("1".equals(flagDel) || "删除".equals(flagDel)){
            statusInt = 1;
        }
        return statusInt;
    }
    /**
     * 城市
     */
    public City getCity(String cid){
        City city = null;
        try{
            city = cityService.getById(Long.parseLong(cid));
        }catch (Exception e){

        }
        if(Objects.isNull(city)){
            city = cityService.getOne(new QueryWrapper<City>().eq("name", cid));
        }
        return city;
    }
    /**
     * 创建人id
     */
    public User getUser(String uid){
        User user = null;
        try{
            user = userService.getUserById(Long.parseLong(uid));
        }catch (Exception e){

        }
        if(Objects.isNull(user)){
            user = userService.getOne(new QueryWrapper<User>().eq("user_name",uid));
        }
        return user;
    }
    /**
     * 创建人id
     */
    public Customer getCustomer(String cid){
        Customer customer = null;
        try{
            customer = customerService.getById(cid);
        }catch (Exception e){

        }

        if(Objects.isNull(customer)){
            customer = customerService.getOne(new QueryWrapper<Customer>().eq("name",cid));
        }
        return customer;
    }
    /**
     * 状态
     */
    public static Integer getStatus(String status){
        Integer statusInt = -1;
        if("1".equals(status) || "移机".equals(status)){
            statusInt = 1;
        } else if("2".equals(status) || "运营中".equals(status)){
            statusInt = 2;
        }else if("3".equals(status) || "已拆机".equals(status)){
            statusInt = 3;
        }else if("4".equals(status) || "初始化".equals(status)){
            statusInt = 4;
        }else if("5".equals(status) || "待安装".equals(status)){
            statusInt = 5;
        }else if("6".equals(status) || "运输中".equals(status)){
            statusInt = 6;
        }else if("7".equals(status) || "安装中".equals(status)){
            statusInt = 7;
        }else if("8".equals(status) || "安装完成".equals(status)){
            statusInt = 8;
        }else if("9".equals(status) || "已暂停".equals(status)){
            statusInt = 9;
        }else if("10".equals(status) || "已取消".equals(status)){
            statusInt = 10;
        }else if("11".equals(status) || "已过保".equals(status)){
            statusInt = 11;
        }

       return statusInt;
    }

    public static Integer getInstallType(String type){
        Integer statusInt = -1;
        if("1".equals(type) || "室外".equals(type)){
            statusInt = 1;
        } else if("2".equals(type) || "半室外".equals(type)){
            statusInt = 2;
        }else if("3".equals(type) || "室内".equals(type)){
            statusInt = 3;
        }
        return statusInt;
    }
}
