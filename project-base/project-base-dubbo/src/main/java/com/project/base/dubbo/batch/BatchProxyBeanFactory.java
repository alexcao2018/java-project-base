package com.project.base.dubbo.batch;

import com.alibaba.dubbo.config.spring.ReferenceBean;
import com.alibaba.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import com.google.common.collect.Lists;
import com.project.base.dubbo.annotation.DubboBatchOption;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zhanghc on 2017-08-23.
 */

public class BatchProxyBeanFactory implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    public BatchProxyBeanFactory() {

    }

    public <T> T createServiceProxyBean(Class<T> clazz) {

        Object proxy = Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[]{clazz},
                new TestInvocationHandler());

        return (T) proxy;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    class TestInvocationHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            ReferenceAnnotationBeanPostProcessor bean = applicationContext.getBean(ReferenceAnnotationBeanPostProcessor.class);
            List<ReferenceBean<?>> referenceBeans = bean.getReferenceBeans().stream().collect(Collectors.toList());

            Class<?> rootInterface = proxy.getClass().getInterfaces()[0].getInterfaces()[0];
            Object targetReference = null;
            for (ReferenceBean<?> referenceBean : referenceBeans) {
                if (referenceBean.getObjectType() != rootInterface)
                    continue;

                targetReference = referenceBean.getObject();
            }

            Method[] rootDeclaredMethods = proxy.getClass().getInterfaces()[0].getInterfaces()[0].getDeclaredMethods();
            Method[] proxyDeclaredMethods = proxy.getClass().getInterfaces()[0].getDeclaredMethods();
            boolean isMatchRootDeclaredMethod = false;
            boolean isInProxyDeclaredMethod = false;

            for (int i = 0; i < proxyDeclaredMethods.length; i++) {
                Method proxyDeclaredMethod = proxyDeclaredMethods[i];
                if (method.toString().equals(proxyDeclaredMethod.toString()))
                    isInProxyDeclaredMethod = true;
            }

            Method rootDeclaredMethod = null;
            if (isInProxyDeclaredMethod) {
                for (int i = 0; i < rootDeclaredMethods.length; i++) {
                    rootDeclaredMethod = rootDeclaredMethods[i];
                    String methodSignature = getMethodSignature(method);
                    if (methodSignature.indexOf(IDubboBatchCallback.class.getName()) > 0) {
                        methodSignature = methodSignature.replace("," + IDubboBatchCallback.class.getName(), "");
                    }

                    if (getMethodSignature(rootDeclaredMethod).equals(methodSignature)) {
                        isMatchRootDeclaredMethod = true;
                        break;
                    }
                }
            }

            Object result = null;
            List<Object> allResult = new ArrayList<>();
            if (isMatchRootDeclaredMethod) {
                IDubboBatchCallback dubboBatchCallback = null;
                if (args[args.length - 1] instanceof IDubboBatchCallback) {
                    dubboBatchCallback = (IDubboBatchCallback) args[args.length - 1];
                }

                try {
                    DubboBatchOption dubboBatchOption = method.getAnnotation(DubboBatchOption.class);
                    List collection = (List) args[dubboBatchOption.batchParameterPosition()];
                    List<List> partitionList = Lists.partition(collection, dubboBatchOption.batchCount());

                    for (int i = 0; i < partitionList.size(); i++) {
                        List partition = partitionList.get(i);
                        args[dubboBatchOption.batchParameterPosition()] = partition;
                        Object tempResult = rootDeclaredMethod.invoke(targetReference, dubboBatchCallback == null ? args : removeLastArgs(args));
                        if (dubboBatchOption.mergeResult()) {
                            Collection<?> tempResultCollection = (Collection<?>) tempResult;
                            if (tempResultCollection.size() > 0)
                                allResult.addAll(tempResultCollection);
                        } else {
                            allResult.add(tempResult);
                        }
                    }

                    if (dubboBatchCallback != null)
                        dubboBatchCallback.invokeCompleted(allResult, null);
                }
                catch (InvocationTargetException ex){
                    if (dubboBatchCallback != null)
                        dubboBatchCallback.invokeCompleted(allResult, ex);
                    else {
                        throw ex.getCause();
                    }
                }
                catch (Exception ex) {
                    if (dubboBatchCallback != null)
                        dubboBatchCallback.invokeCompleted(allResult, ex);
                    else {
                        throw ex;
                    }
                }
                return allResult;
            } else {
                result = method.invoke(targetReference, args);
                return result;
            }
        }

        private String getMethodSignature(Method method) {
            String methodName = method.toString().replaceFirst("\\(.*\\)", "").substring(method.toString().replaceFirst("\\(.*\\)", "").lastIndexOf(".") + 1);
            String methodParameter = method.toString().substring(method.toString().lastIndexOf("("));
            return methodName + methodParameter;
        }

        private Object[] removeLastArgs(Object[] args) {
            Object[] newArgs = new Object[args.length - 1];
            for (int i = 0; i < newArgs.length; i++) {
                newArgs[i] = args[i];
            }
            return newArgs;
        }

    }


}
