package com.converter.colorconverter;

import com.converter.colorconverter.logic.ColorConvert;
import com.converter.colorconverter.logic.ColorConvertEnum;
import javafx.fxml.FXML;
import static com.converter.colorconverter.logic.ColorConvertEnum.*;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Objects;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class ColorConverterView {
    public TextField rgbText;
    public TextField rgbaText;
    public TextField hexText;
    public TextField hslaText;
    public TextField cmykText;
    public AnchorPane scene;
    public ImageView image;
    public Circle circle; //for colorPalette
    public Label noImageLabel;
    public Canvas colorPalette;
    public Canvas hueStrip;
    public Rectangle colorRect;
    public Circle circle1; //for image
    public Canvas alphaStrip;
    public Rectangle alphaRect;

    private double paletteX = 0.0;
    private double paletteY = 0.0;
    private boolean isColorChanging = false;
    private boolean isColorChoosing = false;
    private boolean isAlphaChanging = false;
    private boolean isStartImageGetColoring = false;
    private final double MAXRECTY = 175.0;
    private final double MINRECTY = -14.0;
    private final double MAXRECTX = 16.0;
    private final double MINRECTX = -173.0;
    private int currAlpha = 255;
    private Color currColor = new Color(255, 255, 255);
    private Robot robot;
    private KeyCodeCombination pastKeyCombination;
    private javafx.scene.paint.Color selectedColor = javafx.scene.paint.Color.RED;

    protected void onStart() throws AWTException {
        setHandlers();
        setStartValues();
    }

    private void setHandlers() throws AWTException {

        //------handler for catch ctrl v press and paste image in imageView------//
        pastKeyCombination = new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(!pastKeyCombination.match(keyEvent)) { //изменить, чтобы работало только для ctrl+v и при этом не менялось значение в полях для цветов
                pasteImageFromClipBoard();
            }
        });

        //------handler for set ImageGetColor, when mouse clicked on imageView and get the color------//
        robot = new Robot();
        image.addEventHandler(MouseEvent.MOUSE_PRESSED, this::startImageGetColor);
        image.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::setImageGetColorHandler);
        image.addEventHandler(MouseEvent.MOUSE_EXITED, this::stopImageGetColor);
        image.addEventHandler(MouseEvent.MOUSE_ENTERED, this::startImageGetColor);

        //------handler for color choose------//
        colorPalette.addEventHandler(MouseEvent.MOUSE_PRESSED, this::startColorChoose);
        colorPalette.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::colorChooseHandler);
        colorPalette.addEventHandler(MouseEvent.MOUSE_EXITED, this::stopColorChoose);
        colorPalette.addEventHandler(MouseEvent.MOUSE_ENTERED, this::startColorChoose);

        //------handler for color change------//
        hueStrip.addEventHandler(MouseEvent.MOUSE_PRESSED, this::startColorChange);
        hueStrip.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::colorChangeHandler);
        hueStrip.addEventHandler(MouseEvent.MOUSE_EXITED, this::stopColorChange);
        hueStrip.addEventHandler(MouseEvent.MOUSE_ENTERED, this::startColorChange);

        //------handler for alpha change------//
        alphaStrip.addEventHandler(MouseEvent.MOUSE_PRESSED, this::startAlphaChange);
        alphaStrip.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::alphaChangeHandler);
        alphaStrip.addEventHandler(MouseEvent.MOUSE_EXITED, this::stopAlphaChange);
        alphaStrip.addEventHandler(MouseEvent.MOUSE_ENTERED, this::startAlphaChange);

    }


    //------------handlers------------//

    private void startAlphaChange(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            circle1.setVisible(false);
            isAlphaChanging = true;
            alphaChangeHandler(event);
        }
    }
    private void alphaChangeHandler(MouseEvent event){
        if (isAlphaChanging){
            rectMoving(event);
            double y = event.getY();
            char[] chArr = String.format("%s", 1  - (y / alphaStrip.getHeight()) + 0.0000000001f).toCharArray();
            currAlpha = (int) (255 * Double.parseDouble(String.format("%c%c%c%c", chArr[0], chArr[1], chArr[2], chArr[3])));
            changeColorFields(new Color(currColor.getRed(), currColor.getGreen(), currColor.getBlue(), currAlpha));
        }
    }
    private void stopAlphaChange(MouseEvent event){
        isAlphaChanging = false;
    }

    private void startColorChange(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            circle1.setVisible(false);
            isColorChanging = true;
            colorChangeHandler(event);
        }
    }
    private void colorChangeHandler(MouseEvent event){
        if (isColorChanging) {
            rectMoving(event);
            double y = event.getY();
            double hue = (y / hueStrip.getHeight()) * 360;
            selectedColor = javafx.scene.paint.Color.hsb(hue, 1, 1);
            updateColorPalette();

            getColor();
        }
    }
    private void stopColorChange(MouseEvent event){
        isColorChanging = false;
    }

    private void startColorChoose(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            circle1.setVisible(false);
            isColorChoosing = true;
            colorChooseHandler(event);
        }
    }
    private void colorChooseHandler(MouseEvent event){
        if (isColorChoosing) {
            dotMoving(event);
            paletteX = event.getX();
            paletteY = event.getY();
            getColor();
        }
    }
    private void stopColorChoose(MouseEvent event){
        isColorChoosing = false;
    }

    private void startImageGetColor(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            circle1.setVisible(true);
            isStartImageGetColoring = true;
            setImageGetColorHandler(event);
        }
    }
    private void setImageGetColorHandler(MouseEvent e){
        if (isStartImageGetColoring) {
            dotMoving(e);
            Color color = robot.getPixelColor(MouseInfo.getPointerInfo().getLocation().x, MouseInfo.getPointerInfo().getLocation().y);
            changeColorFields(color);
        }
    }
    private void stopImageGetColor(MouseEvent event){
        isStartImageGetColoring = false;
    }
    //------------------------------------//

    private void rectMoving(MouseEvent e){
        if (e.getSource() == hueStrip) {
            double centerY = e.getY() - 19; //почему
            if (centerY > MAXRECTY) {
                centerY = MAXRECTY;
            } else if (centerY < MINRECTY) {
                centerY = MINRECTY;
            }
            colorRect.setY(centerY);
        }
        else if (e.getSource() == alphaStrip){
            double centerX = -1 * e.getY() + 19; //почему
            if (centerX > MAXRECTX) {
                centerX = MAXRECTX;
            } else if (centerX < MINRECTX) {
                centerX = MINRECTX;
            }
            alphaRect.setX(centerX);
        }
    }
    private void dotMoving(MouseEvent e){
        double centerX;
        double centerY;
        if (e.getSource() == colorPalette) {
            centerX = e.getX() - 545; //почему
            centerY = e.getY() - 247; //почему
            circle.setCenterX(centerX);
            circle.setCenterY(centerY);

        }
        else if (e.getSource() == image)
        {
            centerX = e.getX() - 296; //почему
            centerY = e.getY() - 261; //почему
            circle1.setCenterX(centerX);
            circle1.setCenterY(centerY);
        }
    }
    private void getColor(){ //void for get color and call update fields
        String selectedColorAsString = getColorFromPalette(paletteX, paletteY);
        currColor = new Color(Integer.valueOf(selectedColorAsString.substring(0, 2), 16), Integer.valueOf(selectedColorAsString.substring(2, 4), 16), Integer.valueOf(selectedColorAsString.substring(4, 6), 16), currAlpha);
        updateAlphaStrip(currColor);
        //change color fields values
        changeColorFields(currColor);
    }
    private String getColorFromPalette(double x, double y){
        WritableImage snapshot = colorPalette.snapshot(null, null);
        selectedColor = snapshot.getPixelReader().getColor((int) x, (int) y);
        return String.format("%s", selectedColor).replace("0x", "");
    }

    private void updateColorPalette() {
        GraphicsContext gc = colorPalette.getGraphicsContext2D();
        for (int x = 0; x < colorPalette.getWidth(); x++) {
            for (int y = 0; y < colorPalette.getHeight(); y++) {
                double saturation = x / colorPalette.getWidth();
                double brightness = 1 - (y / colorPalette.getHeight());
                gc.getPixelWriter().setColor(x, y, javafx.scene.paint.Color.hsb(selectedColor.getHue(), saturation, brightness));
            }
        }
    }
    private void updateAlphaStrip(Color color){
        GraphicsContext gsa = alphaStrip.getGraphicsContext2D();
        gsa.clearRect(0, 0, alphaStrip.getWidth(), alphaStrip.getHeight());
        for (int y = 0; y < alphaStrip.getHeight(); y++){
            gsa.setStroke(javafx.scene.paint.Color.color(0, 0, 0, 0));
            double alpha = 1 - (y / alphaStrip.getHeight());
            gsa.setStroke(javafx.scene.paint.Color.color((double) color.getRed() / 255, (double) color.getGreen() / 255, (double) color.getBlue() / 255, alpha));
            gsa.strokeLine(0, y, alphaStrip.getWidth(), y);
        }
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
        circle1.setVisible(false);
        circle1.setMouseTransparent(true);

        circle.setVisible(true);
        circle.setCenterX(-545.0);
        circle.setCenterY(-247.0);
        circle.setMouseTransparent(true);

        colorRect.setY(MAXRECTY);
        colorRect.setMouseTransparent(true);

        alphaRect.setX(MAXRECTX);
        alphaRect.setMouseTransparent(true);

        //set view for color strip//
        GraphicsContext gc = hueStrip.getGraphicsContext2D();
        for (int y = 0; y < hueStrip.getHeight(); y++) {
            double hue = (y / hueStrip.getHeight()) * 360;
            gc.setStroke(javafx.scene.paint.Color.hsb(hue, 1, 1));
            gc.strokeLine(0, y, hueStrip.getWidth(), y);
        }

        //set view for alpha strip//
        updateAlphaStrip(currColor);

        updateColorPalette();
        changeColorFields(currColor);
    }
}