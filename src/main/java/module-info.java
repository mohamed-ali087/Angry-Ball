module com.bouncingball.angryball.angryball {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.bouncingball.angryball to javafx.fxml;
    exports com.bouncingball.angryball;
}