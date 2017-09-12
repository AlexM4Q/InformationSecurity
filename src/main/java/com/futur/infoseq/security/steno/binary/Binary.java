package com.futur.infoseq.security.steno.binary;

import com.futur.infoseq.security.steno.StenoGraph;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

@SuppressWarnings("UnusedReturnValue")
abstract class Binary<I> extends StenoGraph<I> {

    protected Binary(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    protected byte[] read() throws IOException {
        @NotNull final BufferedImage image = userSpace(ImageIO.read(destination));
        @NotNull final byte[] byteData = getByteData(image);

        return decodeBytes(byteData);
    }

    protected void write(@NotNull final byte[] bytes) throws IOException {
        @NotNull final BufferedImage image = userSpace(ImageIO.read(container));

        addBytes(image, bytes);
        setImage(image, destination);
    }

    private static void addBytes(@NotNull final BufferedImage image, @NotNull final byte[] bytes) {
        @NotNull final byte imageBytes[] = getByteData(image);
        @NotNull final byte lengthBytes[] = bitConversion(bytes.length);

        encodeBytes(imageBytes, lengthBytes, 0);
        encodeBytes(imageBytes, bytes, 32);
    }

    private static void setImage(@NotNull final BufferedImage image, @NotNull final File file) throws IOException {
        ImageIO.write(image, "png", file);
    }

    /**
     * Creates a user space version of a Buffered Image, for editing and saving bytes
     *
     * @param image The image to put into user space, removes compression interferences
     * @return The user space version of the supplied image
     */
    @NotNull
    private static BufferedImage userSpace(@NotNull final BufferedImage image) {
        @NotNull final BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        @NotNull final Graphics2D graphics = newImage.createGraphics();

        graphics.drawRenderedImage(image, null);
        graphics.dispose();

        return newImage;
    }

    /**
     * Gets the byte array of an image
     *
     * @param image The image to get byte data from
     * @return Returns the byte array of the image supplied
     * @see WritableRaster
     * @see DataBufferByte
     */
    @NotNull
    private static byte[] getByteData(@NotNull final BufferedImage image) {
        @NotNull final WritableRaster raster = image.getRaster();
        @NotNull final DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    /**
     * Gernerates proper byte format of an integer
     *
     * @param i The integer to convert
     * @return Returns a byte[4] array converting the supplied integer into bytes
     */
    @NotNull
    private static byte[] bitConversion(final int i) {
        final byte byte3 = (byte) ((i & 0xFF000000) >>> 24);
        final byte byte2 = (byte) ((i & 0x00FF0000) >>> 16);
        final byte byte1 = (byte) ((i & 0x0000FF00) >>> 8);
        final byte byte0 = (byte) ((i & 0x000000FF));

        return new byte[]{byte3, byte2, byte1, byte0};
    }

    /**
     * Encode an array of bytes into another array of bytes at a supplied offset
     *
     * @param image    Array of data representing an image
     * @param addition Array of data to add to the supplied image data array
     * @param offset   The offset into the image array to add the addition data
     * @return Returns data Array of merged image and addition data
     */
    @NotNull
    private static byte[] encodeBytes(@NotNull final byte[] image, @NotNull final byte[] addition, int offset) {
        if (addition.length + offset > image.length) {
            throw new IllegalArgumentException("File not long enough!");
        }

        for (final byte add : addition) {
            for (int bit = 7; bit >= 0; --bit, ++offset) {
                final int b = (add >>> bit) & 1;
                image[offset] = (byte) ((image[offset] & 0xFE) | b);
            }
        }

        return image;
    }

    /**
     * Retrieves hidden text from an image
     *
     * @param image Array of data, representing an image
     * @return Array of data which contains the hidden text
     */
    @NotNull
    private static byte[] decodeBytes(@NotNull final byte[] image) {
        int length = 0;
        int offset = 32;

        for (int i = 0; i < 32; ++i) {
            length = (length << 1) | (image[i] & 1);
        }

        @NotNull final byte[] result = new byte[length];

        for (int b = 0; b < result.length; ++b) {
            for (int i = 0; i < 8; ++i, ++offset) {
                result[b] = (byte) ((result[b] << 1) | (image[offset] & 1));
            }
        }

        return result;
    }

}