package com.futur.infoseq.security;

import org.jetbrains.annotations.NotNull;

public interface SecurityGraph<I, O> {

    @NotNull
    O encode(@NotNull final I input);

    @NotNull
    I decode(@NotNull final O output);

}