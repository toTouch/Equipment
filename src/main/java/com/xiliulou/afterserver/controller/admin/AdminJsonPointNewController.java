package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.entity.Customer;
import com.xiliulou.afterserver.entity.PointNew;
import com.xiliulou.afterserver.entity.Province;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.listener.PointListener;
import com.xiliulou.afterserver.service.CityService;
import com.xiliulou.afterserver.service.CustomerService;
import com.xiliulou.afterserver.service.PointNewService;
import com.xiliulou.afterserver.service.ProvinceService;
import com.xiliulou.afterserver.util.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * @author Hardy
 * @date 2021/8/17 10:33
 * @mood 点位模块新改
 */
@RestController
public class AdminJsonPointNewController {

    @Autowired
    private PointNewService pointNewService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CityService cityService;
    @Autowired
    private ProvinceService provinceService;


    @PostMapping("/admin/pointNew")
    public R saveAdminPointNew(@RequestBody PointNew pointNew){
        return pointNewService.saveAdminPointNew(pointNew);
    }

    @PutMapping("/admin/pointNew")
    public R putAdminPointNew(@RequestBody PointNew pointNew){
        return pointNewService.putAdminPoint(pointNew);
    }

    @DeleteMapping("/admin/pointNew/{id}")
    public R delAdminPointNew(@PathVariable("id") Long id){
        return pointNewService.delAdminPointNew(id);
    }

    @GetMapping("/admin/pointNew/list")
    public R pointList(@RequestParam("offset") Integer offset,
                       @RequestParam("limit") Integer limit,
                       @RequestParam(value = "name",required = false) String name){
        List<PointNew> pointNews = pointNewService.queryAllByLimit(offset, limit, name);

        if (Objects.nonNull(pointNews)){
            pointNews.forEach(item -> {
                if (Objects.nonNull(item.getCityId())){
                    City byId = cityService.getById(item.getCityId());
                    item.setCityName(byId.getName());
                    item.setProvince(byId.getPid().toString());
                }

                if (Objects.nonNull(item.getCustomerId())){
                    Customer byId = customerService.getById(item.getCustomerId());
                    if (Objects.nonNull(byId)){
                        item.setCustomerName(byId.getName());
                    }
                }
            });
        }


        Integer count =  pointNewService.count(new LambdaQueryWrapper<PointNew>().like(PointNew::getName,name));


        HashMap<String, Object> map = new HashMap<>();
        map.put("data",pointNews);
        map.put("count",count);
        return R.ok(map);
    }


    /**
     * 导入
     */
    @PostMapping("admin/pointNew/upload")
    public R upload(MultipartFile file) throws IOException {

        ExcelReader excelReader = EasyExcel.read(file.getInputStream(), PointInfo.class,new PointListener(pointNewService,customerService,cityService)).build();
        ReadSheet readSheet = EasyExcel.readSheet(0).build();
        excelReader.read(readSheet);
        excelReader.finish();
        return R.ok();
    }
}
