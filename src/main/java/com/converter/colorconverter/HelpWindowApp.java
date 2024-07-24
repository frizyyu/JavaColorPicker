package com.converter.colorconverter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Objects;

public class HelpWindowApp extends Application {
    private static Stage secondStage;
    private static Stage mainStage;
    @Override
    public void start(Stage stage) throws Exception {
        secondStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(HelpWindowApp.class.getResource("/com/converter/colorconverter/help.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 558, 605);
        HelpWindowView HWV = fxmlLoader.getController();
        HWV.onStart();
        secondStage.getIcons().add(new Image(Objects.requireNonNull(HelpWindowApp.class.getResourceAsStream("/com/converter/colorconverter/images/circle.png"))));
        secondStage.initOwner(mainStage);
        secondStage.initModality(Modality.WINDOW_MODAL);
        secondStage.setTitle("Help");
        secondStage.setScene(scene);
        secondStage.setResizable(false);
        secondStage.showAndWait();
    }

    public static Stage getStage(){
        return secondStage;
    }

    public static void setMainStage(Stage stage){
        mainStage = stage;
    }
}
