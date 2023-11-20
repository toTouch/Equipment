package com.xiliulou.afterserver.constant;

/**
 * @ClassName : CommonConstants
 * @Description : 公用常量
 * @Author : YG
 * @Date: 2020-04-23 10:23
 */
public interface CommonConstants {
	/**
	 * 成功标记
	 */
	Integer SUCCESS = 0;
	/**
	 * 失败标记
	 */
	Integer FAIL = 1;

	String GEOCODE_URL = "http://restapi.amap.com/v3/geocode/geo?key=%s&address=%s";

    String OSS_IMG_WATERMARK_STYLE = "image/watermark,text_%s,type_%s,color_%s,size_%s,x_%s";

    /**
     * oss水印文字类型 （文泉驿正黑）
     */
    String OSS_IMG_WATERMARK_TYPE = "d3F5LXplbmhlaQ";

    /**
     * oss水印文字颜色 （白色）
     */
    String OSS_IMG_WATERMARK_COLOR = "FFFFFF";

    /**
     * oss水印文字大小（px)
     */
    String OSS_IMG_WATERMARK_SIZE = "60";


    /**
     * oss水印文字偏移量（px)
     */
    String OSS_IMG_WATERMARK_OFFSET = "40";
    
    /**
     * 填充最大数量
     */
    Integer FILL_MAX_NO = 6;
}
