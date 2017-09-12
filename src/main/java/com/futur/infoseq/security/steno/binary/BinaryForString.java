package com.futur.infoseq.security.steno.binary;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class BinaryForString extends Binary<String> {

    public BinaryForString(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    @Override
    public File[] encode(@NotNull final String input) throws Throwable {
        write(input.getBytes());

        return new File[]{destination};
    }

    @NotNull
    @Override
    public String decode(@NotNull final File... output) throws Throwable {
        return new String(read());
    }

}
