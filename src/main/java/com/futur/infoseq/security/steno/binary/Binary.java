package com.futur.infoseq.security.steno.binary;

import com.futur.infoseq.security.steno.StenoGraph;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

abstract class Binary<I> extends StenoGraph<I> {

    private static final int DEFAULT_BITS_COUNT = 1;

    @Setter
    protected int bitsCount;

    protected Binary(@NotNull final File container, @NotNull final File destination) {
        super(container, destination);

        this.bitsCount = DEFAULT_BITS_COUNT;
    }

    @NotNull
    protected final byte[] initBytes() throws IOException {
        return initBytes(container);
    }

    @NotNull
    protected final byte[] initBytes(@NotNull final File source) throws IOException {
        return Files.readAllBytes(source.toPath());
    }

    protected final void save() {

    }

    protected static boolean write(@NotNull final byte[] bytes, final int from, final byte info) {
        if (from + 8 >= bytes.length) {
            return false;
        }

        @NotNull final char[] bits = fixLength(Integer.toBinaryString(info)).toCharArray();

        for (int i = from; i < from + 8; i++) {
            @NotNull final String byteBits = Integer.toBinaryString(bytes[i] + 128);
            @NotNull final char[] byteBitsChars = byteBits.toCharArray();

            byteBitsChars[byteBitsChars.length - 1] = bits[i - from];
            bytes[i] = (byte) (Integer.parseInt(new String(byteBitsChars), 2) - 128);
        }

        return true;
    }

    @Nullable
    protected static Byte read(@NotNull final byte[] bytes, final int from) {
        if (from + 8 >= bytes.length) {
            return null;
        }

        @NotNull final char[] bits = new char[8];

        for (int i = from; i < from + 8; i++) {
            @NotNull final String byteBits = Integer.toBinaryString(bytes[from] + 128);
            @NotNull final char[] byteBitsChars = byteBits.toCharArray();

            bits[i - from] = byteBitsChars[byteBitsChars.length - 1];
        }

        return (byte) (Integer.parseInt(new String(bits), 2) - 128);
    }

    private static String fixLength(@NotNull final String line) {
        if (line.length() == 8) {
            return line;
        }

        @NotNull final StringBuilder lineBuilder = new StringBuilder(line);
        while (lineBuilder.length() < 8) {
            lineBuilder.insert(0, "0");
        }

        return lineBuilder.toString();
    }

    public static void printBits(@NotNull final byte[] bytes) {
        for (byte aByte : bytes) {
            System.out.print(Integer.toBinaryString(aByte + 128));
        }

        System.out.println();
    }

}