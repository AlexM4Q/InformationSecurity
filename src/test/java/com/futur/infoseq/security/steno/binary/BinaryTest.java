package com.futur.infoseq.security.steno.binary;

import com.futur.common.helpers.resources.ResourcesHelper;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public final class BinaryTest {

    @Test
    public void encodeDecodeTest() throws Throwable {
        @NotNull final URL containerUrl = ResourcesHelper.getInternalUrl("white.jpg");
        @NotNull final URL destinationUrl = ResourcesHelper.getInternalUrl("output.png");

        @NotNull final File container = new File(containerUrl.getPath());
        @NotNull final File destination = new File(destinationUrl.getPath());
        @NotNull final BinaryForString coder = new BinaryForString(container, destination);

        @NotNull final String input = "some very long and interesting string for my cute test";
        coder.encode(input);
        @NotNull final String decode = coder.decode();

        Assert.assertEquals(input, decode);
    }

}
