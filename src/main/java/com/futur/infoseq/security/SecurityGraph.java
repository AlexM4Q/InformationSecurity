package com.futur.infoseq.security;

import org.jetbrains.annotations.NotNull;

public interface SecurityGraph<I, O> {

    @NotNull String CHARSET_NAME = "UTF-8";

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    O encode(@NotNull final I input) throws Throwable;

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    I decode(@NotNull final O output) throws Throwable;

}
