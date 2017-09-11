package com.futur.infoseq.security.steno.binary;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class BinaryForString extends Binary<String> {

    public BinaryForString(@NotNull final File container, @NotNull final File destination) throws IOException {
        super(container, destination);
    }

    @NotNull
    @Override
    public File encode(@NotNull final String input) throws Throwable {
        @NotNull final byte[] bytes = initBytes();
        @NotNull final byte[] inputBytes = input.getBytes();

        for (int i = 0, bit = 0; i < inputBytes.length; i++, bit += 8) {
            final boolean success = write(bytes, bit, inputBytes[i]);

            if (!success) {
                break;
            }
        }

        new FileOutputStream(destination).write(bytes);

        return destination;
    }

    @NotNull
    @Override
    public String decode(@NotNull final File output) throws Throwable {
        @NotNull final byte[] bytes = initBytes(output);
        @NotNull final List<Byte> outputBytesList = new ArrayList<>();

        for (int bit = 0; bit < bytes.length; bit += 8) {
            @Nullable final Byte read = read(bytes, bit);

            if (read == null) {
                break;
            } else {
                outputBytesList.add(read);
            }
        }

        @Nullable final byte[] outputBytes = new byte[outputBytesList.size()];
        for (int i = 0; i < outputBytesList.size(); i++) {
            outputBytes[i] = outputBytesList.get(i);
        }

        return new String(outputBytes);
    }

}
