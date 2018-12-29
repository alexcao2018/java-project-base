package com.project.base.dotnet.rpc;


import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public class UrlCityParserTest {

    @Test
    public void getCityValue() {
        String result = UrlCityParser.getCityValue("/sz/service/xx");
        Assert.assertEquals("sz", result);

        result = UrlCityParser.getCityValue("/wx/service/xx");
        Assert.assertEquals("wx", result);

        result = UrlCityParser.getCityValue("/sh/service/xx");
        Assert.assertEquals("sh", result);

        result = UrlCityParser.getCityValue("/ss/service/xx");
        Assert.assertNull( result);
    }
}