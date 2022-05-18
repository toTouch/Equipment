package com.xiliulou.afterserver.web.query;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * @author zgw
 * @date 2022/5/5 15:51
 * @mood
 */
@Data
public class GeoCodeQuery {

    @SerializedName("formatted_address")
    private String formattedAddress;
    private String country;
    private String province;
    private String citycode;
    private String city;
    private String district;
    private String adcode;
    private String location;
    private String level;
}
