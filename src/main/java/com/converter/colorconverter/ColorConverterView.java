package com.converter.colorconverter;

import com.converter.colorconverter.logic.ColorConvert;
import com.converter.colorconverter.logic.ColorConvertEnum;
import javafx.fxml.FXML;
import static com.converter.colorconverter.logic.ColorConvertEnum.*;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class ColorConverterView {
    public TextField rgbText;
    public TextField rgbaText;
    public TextField hexText;
    public TextField hslaText;
    public TextField cmykText;
    public AnchorPane scene;
    public ImageView image;
    public Circle circle;
    public Label noImageLabel;

    private Robot robot;
    private KeyCodeCombination pastKeyCombination;

    protected void onStart() throws AWTException {
        setHandlers();
        setStartValues();
    }

    private void setHandlers() throws AWTException {

        //------handler for catch ctrl v press and paste image in imageView------//
        pastKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(!pastKeyCombination.match(keyEvent)) {
                pasteImageFromClipBoard();
            }
        });

        //------handler for set dot, when mouse clicked on imageView and get the color------//
        robot = new Robot();
        image.setOnMouseClicked(
                e -> {
                    circle.setVisible(false);
                    double centerX = e.getX() - 296; //почему
                    double centerY = e.getY() - 261; //почему
                    circle.setCenterX(centerX);
                    circle.setCenterY(centerY);
                    circle.setVisible(true);

                    Color color = robot.getPixelColor(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
                    System.out.println(color);
                    changeColorFields(color);
                }
        );
    }

    private void pasteImageFromClipBoard() {
        Image img = Clipboard.getSystemClipboard().getImage();
        if (img != null)
        {
            noImageLabel.setVisible(false);
            image.setImage(img);
        }
        else {
            noImageLabel.setVisible(true);
        }
    }

    private void changeColorFields(Color color){
        LinkedHashMap<ColorConvertEnum, String> colors = ColorConvert.convert(color);
        rgbText.setText(colors.get(RGB));
        rgbaText.setText(colors.get(RGBA));
        hexText.setText(colors.get(HEX));
        hslaText.setText(colors.get(HSLA));
        cmykText.setText(colors.get(CMYK));
    }

    @FXML
    protected void setStartValues(){
        changeColorFields(new Color(255, 255, 255, 0));
        image.setImage(new Image("images\\circle.png")); //у брат
    }
}