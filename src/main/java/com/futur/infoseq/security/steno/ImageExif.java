package com.futur.infoseq.security.steno;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

abstract class ImageExif<I, O> implements StenoGraph<I, O> {

    @NotNull
    protected static final String TAG = "lolkekcheburek";

    @NotNull
    protected final File container;
    @NotNull
    protected final File destination;

    public ImageExif(@NotNull final File container, @NotNull final File destination) throws IOException, ImageReadException, ImageWriteException {
        this.container = container;
        this.destination = destination;
    }

    @NotNull
    protected TiffOutputSet initOutputSet() throws IOException, ImageReadException, ImageWriteException {
        @Nullable TiffOutputSet outputSet = null;

        @Nullable final JpegImageMetadata jpegMetadata = (JpegImageMetadata) Imaging.getMetadata(container);
        if (jpegMetadata != null) {
            @Nullable final TiffImageMetadata exif = jpegMetadata.getExif();
            if (exif != null) {
                outputSet = exif.getOutputSet();
            }
        }

        if (outputSet == null) {
            outputSet = new TiffOutputSet();
        }

        return outputSet;
    }

    protected void save(@NotNull final TiffOutputSet outputSet) throws ImageWriteException, ImageReadException, IOException {
        try (@NotNull final OutputStream os = new BufferedOutputStream(new FileOutputStream(destination))) {
            new ExifRewriter().updateExifMetadataLossless(container, os, outputSet);
        }
    }

}
