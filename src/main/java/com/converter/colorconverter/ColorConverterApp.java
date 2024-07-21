package com.converter.colorconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;

public class ColorConverterApp extends Application {
    @Override
    public void start(Stage stage) throws IOException, AWTException {
        FXMLLoader fxmlLoader = new FXMLLoader(ColorConverterApp.class.getResource("colorConverter.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 675);
        ColorConverterView cvc = fxmlLoader.getController();
        cvc.onStart();
        stage.setTitle("Color converter");
        stage.setScene(scene);
        stage.show();
    }

    public static void launchApp() {
        launch();
    }
}