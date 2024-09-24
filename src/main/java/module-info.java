module com.faraimunashe.superpos {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.net.http;
    requires com.google.gson;
    requires com.fasterxml.jackson.databind;

    // Open the controllers package for reflective access by FXMLLoader
    opens com.faraimunashe.superpos.Controllers to javafx.fxml;

    // The root package can still be exported
    opens com.faraimunashe.superpos to javafx.fxml;
    exports com.faraimunashe.superpos;
}
