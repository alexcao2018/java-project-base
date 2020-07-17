package com.project.base.web.convert;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public class StringToDateConverterTest {

    private StringToDateConverter stringToDateConverter = new StringToDateConverter();

    @Test
    public void convert() {
        Date date = stringToDateConverter.convert("2018-04-17 10:19:26.020");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals("2018-04-17 10:19:26.020", formatter.format(date));
    }

    @Test
    public void convert1() {
        Date date = stringToDateConverter.convert("2018-04-17 10:19:26");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals("2018-04-17 10:19:26.000", formatter.format(date));
    }

    @Test
    public void convert2() {
        Date date = stringToDateConverter.convert("2018-04-17");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Assert.assertEquals("2018-04-17 00:00:00.000", formatter.format(date));
    }
}