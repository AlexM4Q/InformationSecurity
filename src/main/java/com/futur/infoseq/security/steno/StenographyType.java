package com.futur.infoseq.security.steno;

import com.futur.infoseq.security.steno.binary.BinaryForFile;
import com.futur.infoseq.security.steno.binary.BinaryForString;
import com.futur.infoseq.security.steno.exif.ExifForFile;
import com.futur.infoseq.security.steno.exif.ExifForString;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.function.BiFunction;

public enum StenographyType {

    BINARY("Бинарный", BinaryForFile::new, BinaryForString::new),
    EXIF("EXIF", ExifForFile::new, ExifForString::new);

    @NotNull
    private final String label;
    @NotNull
    private final BiFunction<File, File, StenoGraph<File>> initializerForFile;
    @NotNull
    private final BiFunction<File, File, StenoGraph<String>> initializerForString;

    StenographyType(@NotNull final String label,
                    @NotNull final BiFunction<File, File, StenoGraph<File>> initializerForFile,
                    @NotNull final BiFunction<File, File, StenoGraph<String>> initializerForString) {
        this.label = label;
        this.initializerForFile = initializerForFile;
        this.initializerForString = initializerForString;
    }

    @NotNull
    public StenoGraph<File> initializeForFile(@NotNull final File container, @NotNull final File destination) {
        return initializerForFile.apply(container, destination);
    }

    @NotNull
    public StenoGraph<String> initializeForString(@NotNull final File container, @NotNull final File destination) {
        return initializerForString.apply(container, destination);
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public String toString() {
        return label;
    }

}
