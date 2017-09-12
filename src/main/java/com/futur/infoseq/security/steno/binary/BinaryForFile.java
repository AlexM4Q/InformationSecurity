package com.futur.infoseq.security.steno.binary;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public final class BinaryForFile extends Binary<File> {

    public BinaryForFile(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    @Override
    public File[] encode(@NotNull final File input) throws IOException {
        write(Files.readAllBytes(input.toPath()));

        return new File[]{destination};
    }

    @NotNull
    @Override
    public File decode(@NotNull final File... output) throws Throwable {
        @NotNull final File file = new File(UUID.randomUUID().toString());

        try (@NotNull final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(read());
        }

        return file;
    }

}
