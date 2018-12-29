package com.project.base.dotnet.rpc.context;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public class DotnetRpcContextManager {

    private static DotnetRpcContextCache contextCache = new TtlDotnetRpcContextCache();

    public static void save(DotnetRpcContext context) {
        contextCache.save(context);
    }

    public static void clear() {
        contextCache.clear();
    }

    public static DotnetRpcContext getContext() {
        return contextCache.getContext();
    }
}
