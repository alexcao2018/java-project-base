package com.project.base.web.convert;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

/**
 * 此converter 只在form提交的时候才启作用，
 * content-type:application/json 方式 不生效
 */
public class StringToDateConverter
        implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        Date result = null;
        try {
            result = DateUtils.parseDate(source, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
