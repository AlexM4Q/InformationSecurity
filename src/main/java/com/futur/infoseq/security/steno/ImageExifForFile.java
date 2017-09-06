package com.futur.infoseq.security.steno;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public final class ImageExifForFile extends ImageExif<File, File> {

    public ImageExifForFile(@NotNull final File container, @NotNull final File destination) throws IOException, ImageReadException, ImageWriteException {
        super(container, destination);
    }

    @NotNull
    @Override
    public File encode(@NotNull final File input) {
        return null;
    }

    @NotNull
    @Override
    public File decode(@NotNull final File output) {
        return null;
    }

}
