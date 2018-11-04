package com.project.base.redis;

import com.project.base.redis.annotation.RedisCacheEvict;
import com.project.base.redis.annotation.RedisCacheable;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
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
import java.text.MessageFormat;
import java.util.Set;

@Component
@Aspect
public class RedisAspect {

    private Logger logger = LoggerFactory.getLogger(RedisAspect.class);

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private DefaultKeyGenerator defaultKeyGenerator;

    @Pointcut("@annotation(com.project.base.redis.annotation.RedisCacheable)")
    public void pointcutCacheableMethod() {

    }

    @Pointcut("@annotation(com.project.base.redis.annotation.RedisCacheEvict)")
    public void pointcutCacheEvictMethod() {

    }

    @Before("pointcutCacheEvictMethod()")
    public void beforeCacheEvict(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        RedisCacheEvict redisCacheEvict = method.getAnnotation(RedisCacheEvict.class);
        if (redisCacheEvict.cacheNames() == null || redisCacheEvict.cacheNames().length == 0) {
            return;
        }

        String cachePrefix = redisCacheEvict.cacheNames()[0];
        String cachePostfix = getPostfixByGenerator(joinPoint, redisCacheEvict.key(), redisCacheEvict.keyGenerator(), method);
        String cacheKey = MessageFormat.format("{0}:{1}", cachePrefix, cachePostfix);

        boolean allEntries = redisCacheEvict.allEntries();
        if (allEntries) {
            Set<String> keys = redisClient.keys(cachePrefix + ":*");
            redisClient.del(keys.toArray(new String[keys.size()]));
            logger.info("缓存:{},移除", keys);
        } else if (StringUtils.isNotBlank(redisCacheEvict.key())) {
            redisClient.del(cacheKey);
        }
        logger.info("缓存:{},移除", cacheKey);
    }


    @Around("pointcutCacheableMethod()")
    public Object aroundCacheable(ProceedingJoinPoint joinPoint) {
        Object result = null;
        Method method = getMethod(joinPoint);
        RedisCacheable redisCacheable = method.getAnnotation(RedisCacheable.class);
        if (redisCacheable.cacheNames() == null || redisCacheable.cacheNames().length == 0) {
            try {
                result = joinPoint.proceed();
                return result;
            } catch (Throwable throwable) {
                logger.error(throwable.getMessage(), throwable);
            }
        }

        String cachePrefix = redisCacheable.cacheNames()[0];
        String cachePostfix = getPostfixByGenerator(joinPoint, redisCacheable.key(), redisCacheable.keyGenerator(), method);
        String cacheKey = MessageFormat.format("{0}:{1}", cachePrefix, cachePostfix);
        int cacheTimeout = redisCacheable.timeout();
        result = redisClient.get(cacheKey);
        if (result == null) {
            logger.info("缓存:{},未命中", cacheKey);
            try {
                result = joinPoint.proceed();
                if (result != null) {
                    redisClient.set(cacheKey, result, cacheTimeout);
                    logger.info("缓存:{},添加到缓存，过期时间{}秒", cacheKey, cacheTimeout);
                }
            } catch (Throwable throwable) {
                logger.error(throwable.getMessage(), throwable);
            }
        } else {
            logger.info("缓存:{},命中", cacheKey);
        }

        return result;
    }

    private String getPostfixByGenerator(JoinPoint joinPoint, String key, String keyGeneratorName, Method method) {

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
