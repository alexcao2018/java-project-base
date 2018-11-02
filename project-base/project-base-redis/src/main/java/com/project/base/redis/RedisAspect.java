package com.project.base.redis;

import com.project.base.redis.annotation.RedisCacheable;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class RedisAspect {
    @Autowired
    private RedisClient redisClient;

    @Pointcut("@annotation(com.project.base.redis.annotation.RedisCacheable)")
    public void pointcutMethod() {

    }


    @Around("pointcutMethod()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object result = null;
        Method method = getMethod(joinPoint);
        RedisCacheable redisCacheable = method.getAnnotation(RedisCacheable.class);
        if (redisCacheable.cacheNames() == null || redisCacheable.cacheNames().length == 0) {
            try {
                result = joinPoint.proceed();
                return result;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        //TODO

        String cachePrefix = redisCacheable.cacheNames()[0];
        String cachePostfix = parseKey(redisCacheable.key(), method, joinPoint.getArgs());

        return result;
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


    public Method getMethod(ProceedingJoinPoint joinPoint) {
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
