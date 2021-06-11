package com.xiliulou.afterserver.web.query;

import com.xiliulou.afterserver.entity.File;
import lombok.Data;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-29 16:18
 **/
@Data
public class FileQuery extends File {

    public static final Integer FLAG_FALSE = 0;
    public static final Integer FLAG_TRUE = 1;

    private String fileStr;
    public Integer delFlag;
}
