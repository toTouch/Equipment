package com.xiliulou.afterserver.constant;

import com.xiliulou.afterserver.entity.File;
import org.apache.commons.collections4.map.HashedMap;

import java.util.Map;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 16:05
 **/
public class FileConstant {

    public static final String BUCKET_NAME = "after-service";
    public static final String POINT_FILE_PREFIX = "point";


    public static final Map<Integer, String> prefixMap = new HashedMap<>();

    static {
        prefixMap.put(File.TYPE_POINT, POINT_FILE_PREFIX);
    }

}
