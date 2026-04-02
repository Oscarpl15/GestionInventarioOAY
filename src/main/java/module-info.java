module com.oay.gestioninventariooay {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.oay.gestioninventariooay to javafx.fxml;
    exports com.oay.gestioninventariooay;
}