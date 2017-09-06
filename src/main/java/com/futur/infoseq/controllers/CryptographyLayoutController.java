package com.futur.infoseq.controllers;

import com.futur.infoseq.security.steno.ImageExifForString;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;

import java.io.File;
import java.io.IOException;

public final class CryptographyLayoutController extends BaseController {

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
    private void decode_B_action() {
        File container = new File("file1.jpeg");
        File destination = new File("file2.jpeg");

        try {
            ImageExifForString imageExifForString = new ImageExifForString(container, destination);
            imageExifForString.encode(processingText_TA.getText());
        } catch (ImageWriteException | IOException | ImageReadException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void encode_B_action() {
        File container = new File("file1.jpeg");
        File destination = new File("file2.jpeg");

        try {
            ImageExifForString imageExifForString = new ImageExifForString(container, destination);
            String decode = imageExifForString.decode(destination);
            processingText_TA.setText(decode);
        } catch (ImageWriteException | IOException | ImageReadException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

}
