package com.futur.infoseq;

import com.futur.common.helpers.resources.FXMLHelper;
import com.futur.common.log.LogInitializer;
import com.futur.infoseq.service.ResourceService;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class StartApplication extends Application {

    @NotNull
    private static final String LOG_PROPERTY_NAME = "ISLogbackPath";
    @NotNull
    private static final String APPLICATION_NAME = "Information Security";

    private static final int APPLICATION_WIDTH = 1200;
    private static final int APPLICATION_HEIGHT = 800;

    public static void main(@NotNull final String[] args) throws Throwable {
        launch(args);

//        char[] password = new char[]{'p' , 'a' , 's' , 's' , 'w' , 'o' , 'r' , 'd'};// your password, possibly from JPasswordField
//        Steganography steg = new Steganography();
//        steg.setCompression(false); // enable compression
//        steg.setEncryption(false); // enable encryption
//        steg.hide(new File("C:\\path\\carrier\\Pictures"), new File("C:\\path\\secret\\tt.txt"), new File("C:\\path\\output\\hide"), "secret message to be encoded", password);
//        steg.reveal(new File("C:\\path\\steg\\Pictures"), new File("C:\\path\\output\\revel"), password);
    }

    @Override
    public void start(@NotNull final Stage primaryStage) throws Exception {
        LogInitializer.initLogFileName(APPLICATION_NAME, LOG_PROPERTY_NAME);

        @NotNull final Parent root = FXMLHelper.loadNode(ResourceService.START_LAYOUT_FXML);
        @NotNull final Scene scene = new Scene(root, APPLICATION_WIDTH, APPLICATION_HEIGHT);

        primaryStage.setTitle(APPLICATION_NAME);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}