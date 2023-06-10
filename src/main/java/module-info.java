module com.example.game2048 {
    requires javafx.controls;
    requires javafx.fxml;
//    requires jfxrt;
//    requires rt;


    opens com.example.game2048 to javafx.fxml;
    exports com.example.game2048;
}