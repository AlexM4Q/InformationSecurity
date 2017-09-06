package com.futur.infoseq.security.steno;

import com.futur.common.helpers.ReflectionHelper;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputField;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public final class ImageExifForString extends ImageExif<String, File> {

    public ImageExifForString(@NotNull final File container, @NotNull final File destination) throws ImageWriteException, ImageReadException, IOException {
        super(container, destination);
    }

    @NotNull
    @Override
    public File encode(@NotNull final String input) throws ImageWriteException, ImageReadException, IOException {
        @NotNull final TiffOutputSet outputSet = initOutputSet();
        @NotNull final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_USER_COMMENT);
        exifDirectory.add(ExifTagConstants.EXIF_TAG_USER_COMMENT, input);

        save(outputSet);

        return destination;
    }

    @NotNull
    @Override
    public String decode(@NotNull final File output) throws ImageWriteException, ImageReadException, IOException, NoSuchFieldException, IllegalAccessException {
        @NotNull final TiffOutputSet outputSet = initOutputSet();
        @NotNull final TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();
        @NotNull final TiffOutputField userCommentField = exifDirectory.findField(ExifTagConstants.EXIF_TAG_USER_COMMENT);

        @NotNull final Field bytesField = ReflectionHelper.getField(TiffOutputField.class, "bytes");
        @NotNull final byte[] bytes = (byte[]) bytesField.get(userCommentField);
        @NotNull final String userComment = new String(bytes);

        return userComment.substring("ASCII".length(), userComment.length());
    }

}
