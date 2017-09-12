package com.futur.infoseq.security.steno.exif;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class ExifForString extends Exif<String> {

    public ExifForString(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    @Override
    public File[] encode(@NotNull final String input) throws ImageWriteException, ImageReadException, IOException {
        return write(input.getBytes(CHARSET_NAME));
    }

    @NotNull
    @Override
    public String decode(@NotNull final File... output) throws ImageWriteException, ImageReadException, IOException, NoSuchFieldException, IllegalAccessException {
        return new String(read(), CHARSET_NAME);
    }

}
