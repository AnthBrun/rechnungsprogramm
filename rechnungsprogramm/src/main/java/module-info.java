module com.rechnungsprogramm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.apache.pdfbox;
    requires java.desktop;
    requires javafx.swing;

    opens com.GUI to javafx.fxml;
    opens com to javafx.graphics;
    exports com.GUI;
}
