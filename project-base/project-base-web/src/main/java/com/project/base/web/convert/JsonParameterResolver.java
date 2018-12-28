package com.project.base.web.convert;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import java.util.Map;
import java.util.TimeZone;

/**
 * 针对参数名上有@JsonProperty生效
 * 从请求中获取参数
 * 1，首先按参数名称从querystring中取值
 * 2，若未取到，则将首字母改为大写再取值
 * 3，若仍未取到，则从json= 的string中取值，参数名称优先值 value>name(首字母大写)>name
 *
 * 不可同时与@RequestParam使用，@RequestParam优先级比JsonProperty高
 */
public class JsonParameterResolver extends RequestParamMethodArgumentResolver {

    ObjectMapper objectMapper;

    public JsonParameterResolver(){
        super(true);
        objectMapper=(new ObjectMapper()).setTimeZone(TimeZone.getTimeZone("GMT+8")).setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if(super.supportsParameter(parameter)&&parameter.getParameterAnnotation(JsonProperty.class)!=null){
            return true;
        }
        return false;
    }

    @Override
    protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request) throws Exception {
        Object r= super.resolveName(name, parameter, request);
        if(r==null){
            r=super.resolveName(name.substring(0,1).toUpperCase()+name.substring(1),parameter,request);
        }
        if(r==null){
            JsonProperty jsonProperty=parameter.getParameterAnnotation(JsonProperty.class);
            String field=jsonProperty.value();
            if(StringUtils.isEmpty(field)){
                field=name.substring(0,1).toUpperCase()+name.substring(1);
            }
            //从Json中取
            String json=request.getParameter("json");
            if(!StringUtils.isEmpty(json)){
                Map<String,String> map=objectMapper.readValue(json,Map.class);
                r= map.get(field);
                if(r==null){
                    r=map.get(name);
                }
            }
        }
        return r;
    }
}
