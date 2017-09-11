package com.futur.infoseq.security.steno.exif;

import com.futur.common.helpers.DevelopmentHelper;
import com.futur.infoseq.security.steno.StenoGraph;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.constants.TiffDirectoryType;
import org.apache.commons.imaging.formats.tiff.fieldtypes.FieldType;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfoByte;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

abstract class Exif<I> extends StenoGraph<I> {

    private static final int BLOCK_LIMIT = 66000;

    @NotNull
    protected static final TagInfoByte TAG = new TagInfoByte("LOL", 55555, FieldType.BYTE, 0, TiffDirectoryType.EXIF_DIRECTORY_EXIF_IFD);

    protected Exif(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    protected final TiffOutputSet initOutputSet() throws IOException, ImageReadException, ImageWriteException {
        return initOutputSet(container);
    }

    @NotNull
    protected final TiffOutputSet initOutputSet(@NotNull final File source) throws IOException, ImageReadException, ImageWriteException {
        @Nullable TiffOutputSet outputSet = DevelopmentHelper.ifNotNull(getJpegMetadata(source), metadata -> {
            return DevelopmentHelper.ifNotNull(metadata.getExif(), exif -> {
                return DevelopmentHelper.executeSafe(exif::getOutputSet);
            });
        });

        return outputSet == null ? new TiffOutputSet() : outputSet;
    }

    @Nullable
    protected final byte[] read() throws IOException, ImageReadException {
        return DevelopmentHelper.ifNotNull(getJpegMetadata(destination), metadata -> {
            return metadata.findEXIFValue(TAG).getByteArrayValue();
            //todo make it for multi tag
        });
    }

    protected final void write(@NotNull final byte[] bytes) throws ImageWriteException, ImageReadException, IOException {
        @NotNull final TiffOutputSet outputSet = initOutputSet();
        @NotNull final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

        final int blocksCount = bytes.length / BLOCK_LIMIT + 1;
        for (int i = 0; i < blocksCount; i++) {
            final int from = i * BLOCK_LIMIT;

            @NotNull final byte[] block;
            if (i + 1 == blocksCount) {
                block = new byte[bytes.length - from];
            } else {
                block = new byte[BLOCK_LIMIT];
            }

            System.arraycopy(bytes, from, block, 0, block.length);

            @NotNull final TagInfoByte lol = new TagInfoByte("LOL" + i, 55555, FieldType.BYTE, 0, TiffDirectoryType.EXIF_DIRECTORY_EXIF_IFD);
            exifDirectory.removeField(lol);
            exifDirectory.add(lol, block);
        }

        save(outputSet);
    }

    protected final void save(@NotNull final TiffOutputSet outputSet) throws ImageWriteException, ImageReadException, IOException {
        try (@NotNull final OutputStream os = new BufferedOutputStream(new FileOutputStream(destination))) {
            new ExifRewriter().updateExifMetadataLossless(container, os, outputSet);
        }
    }

    @NotNull
    protected static JpegImageMetadata getJpegMetadata(@NotNull final File source) throws IOException, ImageReadException {
        return ((JpegImageMetadata) Imaging.getMetadata(source));
    }

}
