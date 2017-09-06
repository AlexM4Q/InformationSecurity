package com.futur.infoseq.controllers;

import com.futur.infoseq.security.steno.ImageExifForString;
import com.futur.infoseq.service.ResourceService;
import javafx.fxml.Initializable;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public final class StartLayoutController implements Initializable {

    @NotNull
    private static final Logger LOG = LoggerFactory.getLogger(ResourceService.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File container = new File("file1.jpeg");
        File destination = new File("file2.jpeg");
        System.out.println(container.getAbsolutePath());
        System.out.println(destination.getAbsolutePath());
        ImageExifForString imageExifForString = new ImageExifForString(container, destination);
        imageExifForString.encode("");
    }

}
