package com.futur.infoseq.security.steno.exif;

import com.futur.common.helpers.DevelopmentHelper;
import com.futur.infoseq.security.steno.StenoGraph;
import com.google.common.base.Preconditions;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

abstract class Exif<I> extends StenoGraph<I> {

    private static final int BLOCK_LIMIT = 65000;

    protected Exif(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    protected final TiffOutputSet initOutputSet() throws IOException, ImageReadException, ImageWriteException {
        return initOutputSet(container);
    }

    @NotNull
    protected final TiffOutputSet initOutputSet(@NotNull final File source) throws IOException, ImageReadException, ImageWriteException {
        @Nullable final TiffOutputSet outputSet = DevelopmentHelper.ifNotNull(getJpegMetadata(source), metadata -> {
            return DevelopmentHelper.ifNotNull(metadata.getExif(), exif -> {
                return DevelopmentHelper.executeSafe(exif::getOutputSet);
            });
        });

        return outputSet == null ? new TiffOutputSet() : outputSet;
    }

    @NotNull
    protected final byte[] read() throws IOException, ImageReadException {
        @NotNull final TagInfoByte tag = createTag();
        @NotNull byte[] bytes = new byte[0];

        @Nullable final File[] files = getNeededFiles(destination);
        if (files == null) {
            return bytes;
        }

        for (@Nullable final File file : files) {
            assert file != null;

            @Nullable final byte[] block = DevelopmentHelper.ifNotNull(getJpegMetadata(file), metadata -> {
                return DevelopmentHelper.ifNotNull(metadata.findEXIFValue(tag), TiffField::getByteArrayValue);
            });

            Preconditions.checkNotNull(block);

            //noinspection NullableProblems
            bytes = sum(bytes, block);
        }

        return bytes;
    }

    @NotNull
    protected final File[] write(@NotNull final byte[] bytes) throws ImageWriteException, ImageReadException, IOException {
        @NotNull final TagInfoByte lol = createTag();

        final int blocksCount = bytes.length / BLOCK_LIMIT + 1;
        @NotNull final File[] files = new File[blocksCount];

        for (int i = 0; i < blocksCount; i++) {
            files[i] = new File(destination, FILENAME_PREFIX + fixLength(i, blocksCount) + ".jpg");
            @NotNull final TiffOutputSet outputSet = initOutputSet();
            @NotNull final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

            final int from = i * BLOCK_LIMIT;

            @NotNull final byte[] block;
            if (i + 1 == blocksCount) {
                block = new byte[bytes.length - from];
            } else {
                block = new byte[BLOCK_LIMIT];
            }

            System.arraycopy(bytes, from, block, 0, block.length);

            exifDirectory.removeField(lol);
            exifDirectory.add(lol, block);

            save(outputSet, files[i]);
        }

        return files;
    }

    protected final void save(@NotNull final TiffOutputSet outputSet, @NotNull final File destination) throws ImageWriteException, ImageReadException, IOException {
        try (@NotNull final OutputStream os = new BufferedOutputStream(new FileOutputStream(destination))) {
            new ExifRewriter().updateExifMetadataLossless(container, os, outputSet);
        }
    }

    @Nullable
    protected static JpegImageMetadata getJpegMetadata(@NotNull final File source) throws IOException, ImageReadException {
        return (JpegImageMetadata) Imaging.getMetadata(source);
    }

    @NotNull
    private static TagInfoByte createTag() {
        return new TagInfoByte("LOL", 55555, FieldType.BYTE, 0, TiffDirectoryType.EXIF_DIRECTORY_EXIF_IFD);
    }

    @NotNull
    private static byte[] sum(@NotNull final byte[] a, @NotNull final byte[] b) {
        @NotNull final byte[] sum = new byte[a.length + b.length];
        System.arraycopy(a, 0, sum, 0, a.length);
        System.arraycopy(b, 0, sum, a.length, b.length);
        return sum;
    }

    @NotNull
    private static String fixLength(final int value, final int max) {
        final int length = String.valueOf(max).length();

        @NotNull final StringBuilder builder = new StringBuilder(String.valueOf(value));
        while (builder.length() < length) {
            builder.insert(0, "0");
        }

        return builder.toString();
    }

    @Nullable
    private static File[] getNeededFiles(@NotNull final File filesDir) {
        return filesDir.listFiles(pathname -> pathname.getName().startsWith(FILENAME_PREFIX) && pathname.getName().endsWith(".jpg"));
    }

}
