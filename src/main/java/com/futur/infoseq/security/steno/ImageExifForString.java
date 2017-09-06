package com.futur.infoseq.security.steno;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public final class ImageExifForString extends ImageExif<String, File> {

    @NotNull
    private final File container;
    @NotNull
    private final File destination;

    public ImageExifForString(@NotNull final File container, @NotNull final File destination) {
        this.container = container;
        this.destination = destination;
    }

    @NotNull
    @Override
    public File encode(@NotNull final String input) {
        try (@NotNull final OutputStream os = new BufferedOutputStream(new FileOutputStream(destination))) {

            @Nullable TiffOutputSet outputSet = null;

            @Nullable final JpegImageMetadata jpegMetadata = (JpegImageMetadata) Imaging.getMetadata(container);
            if (jpegMetadata != null) {
                @Nullable final TiffImageMetadata exif = jpegMetadata.getExif();
                if (exif != null) {
                    outputSet = exif.getOutputSet();
                }
            }

            if (null == outputSet) {
                outputSet = new TiffOutputSet();
            }

            @NotNull final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
            exifDirectory.removeField(ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
            exifDirectory.add(ExifTagConstants.EXIF_TAG_APERTURE_VALUE, new RationalNumber(3, 10));

//            exifDirectory.add(new TagInfoAscii());

            final double longitude = -74.0;
            final double latitude = 40 + 43 / 60.0;
            outputSet.setGPSInDegrees(longitude, latitude);

            new ExifRewriter().updateExifMetadataLossless(container, os, outputSet);
        } catch (IOException | ImageWriteException | ImageReadException e) {
            e.printStackTrace();
        }

        return destination;
    }

    @NotNull
    @Override
    public String decode(@NotNull final File output) {
        return null;
    }

}
