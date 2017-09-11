package com.futur.infoseq.security.steno.exif;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public final class ExifForString extends Exif<String> {

    public ExifForString(@NotNull final File container, @NotNull final File destination) throws ImageWriteException, ImageReadException, IOException {
        super(container, destination);
    }

    @NotNull
    @Override
    public File encode(@NotNull final String input) throws ImageWriteException, ImageReadException, IOException {
        write(input.getBytes("UTF-8"));
        return destination;
    }

    @NotNull
    @Override
    public String decode(@NotNull final File output) throws ImageWriteException, ImageReadException, IOException, NoSuchFieldException, IllegalAccessException {
        @Nullable final byte[] read = read();
        if (read == null) {
            return "";
        }

        return new String(read, "UTF-8");
    }

}
