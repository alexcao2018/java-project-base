package com.project.base.web.convert;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;

public class StringToDateConverter
        implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        Date result = null;
        try {
            result = DateUtils.parseDate(source, "yyyy-MM-dd hh:mm:ss", "yyyy-MM-dd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }
}
