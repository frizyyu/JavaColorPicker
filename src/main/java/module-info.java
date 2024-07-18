module com.converter.colorconverter {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    requires com.dlsc.formsfx;

    opens com.converter.colorconverter to javafx.fxml;
    exports com.converter.colorconverter;
    exports com.converter.colorconverter.logic;
    opens com.converter.colorconverter.logic to javafx.fxml;
}