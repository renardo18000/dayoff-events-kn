package com.simon.env;

import java.lang.System;

@kotlin.Metadata(mv = {1, 7, 1}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010\u0003\u001a\u00020\u0004H\u0007\u00a8\u0006\u0005"}, d2 = {"Lcom/simon/env/EnvModule;", "", "()V", "getEnv", "Lorg/http4k/cloudnative/env/Environment;", "events-lambda"})
@org.koin.core.annotation.ComponentScan(value = "com.simon.env")
@org.koin.core.annotation.Module()
public final class EnvModule {
    
    public EnvModule() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @org.koin.core.annotation.Single()
    public final org.http4k.cloudnative.env.Environment getEnv() {
        return null;
    }
}