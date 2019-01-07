package com.project.base.dotnet.rpc.context;

import com.alibaba.ttl.TransmittableThreadLocal;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public class TtlDotnetRpcContextCache implements DotnetRpcContextCache {

    private static final TransmittableThreadLocal<DotnetRpcContext> threadLocal = new TransmittableThreadLocal<>();

    @Override
    public void save(DotnetRpcContext context) {
        if (context == null) {
            throw new NullPointerException("rpc context can not be null");
        }
        threadLocal.set(context);
    }

    @Override
    public void clear() {
        threadLocal.remove();
    }

    @Override
    public DotnetRpcContext getContext() {
        DotnetRpcContext context = threadLocal.get();
        if (context == null) {
            throw new NullPointerException("dotnet rpc context is null");
        }
        return context;
    }
}
