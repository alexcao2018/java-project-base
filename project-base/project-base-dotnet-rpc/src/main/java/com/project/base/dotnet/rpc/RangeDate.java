package com.project.base.dotnet.rpc;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public class RangeDate extends Range<Date> {

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+08:00")
    private Date left;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss+08:00")
    private Date right;
}
