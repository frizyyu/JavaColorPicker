package com.converter.colorconverter;

import com.converter.colorconverter.logic.ColorConvert;
import com.converter.colorconverter.logic.ColorConvertEnum;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.LinkedHashMap;

import static com.converter.colorconverter.logic.ColorConvertEnum.*;

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
    public Rectangle resultColor;

    private double paletteX = 0.0;
    private double paletteY = 0.0;
    private double MAXPALETTEX = 200.0;
    private double MINPALETTEX = 0.0;
    private double MAXPALETTEY = 200.0;
    private double MINPALETTEY = 0.0;
    private boolean isColorChanging = false;
    private boolean isColorChoosing = false;
    private boolean isAlphaChanging = false;
    private boolean isStartImageGetColoring = false;
    private final double MAXRECTY = 175.0;
    private final double MINRECTY = -14.0;
    private final double MAXRECTX = 16.0;
    private final double MINRECTX = -173.0;
    private int currAlpha = 255;
    private javafx.scene.paint.Color selectedColor = javafx.scene.paint.Color.WHITE;

    protected void onStart() {
        setHandlers();
        setStartValues();
    }

    private void setHandlers() {

        //------handler for catch ctrl v press and paste image in imageView------//
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if(keyEvent.isControlDown() && keyEvent.getCode() == KeyCode.V) {
                pasteImageFromClipBoard();
            }
            else if(keyEvent.getCode() == KeyCode.ESCAPE) //event for setting focus on scene to correct work ctrl+v press
                scene.requestFocus();
        });

        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> scene.requestFocus()); //event for setting focus on scene to correct work ctrl+v press

        //------handler for set ImageGetColor, when mouse clicked on imageView and get the color------//
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

        //------handlers for change color when fields were changed------//
        rgbText.textProperty().addListener((observableValue, s, t1) -> {
            if (rgbText.isFocused()) {
                updateColorStripesAndPalette(ColorConvert.convertToRGBA(rgbText.getText().split(", "), RGB));
            }
        });
        rgbaText.textProperty().addListener((observableValue, s, t1) -> {
            if (rgbaText.isFocused()) {
                updateColorStripesAndPalette(ColorConvert.convertToRGBA(rgbaText.getText().split(", "), RGBA));
            }
        });
        hexText.textProperty().addListener((observableValue, s, t1) -> {
            if (hexText.isFocused() && hexText.getLength() == 9) {
                updateColorStripesAndPalette(ColorConvert.convertToRGBA(new String[]{hexText.getText(), String.valueOf(currAlpha)}, HEX));
            }
        });
        hslaText.textProperty().addListener((observableValue, s, t1) -> {
            String hslaTextS = hslaText.getText();
            if (hslaText.isFocused() && !hslaTextS.endsWith(".")) {
                updateColorStripesAndPalette(ColorConvert.convertToRGBA(hslaTextS.replace("%", "").split(", "), HSLA));
            }
        });
        cmykText.textProperty().addListener((observableValue, s, t1) -> {
            if (cmykText.isFocused()) {
                String[] inp = cmykText.getText().split(", ");
                updateColorStripesAndPalette(ColorConvert.convertToRGBA(new String[]{inp[0], inp[1], inp[2], inp[3], String.valueOf(currAlpha)}, CMYK));
            }
        });


    }


    //------------handlers------------//
    //---alpha handlers---//
    private void startAlphaChange(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            alphaStrip.requestFocus();
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
            changeColorFields(new Color((int) selectedColor.getRed() * 255, (int) (selectedColor.getGreen() * 255), (int) (selectedColor.getBlue() * 255), currAlpha));
        }
    }
    private void stopAlphaChange(MouseEvent event){
        isAlphaChanging = false;
    }
    //------//

    //---color change handlers---//
    private void startColorChange(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            hueStrip.requestFocus();
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
            //change color fields values
            changeColorFields(new Color((int) (selectedColor.getRed() * 255), (int) (selectedColor.getGreen() * 255), (int) (selectedColor.getBlue() * 255), (int) selectedColor.getOpacity() * 255));
        }
    }
    private void stopColorChange(MouseEvent event){
        isColorChanging = false;
    }
    //------//

    //---color palette handlers---//
    private void startColorChoose(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            colorPalette.requestFocus();
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
            //change color fields values
            changeColorFields(new Color((int) (selectedColor.getRed() * 255), (int) (selectedColor.getGreen() * 255), (int) (selectedColor.getBlue() * 255), (int) selectedColor.getOpacity() * 255));
        }
    }
    private void stopColorChoose(MouseEvent event){
        isColorChoosing = false;
    }
    //------//

    //---image handlers---//
    private void startImageGetColor(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            image.requestFocus();
            circle1.setVisible(true);
            isStartImageGetColoring = true;
            setImageGetColorHandler(event);
        }
    }
    private void setImageGetColorHandler(MouseEvent e){
        try {
            if (isStartImageGetColoring) {
                dotMoving(e);
                PixelReader pr = image.getImage().getPixelReader();
                double x = e.getX() * image.getImage().getWidth() / image.getBoundsInLocal().getWidth();
                double y = e.getY() * image.getImage().getHeight() / image.getBoundsInLocal().getHeight();
                selectedColor = pr.getColor((int) x, (int) y);
                changeColorFields(new Color((int) (selectedColor.getRed() * 255), (int) (selectedColor.getGreen() * 255), (int) (selectedColor.getBlue() * 255), (int) (selectedColor.getOpacity() * 255)));
                updateColorStripesAndPalette(selectedColor);
            }
        } catch (IndexOutOfBoundsException ignored){}
    }
    private void stopImageGetColor(MouseEvent event){
        isStartImageGetColoring = false;
    }
    //------//
    //------------------------------------//

    //---voids for moving circles and rectangles for choosing colors---//
    private void rectMoving(MouseEvent e){
        if (e.getSource() == hueStrip) {
            double centerY = e.getY() - 19;
            if (centerY > MAXRECTY) {
                centerY = MAXRECTY;
            } else if (centerY < MINRECTY) {
                centerY = MINRECTY;
            }
            colorRect.setY(centerY);
        }
        else if (e.getSource() == alphaStrip){
            double centerX = -1 * e.getY() + 19; 
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
            centerX = e.getX() - 545; 
            centerY = e.getY() - 247; 
            circle.setCenterX(centerX);
            circle.setCenterY(centerY);

        }
        else if (e.getSource() == image)
        {
            centerX = e.getX() - 296;
            centerY = e.getY() - 261;
            circle1.setCenterX(centerX);
            circle1.setCenterY(centerY);
        }
    }
    //------//

    private void updateColorStripesAndPalette(javafx.scene.paint.Color color){
        selectedColor = color;
        currAlpha = (int) (color.getOpacity() * 255);
        updateColorPalette();
        moveCircleByColor();
        moveColorRectByColor();
        updateAlphaStrip();
        moveAlphaRectByColor();
        changeColorFields(new Color((int) (selectedColor.getRed() * 255), (int) (selectedColor.getGreen() * 255), (int) (selectedColor.getBlue() * 255), currAlpha));

    }

    private void moveCircleByColor(){
        javafx.scene.paint.Color checkColor = new javafx.scene.paint.Color(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), 1.0);
        paletteX = checkColor.getSaturation() * colorPalette.getWidth();
        paletteY = (1 - checkColor.getBrightness()) * colorPalette.getHeight();
        if (paletteY > MAXPALETTEY)
            paletteY = MAXPALETTEY;
        else if (paletteY < MINPALETTEY)
            paletteY = MINPALETTEY;

        if (paletteX > MAXPALETTEX)
            paletteX = MAXPALETTEX;
        else if (paletteX < MINPALETTEX)
            paletteX = MINPALETTEX;

        circle.setCenterX(paletteX - 545);
        circle.setCenterY(paletteY - 247);
    }

    private void moveColorRectByColor(){
        double y = ((selectedColor.getHue() / 360) * hueStrip.getHeight()) - 19;
        if (y > MAXRECTY) {
            y = MAXRECTY;
        } else if (y < MINRECTY) {
            y = MINRECTY;
        }
        colorRect.setY(y);
    }

    private void moveAlphaRectByColor(){
        double x = (selectedColor.getOpacity() / 360) * alphaStrip.getHeight() * 340 - 173;
        alphaRect.setX(x);
    }

    private void getColor(){ //void for get color
        String selectedColorAsString = getColorFromPalette(paletteX, paletteY);
        selectedColor = new javafx.scene.paint.Color(Integer.valueOf(selectedColorAsString.substring(0, 2), 16) / 255.0, Integer.valueOf(selectedColorAsString.substring(2, 4), 16) / 255.0, Integer.valueOf(selectedColorAsString.substring(4, 6), 16) / 255.0, currAlpha / 255.0);
        updateAlphaStrip();
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
    private void updateAlphaStrip(){
        GraphicsContext gsa = alphaStrip.getGraphicsContext2D();
        gsa.clearRect(0, 0, alphaStrip.getWidth(), alphaStrip.getHeight());
        for (int y = 0; y < alphaStrip.getHeight(); y++){
            gsa.setStroke(javafx.scene.paint.Color.color(0, 0, 0, 0));
            double alpha = 1 - (y / alphaStrip.getHeight());
            gsa.setStroke(javafx.scene.paint.Color.color(selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue(), alpha));
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
        LinkedHashMap<ColorConvertEnum, String> colors = ColorConvert.convert(new Color(color.getRed(), color.getGreen(), color.getBlue(), currAlpha));
        rgbText.setText(colors.get(RGB));
        rgbaText.setText(colors.get(RGBA));
        hexText.setText(colors.get(HEX));
        hslaText.setText(colors.get(HSLA));
        cmykText.setText(colors.get(CMYK));
        resultColor.setFill(new javafx.scene.paint.Color(color.getRed()/255.0, color.getGreen()/255.0, color.getBlue()/255.0, currAlpha / 255.0));
    }

    @FXML
    protected void setStartValues(){
        scene.requestFocus();
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
        updateAlphaStrip();

        updateColorPalette();
        changeColorFields(new Color((int) (selectedColor.getRed() * 255), (int) (selectedColor.getGreen() * 255), (int) (selectedColor.getBlue() * 255), (int) selectedColor.getOpacity() * 255));
    }
}