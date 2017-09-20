package com.futur.infoseq.security.steno.binary;

import com.futur.infoseq.security.steno.StenoGraph;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Contract;
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

    @Getter
    private short bytesCount = 5;

    protected Binary(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);
    }

    @NotNull
    protected byte[] read() throws IOException {
        @NotNull final BufferedImage image = getUserSpace(ImageIO.read(destination));
        @NotNull final byte[] byteData = getByteData(image);

        return decodeBytes(byteData);
    }

    protected void write(@NotNull final byte[] bytes) throws IOException {
        @NotNull final BufferedImage image = getUserSpace(ImageIO.read(container));

        setBytes(image, bytes);
        setImage(image, destination);
    }

    private void setBytes(@NotNull final BufferedImage image, @NotNull final byte[] bytes) {
        @NotNull final byte imageBytes[] = getByteData(image);
        @NotNull final byte lengthBytes[] = bitConversion(bytes.length);

        @NotNull final IntPair pointerOffset = new IntPair();
        encodeBytes(imageBytes, lengthBytes, pointerOffset);
        encodeBytes(imageBytes, bytes, pointerOffset);
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
    @Contract(pure = true)
    private static BufferedImage getUserSpace(@NotNull final BufferedImage image) {
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
    @Contract(pure = true)
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
    @Contract(pure = true)
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
     * @param image         Array of data representing an image
     * @param addition      Array of data to add to the supplied image data array
     * @param pointerOffset The offset into the image array to add the addition data
     */
    private void encodeBytes(@NotNull final byte[] image, @NotNull final byte[] addition, @NotNull final IntPair pointerOffset) {
        if (addition.length + pointerOffset.offset > image.length) {
            throw new IllegalArgumentException("File not long enough!");
        }

        for (byte add : addition) {
            for (int i = 0; i < Byte.SIZE; ) {
                for (int byteCounter = 0; byteCounter < bytesCount && i < Byte.SIZE; pointerOffset.pointer++, i++, byteCounter++) {
                    image[pointerOffset.offset] = setBit(image[pointerOffset.offset], pointerOffset.pointer % bytesCount, getBit(add, 7 - i));

                    if (pointerOffset.pointer % bytesCount == 0) {
                        pointerOffset.offset++;
                    }
                }
            }
        }
    }

    /**
     * Retrieves hidden text from an image
     *
     * @param image Array of data, representing an image
     * @return Array of data which contains the hidden text
     */
    @NotNull
    private byte[] decodeBytes(@NotNull final byte[] image) {
        int pointer = 0;        // глобальный индекс бита
        int offset = 0;         // глобальный индекс байта

        int length = 0;
        for (int i = 0; i < Integer.SIZE; ) {
            for (int byteCounter = 0; byteCounter < bytesCount && i < Integer.SIZE; pointer++, i++, byteCounter++) {
                length = addBit(length, getBit(image[offset], pointer % bytesCount));

                if (pointer % bytesCount == 0) {
                    offset++;
                }
            }
        }

        @NotNull final byte[] result = new byte[length];
        for (int b = 0; b < result.length; b++) {
            for (int i = 0; i < Byte.SIZE; ) {
                for (int byteCounter = 0; byteCounter < bytesCount && i < Byte.SIZE; pointer++, i++, byteCounter++) {
                    result[b] = (byte) addBit(result[b], getBit(image[offset], pointer % bytesCount));

                    if (pointer % bytesCount == 0) {
                        offset++;
                    }
                }
            }
        }

        return result;
    }

    private static int getBit(final byte data, final int position) {
        return (data >>> position) & 1;
    }

    private static byte setBit(final byte data, final int position, final int value) {
        switch (position) {
            case 0:
                return (byte) ((data & 0xFE) | (value << position));
            case 1:
                return (byte) ((data & 0xFD) | (value << position));
            case 2:
                return (byte) ((data & 0xFB) | (value << position));
            case 3:
                return (byte) ((data & 0xF7) | (value << position));
            case 4:
                return (byte) ((data & 0xEF) | (value << position));
            case 5:
                return (byte) ((data & 0xDF) | (value << position));
            case 6:
                return (byte) ((data & 0xBF) | (value << position));
            case 7:
                return (byte) ((data & 0x7F) | (value << position));
            default:
                throw new RuntimeException(String.format("Data: %s, position: %s, value: %s", data, position, value));
        }
    }

    private static int addBit(final int data, final int value) {
        return (data << 1) | value;
    }

    private static final class IntPair {
        @Getter
        @Setter
        private int pointer;    // глобальный индекс бита
        @Getter
        @Setter
        private int offset;     // глобальный индекс байта
    }

}