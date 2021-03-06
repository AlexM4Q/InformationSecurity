package com.futur.infoseq.controllers;

import com.futur.common.helpers.DevelopmentHelper;
import com.futur.infoseq.security.DataType;
import com.futur.infoseq.security.steno.StenographyType;
import com.futur.infoseq.security.steno.binary.Binary;
import com.futur.ui.FormatterHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.val;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public final class StenographyLayoutController extends BaseController {

    @NotNull
    private final FileChooser fileChooser;
    @NotNull
    private final DirectoryChooser directoryChooser;
    @NotNull
    private final ValidationSupport validation;

    @FXML
    private TextField containerPath_TF;
    @FXML
    private TextField destinationPath_TF;
    @FXML
    private ComboBox<StenographyType> type_CB;
    @FXML
    private TextField bitsCount_TF;
    @FXML
    private ComboBox<DataType> data_CB;
    @FXML
    private TextField filePath_TF;
    @FXML
    private Accordion processingTypeSelector_A;
    @FXML
    private TextArea processingText_TA;

    public StenographyLayoutController() {
        this.validation = new ValidationSupport();
        this.fileChooser = new FileChooser();
        this.directoryChooser = new DirectoryChooser();
    }

    @Override
    public void initialize(@NotNull final URL location, @Nullable final ResourceBundle resources) {
        type_CB.getItems().addAll(StenographyType.values());
        data_CB.getItems().addAll(DataType.values());

        FormatterHelper.applyIntegerFormat(bitsCount_TF);

        validation.setErrorDecorationEnabled(false);
        validation.registerValidator(containerPath_TF, (c, value) -> {
            final boolean condition = value instanceof String
                    && new File(value.toString()).isFile()
                    && (value.toString().endsWith(".jpg") || value.toString().endsWith(".jpeg"));
            return ValidationResult.fromMessageIf(c, "Путь должен указывать на jpg/jpeg файл", Severity.ERROR, !condition);
        });
        validation.registerValidator(destinationPath_TF, (c, value) -> {
            @NotNull final String message;
            final boolean condition;

            @Nullable final StenographyType selectedItem = type_CB.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                message = "Не выбран тип шифрования";
                condition = false;
            } else {
                switch (selectedItem) {
                    case BINARY:
                        message = "Путь должен указывать на png файл";
                        condition = value instanceof String
                                && !value.toString().trim().isEmpty()
                                && new File(value.toString()).isFile()
                                && value.toString().endsWith(".png");
                        break;
                    case EXIF:
                        message = "Путь должен указывать на каталог";
                        condition = value instanceof String
                                && !((String) value).trim().isEmpty()
                                && new File((String) value).isDirectory();
                        break;
                    default:
                        message = String.format("Непредвиденная ошибка: %s", selectedItem);
                        condition = false;
                        break;
                }
            }

            return ValidationResult.fromMessageIf(c, message, Severity.ERROR, !condition);
        });
        validation.registerValidator(type_CB, Validator.createEmptyValidator("Выберете тип шифрования"));
        validation.registerValidator(data_CB, Validator.createEmptyValidator("Выберете тип шифруемых данных"));
        validation.registerValidator(filePath_TF, (c, value) -> {
            final boolean condition = value instanceof String && new File(value.toString()).isFile();
            return ValidationResult.fromMessageIf(c, "Путь должен указывать на файл", Severity.ERROR, !condition);
        });

        type_CB.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> bitsCount_TF.setDisable(newValue != StenographyType.BINARY));
        data_CB.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            final int index = newValue.intValue();
            if (0 <= index && index < processingTypeSelector_A.getPanes().size()) {
                processingTypeSelector_A.getPanes().get(index).setExpanded(true);
                fixFilePathValidationRequire();
            }
        });
        processingTypeSelector_A.expandedPaneProperty().addListener((observable, oldValue, newValue) -> {
            final int index = processingTypeSelector_A.getPanes().indexOf(newValue);
            if (0 <= index && index < data_CB.getItems().size()) {
                data_CB.getSelectionModel().select(index);
                fixFilePathValidationRequire();
            }
        });
    }

    private void fixFilePathValidationRequire() {
        ValidationSupport.setRequired(filePath_TF, data_CB.getSelectionModel().getSelectedItem() == DataType.FILE);
    }

    @FXML
    private void containerPath_B_action() {
        DevelopmentHelper.ifNotNull(fileChooser.showOpenDialog(null), file -> {
            containerPath_TF.setText(file.getAbsolutePath());
        });
    }

    @FXML
    private void destinationPath_B_action() {
        if (type_CB.getSelectionModel().getSelectedItem() == StenographyType.EXIF) {
            DevelopmentHelper.ifNotNull(directoryChooser.showDialog(null), file -> {
                destinationPath_TF.setText(file.getAbsolutePath());
            });
        } else {
            DevelopmentHelper.ifNotNull(fileChooser.showOpenDialog(null), file -> {
                destinationPath_TF.setText(file.getAbsolutePath());
            });
        }
    }

    @FXML
    private void filePath_B_action() {
        DevelopmentHelper.ifNotNull(fileChooser.showOpenDialog(null), file -> {
            filePath_TF.setText(file.getAbsolutePath());
        });
    }

    @FXML
    private void decode_B_action() throws Throwable {
        validation.setErrorDecorationEnabled(true);
        validation.redecorate();

        if (!validation.isInvalid()) {
            @NotNull final File container = new File(containerPath_TF.getText());
            @NotNull final File destination = new File(destinationPath_TF.getText());

            @NotNull final StenographyType type = type_CB.getSelectionModel().getSelectedItem();
            switch (data_CB.getSelectionModel().getSelectedItem()) {
                case STRING:
                    @NotNull val stringStenoGraph = type.initializeForString(container, destination);
                    if (type == StenographyType.BINARY) {
                        FormatterHelper.getIntegerNumeric(bitsCount_TF, ((Binary) stringStenoGraph)::setBytesCount);
                    }
                    processingText_TA.setText(stringStenoGraph.decode(new File[0]));
                    break;
                case FILE:
                    @NotNull val fileStenoGraph = type.initializeForFile(container, destination);
                    if (type == StenographyType.BINARY) {
                        FormatterHelper.getIntegerNumeric(bitsCount_TF, ((Binary) fileStenoGraph)::setBytesCount);
                    }
                    fileStenoGraph.decode(new File[0]);
                    break;
            }
        }
    }

    @FXML
    private void encode_B_action() throws Throwable {
        validation.setErrorDecorationEnabled(true);
        validation.redecorate();

        if (!validation.isInvalid()) {
            @NotNull final File container = new File(containerPath_TF.getText());
            @NotNull final File destination = new File(destinationPath_TF.getText());

            @NotNull final StenographyType type = type_CB.getSelectionModel().getSelectedItem();
            switch (data_CB.getSelectionModel().getSelectedItem()) {
                case STRING:
                    @NotNull val stringStenoGraph = type.initializeForString(container, destination);
                    if (type == StenographyType.BINARY) {
                        FormatterHelper.getIntegerNumeric(bitsCount_TF, ((Binary) stringStenoGraph)::setBytesCount);
                    }
                    stringStenoGraph.encode(processingText_TA.getText());
                    break;
                case FILE:
                    @NotNull val fileStenoGraph = type.initializeForFile(container, destination);
                    if (type == StenographyType.BINARY) {
                        FormatterHelper.getIntegerNumeric(bitsCount_TF, ((Binary) fileStenoGraph)::setBytesCount);
                    }
                    fileStenoGraph.encode(new File(filePath_TF.getText()));
                    break;
            }
        }
    }

}
