package com.futur.infoseq.security.steno.exif;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public final class ExifForFile extends Exif<File> {

    public ExifForFile(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    @Override
    public File[] encode(@NotNull final File input) throws IOException, ImageWriteException, ImageReadException {
        return write(Files.readAllBytes(input.toPath()));
    }

    @NotNull
    @Override
    public File decode(@NotNull final File... output) throws ImageWriteException, IOException, NoSuchFieldException, IllegalAccessException, ImageReadException {
        @NotNull final File file = new File(UUID.randomUUID().toString());

        try (@NotNull final FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(read());
        }

        return file;
    }

}
