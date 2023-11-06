module com.example.brickbreakergame {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.brickbreakergame to javafx.fxml;
    exports com.example.brickbreakergame;
}