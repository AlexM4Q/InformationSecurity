package com.futur.infoseq.security.steno;

import com.futur.infoseq.security.SecurityGraph;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public abstract class StenoGraph<I> implements SecurityGraph<I, File> {

    @NotNull
    protected final File container;
    @NotNull
    protected final File destination;

    protected StenoGraph(@NotNull final File container, @NotNull final File destination) {
        this.container = container;
        this.destination = destination;
    }

}
