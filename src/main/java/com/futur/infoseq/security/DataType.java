package com.futur.infoseq.security;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public enum DataType {

    STRING("Текст"),
    FILE("Файл");

    @NotNull
    private final String label;

    DataType(@NotNull final String label) {
        this.label = label;
    }

    @NotNull
    @Contract(pure = true)
    @Override
    public String toString() {
        return label;
    }

}
