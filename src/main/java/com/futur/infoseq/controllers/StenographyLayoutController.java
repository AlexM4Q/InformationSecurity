package com.futur.infoseq.controllers;

import com.futur.infoseq.security.steno.exif.ExifForFile;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import java.io.File;
import java.io.IOException;

public final class StenographyLayoutController extends BaseController {

    @FXML
    private TextField containerPath_TF;
    @FXML
    private TextField path_TF;
    @FXML
    private Accordion processingTypeSelector_A;
    @FXML
    private TextArea processingText_TA;

    @FXML
    private void containerPath_B_action() {
    }

    @FXML
    private void path_B_action() {
    }

    @FXML
    private void decode_B_action() throws Throwable {
        File container = new File("file1.jpg");
        File destination = new File("file2.jpg");

        try {
            ExifForFile imageExifForString = new ExifForFile(container, destination);
            imageExifForString.decode(destination);
        } catch (ImageWriteException | IOException | ImageReadException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void encode_B_action() throws Throwable {
        File container = new File("file1.jpg");
        File destination = new File("file2.jpg");

        try {
            ExifForFile imageExifForString = new ExifForFile(container, destination);
            imageExifForString.encode(new File("file2.txt"));
        } catch (ImageWriteException | IOException | ImageReadException e) {
            e.printStackTrace();
        }
    }

}
