package com.project.base.dotnet.rpc;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public class UrlCityParser {

    private static List<String> cities = Arrays.asList("sz", "sh", "wx");

    public static String getCityValue(String url) {
        if (url == null || url == "") {
            return null;
        }
        String[] urlArr = url.split("/");
        if (urlArr.length > 1) {
            String city = urlArr[1];
            if (cities.contains(city)) {
                return city;
            }
        }
        return null;
    }
}
