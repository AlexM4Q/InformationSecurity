package com.futur.infoseq.controllers;

import com.futur.common.helpers.DevelopmentHelper;
import com.futur.infoseq.security.steno.StenographyType;
import com.futur.infoseq.security.steno.binary.BinaryForFile;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.ImageWriteException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public final class StenographyLayoutController extends BaseController {

    @NotNull
    private static final FileChooser fileChooser = new FileChooser();

    @NotNull
    private static final DirectoryChooser directoryChooser = new DirectoryChooser();

    @FXML
    private TextField containerPath_TF;
    @FXML
    private TextField destinationPath_TF;
    @FXML
    private ComboBox<StenographyType> type_CB;
    @FXML
    private TextField filePath_TF;
    @FXML
    private Accordion processingTypeSelector_A;
    @FXML
    private TextArea processingText_TA;

    @Override
    public void initialize(@NotNull final URL location, @Nullable final ResourceBundle resources) {
        type_CB.getItems().addAll(StenographyType.values());
    }

    @FXML
    private void containerPath_B_action() {
        DevelopmentHelper.ifNotNull(fileChooser.showOpenDialog(null), file -> {
            containerPath_TF.setText(file.getAbsolutePath());
        });
    }

    @FXML
    private void destinationPath_B_action() {
        DevelopmentHelper.ifNotNull(fileChooser.showOpenDialog(null), file -> {
            destinationPath_TF.setText(file.getAbsolutePath());
        });
    }

    @FXML
    private void filePath_B_action() {
        DevelopmentHelper.ifNotNull(fileChooser.showOpenDialog(null), file -> {
            filePath_TF.setText(file.getAbsolutePath());
        });
    }

    @FXML
    private void decode_B_action() throws Throwable {
        @NotNull final File container = new File(containerPath_TF.getText());
        @NotNull final File destination = new File(destinationPath_TF.getText());

        if (processingTypeSelector_A.getPanes().get(0).isExpanded()) {

        }

//        type_CB.getValue().

        try {
            @NotNull final BinaryForFile imageExifForString = new BinaryForFile(container, destination);
            imageExifForString.decode();
        } catch (ImageWriteException | IOException | ImageReadException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void encode_B_action() throws Throwable {
        @NotNull final File container = new File(containerPath_TF.getText());
        @NotNull final File destination = new File(destinationPath_TF.getText());

        try {
            @NotNull final BinaryForFile imageExifForString = new BinaryForFile(container, destination);
            imageExifForString.encode(new File("cat.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
