package com.project.base.dotnet.rpc.context;

/**
 * Created by Yangchao.
 * Date: 2018/12/29
 */
public interface DotnetRpcContextCache {

    void save(DotnetRpcContext context);

    void clear();

    DotnetRpcContext getContext();
}
