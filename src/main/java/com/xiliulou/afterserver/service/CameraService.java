package com.xiliulou.afterserver.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiliulou.afterserver.entity.Camera;
import com.xiliulou.afterserver.entity.City;
import com.xiliulou.afterserver.util.R;
import com.xiliulou.afterserver.web.query.CameraQuery;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Hardy
 * @date 2022/2/8 16:34
 * @mood
 */
public interface CameraService extends IService<Camera> {

    R saveOne(CameraQuery cameraQuery);

    R updateOne(CameraQuery cameraQuery);

    R deleteOne(Long id);

    R getPage(Long offset, Long size, CameraQuery cameraQuery);

    void exportExcel(CameraQuery cameraQuery, HttpServletResponse response);

    Camera queryBySerialNum(String serialNum);

    List<Camera> likeCameraBySerialNum(String serialNum);

    R likeSerialNumPull(String serialNum);

    R cameraSnLike(Long offset, Long size, String sn);

    R queryCameraIotCardBySn(String sn);

    Camera getBySn(String value);
}
