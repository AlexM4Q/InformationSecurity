package com.futur.infoseq.security.steno.exif;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public final class ExifForFile extends Exif<File> {

    public ExifForFile(@NotNull final File container, @NotNull final File destination) throws IOException, ImageReadException, ImageWriteException {
        super(container, destination);
    }

    @NotNull
    @Override
    public File encode(@NotNull final File input) throws IOException, ImageWriteException, ImageReadException {
        write(Files.readAllBytes(input.toPath()));
        return destination;
    }

    @NotNull
    @Override
    public File decode(@NotNull final File output) throws ImageWriteException, IOException, NoSuchFieldException, IllegalAccessException, ImageReadException {
        @Nullable final byte[] bytes = read();
        @NotNull final File file = new File(UUID.randomUUID().toString());

        if (bytes != null) {
            try (@NotNull final FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(bytes);
            }
        }

        return file;
    }

}
