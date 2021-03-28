package com.xiliulou.afterserver.service;

import java.util.Map;

public interface MouthService {

    Map<String, Integer> getMouth(Map<String, Object> params);

    Map<String, Object> getShowData(String begin, String end);
}
