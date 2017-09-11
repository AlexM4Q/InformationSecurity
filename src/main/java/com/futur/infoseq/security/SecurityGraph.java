package com.futur.infoseq.security;

import org.jetbrains.annotations.NotNull;

public interface SecurityGraph<I, O> {

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    O encode(@NotNull final I input) throws Throwable;

    @SuppressWarnings("UnusedReturnValue")
    @NotNull
    I decode(@NotNull final O output) throws Throwable;

}
