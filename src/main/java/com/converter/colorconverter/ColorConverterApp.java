package com.converter.colorconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ColorConverterApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ColorConverterApp.class.getResource("colorConverter.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1165, 715);
        ColorConverterView cvc = fxmlLoader.getController();
        cvc.onStart();
        HelpWindowApp.setMainStage(stage);
        stage.getIcons().add(new Image(Objects.requireNonNull(ColorConverterApp.class.getResourceAsStream("/com/converter/colorconverter/images/circle.png"))));
        stage.setTitle("Color converter");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void launchApp() {
        launch();
    }
}