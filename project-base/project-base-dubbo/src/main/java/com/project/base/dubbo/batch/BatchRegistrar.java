package com.project.base.dubbo.batch;

import com.project.base.dubbo.annotation.DubboBatchInvoke;
import com.project.base.dubbo.annotation.EnableDubboBatchInvoke;
import org.reflections.Reflections;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

public class BatchRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(importingClassMetadata
                .getAnnotationAttributes(EnableDubboBatchInvoke.class.getName()));
        String[] packages = attributes.getStringArray("value");

        Reflections reflections = new Reflections(packages[0]);
        Set<Class<?>> annotatedClazzCollection = reflections.getTypesAnnotatedWith(DubboBatchInvoke.class);

        for (Class<?> clazz : annotatedClazzCollection) {

            GenericBeanDefinition proxyBeanDefinition = new GenericBeanDefinition();
            proxyBeanDefinition.setBeanClass(clazz);

            ConstructorArgumentValues args = new ConstructorArgumentValues();
            args.addGenericArgumentValue(clazz);
            proxyBeanDefinition.setConstructorArgumentValues(args);

            proxyBeanDefinition.setFactoryBeanName("batchProxyBeanFactory");
            proxyBeanDefinition.setFactoryMethodName("createServiceProxyBean");
            registry.registerBeanDefinition(clazz.getSimpleName(), proxyBeanDefinition);
        }
    }
}
