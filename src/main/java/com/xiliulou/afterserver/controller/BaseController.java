package com.xiliulou.afterserver.controller;

import com.xiliulou.afterserver.util.R;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @program: XILIULOU
 * @description:
 * @author: Mr.YG
 * @create: 2021-01-28 18:10
 **/
public class BaseController {

    protected R returnPairResult(Pair<Boolean, Object> result) {
        if (result.getLeft()) {
            return R.ok(result.getRight());
        }
        return R.failMsg(result.getRight() == null ? "" : result.getRight().toString());
    }
}
