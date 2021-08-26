package com.xiliulou.afterserver.controller.admin;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiliulou.afterserver.entity.*;
import com.xiliulou.afterserver.export.PointInfo;
import com.xiliulou.afterserver.listener.PointListener;
import com.xiliulou.afterserver.service.*;
import com.xiliulou.afterserver.util.DateUtils;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.vo.PointExcelVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

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
    @Autowired
    private UserService userService;


    @PostMapping("/admin/pointNew")
    public R saveAdminPointNew(@RequestBody PointNew pointNew, HttpServletRequest request){
        Long uid = (Long) request.getAttribute("uid");
        if (Objects.isNull(uid)){
            return R.fail("用户为空");
        }
        pointNew.setCreateUid(uid);
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
                       @RequestParam(value = "name",required = false) String name,
                       @RequestParam(value = "cid",required = false) Integer cid,
                       @RequestParam(value = "status",required = false) Integer status,
                       @RequestParam(value = "customerId",required = false) Long customerId,
                       @RequestParam(value = "startTime",required = false) Long startTime,
                       @RequestParam(value = "endTime",required = false) Long endTime,
                       @RequestParam(value = "createUid",required = false) Long createUid){
        List<PointNew> pointNews = pointNewService.queryAllByLimit(offset, limit, name,cid,status,customerId,startTime,endTime,createUid);

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

                if (Objects.nonNull(item.getCreateUid())){
                    User userById = userService.getUserById(item.getCreateUid());
                    if (Objects.nonNull(userById)){
                        item.setUserName(userById.getUserName());
                    }
                }
            });
        }


        Integer count =  pointNewService.countPoint(name,cid,status,customerId,startTime,endTime,createUid);


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

    @GetMapping("/admin/pointNew/exportExcel")
    public void pointExportExcel(@RequestParam(value = "name",required = false) String name,
                              @RequestParam(value = "cid",required = false) Integer cid,
                              @RequestParam(value = "status",required = false) Integer status,
                              @RequestParam(value = "customerId",required = false) Long customerId,
                              @RequestParam(value = "startTime",required = false) Long startTime,
                              @RequestParam(value = "endTime",required = false) Long endTime,
                              @RequestParam(value = "createUid",required = false) Long createUid,
                              HttpServletResponse response){
        List<PointNew> pointNews = pointNewService.queryAllByLimitExcel(name,cid,status,customerId,startTime,endTime,createUid);


        if (Objects.isNull(startTime) || Objects.isNull(endTime)){
            throw new NullPointerException("请选择开始时间和结束时间");
        }

        if (pointNews.isEmpty()){
            return;
        }

        ArrayList<PointExcelVo> pointExcelVos = new ArrayList<>();

        pointNews.forEach(item -> {
            PointExcelVo pointExcelVo = new PointExcelVo();
            pointExcelVo.setName(item.getName());
            pointExcelVo.setAddress(item.getAddress());
            pointExcelVo.setCameraCount(item.getCameraCount());
            pointExcelVo.setCanopyCount(item.getCanopyCount());
            pointExcelVo.setSnNo(item.getSnNo());
            pointExcelVo.setCardNumber(item.getCardNumber());

            // 1:室外 2:半室外3：室内
            if (Objects.isNull(item.getInstallType())){
                pointExcelVo.setInstallType("");
            }else if (Objects.equals(item.getInstallType(),1)){
                pointExcelVo.setInstallType("室外");
            }else if (Objects.equals(item.getInstallType(),2)){
                pointExcelVo.setInstallType("半室外");
            }else if (Objects.equals(item.getInstallType(),3)){
                pointExcelVo.setInstallType("室内");
            }

            // 状态 1,移机,2运营中,3:拆机
            if (Objects.isNull(item.getStatus())){
                pointExcelVo.setStatus("");
            }else if (Objects.equals(item.getStatus(),1)){
                pointExcelVo.setStatus("移机");
            }else if (Objects.equals(item.getStatus(),2)){
                pointExcelVo.setStatus("运营中");
            }else if (Objects.equals(item.getStatus(),3)){
                pointExcelVo.setStatus("拆机");
            }

            if (item.getCreateTime() != null) {
                pointExcelVo.setCreateTime(DateUtils.stampToDate(item.getCreateTime().toString()));
            }

            if (Objects.nonNull(item.getCityId())){
                City byId = cityService.getById(item.getCityId());
                pointExcelVo.setCityName(byId.getName());
            }

            if (Objects.nonNull(item.getCustomerId())){
                Customer byId = customerService.getById(item.getCustomerId());
                if (Objects.nonNull(byId)){
                    pointExcelVo.setCustomerName(byId.getName());
                }
            }

            pointExcelVos.add(pointExcelVo);

        });

        String fileName = "点位.xlsx";
        try {
            ServletOutputStream outputStream = response.getOutputStream();
            // 告诉浏览器用什么软件可以打开此文件
            response.setHeader("content-Type", "application/vnd.ms-excel");
            // 下载文件的默认名称
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
            EasyExcel.write(outputStream, PointExcelVo.class).sheet("sheet").doWrite(pointExcelVos);
            return;
        } catch (IOException e) {
            throw new NullPointerException("导出报表失败！请联系管理员处理！");
        }


    }




    @GetMapping("/admin/pointNew/info/{pid}")
    public R printInfo(@PathVariable("pid") Long pid){
        return pointNewService.pointInfo(pid);
    }
}
