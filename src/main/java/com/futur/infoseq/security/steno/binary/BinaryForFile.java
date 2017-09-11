package com.futur.infoseq.security.steno.binary;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class BinaryForFile extends Binary<File> {

    protected BinaryForFile(@NotNull File container, @NotNull File destination) {
        super(container, destination);
    }

    @NotNull
    @Override
    public File encode(@NotNull File input) throws Throwable {
        return null;
    }

    @NotNull
    @Override
    public File decode(@NotNull File output) throws Throwable {
        return null;
    }

}
