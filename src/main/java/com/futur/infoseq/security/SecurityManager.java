package com.futur.infoseq.security;

import com.futur.common.helpers.StringHelper;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class SecurityManager {

    private SecurityManager() {
        StringHelper.throwNonInitializeable();
    }

    @NotNull
    public static File encode() {
        return null;
    }

}
