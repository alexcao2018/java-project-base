package com.project.base.redis;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
public class CacheHelper {
    @Autowired
    private DefaultKeyGenerator defaultKeyGenerator;
    private Logger logger = LoggerFactory.getLogger(CacheHelper.class);

    public String getSuffixByGenerator(JoinPoint joinPoint, String key, String keyGeneratorName, Method method) {
        String cachePostfix = StringUtils.EMPTY;
        if (StringUtils.isNotBlank(key)) {
            cachePostfix = parseKey(key, method, joinPoint.getArgs());
        } else if (StringUtils.isNotBlank(keyGeneratorName)) {
            Object objectGenerator = ApplicationContextAware4Redis.getContext().getBean(keyGeneratorName);
            if (!KeyGenerator.class.isAssignableFrom(objectGenerator.getClass())) {
                logger.error("缓存:key generator({}) 未实现 KeyGenerator 接口，将使用 defaultKeyGenerator !", keyGeneratorName);
                cachePostfix = defaultKeyGenerator.generate(joinPoint.getTarget(), method, joinPoint.getArgs()).toString();
            } else {
                KeyGenerator keyGenerator = (KeyGenerator) objectGenerator;
                cachePostfix = keyGenerator.generate(joinPoint.getTarget(), method, joinPoint.getArgs()).toString();
            }
        } else {
            cachePostfix = defaultKeyGenerator.generate(joinPoint.getTarget(), method, joinPoint.getArgs()).toString();
        }

        return cachePostfix;
    }

    private String parseKey(String key, Method method, Object[] args) {
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u =
                new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);

        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context, String.class);
    }

    public Method getMethod(JoinPoint joinPoint) {
        //获取参数的类型
        Object[] args = joinPoint.getArgs();
        Class[] argTypes = new Class[joinPoint.getArgs().length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        Method method = null;
        try {
            method = joinPoint.getTarget().getClass().getMethod(joinPoint.getSignature().getName(), argTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return method;

    }



}
