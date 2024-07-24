package com.converter.colorconverter;

import com.converter.colorconverter.language.Language;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HelpWindowView {


    public Label help;
    public TextArea helpText;
    public Button closeButton;

    private Language language;
    private ObjectProperty<ResourceBundle> resources;

    protected void onStart() throws IOException {
        initLang();
    }

    private void initLang() throws IOException {
        language = new Language();
        resources = language.getResources();

        String[] langList = language.readLangFromFile();
        resources.set(ResourceBundle.getBundle
                ("bundles.gui", new Locale(langList[0], langList[1])));

        help.textProperty().bind(language.getStringBinding("help"));
        helpText.textProperty().bind(language.getStringBinding("helpText"));
        closeButton.textProperty().bind(language.getStringBinding("close"));
    }

    @FXML
    protected void closeButton(){
        HelpWindowApp.getStage().close();
    }
}
