package com.project.base.web.convert;

import com.project.base.web.GlobalExceptionResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 此converter 只在form提交的时候才启作用，
 * content-type:application/json 方式 不生效
 */
public class StringToDateConverter
        implements Converter<String, Date> {

    private static String timestamp10 = "^[0-9]{10}$";
    private static String timestamp13 = "^[0-9]{13}$";

    private Logger logger = LoggerFactory.getLogger(StringToDateConverter.class);

    @Override
    public Date convert(String source) {
        if(StringUtils.isBlank(source)){
            return null;
        }

        Date result = null;
        try {
            if (Pattern.matches(timestamp10, source)) {
                result = new Date(Long.parseLong(source) * 1000);
            } else if (Pattern.matches(timestamp13, source)) {
                result = new Date(Long.parseLong(source));
            } else {
                result = DateUtils.parseDate(source, new String[]{"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"});
            }
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return result;
    }

}
