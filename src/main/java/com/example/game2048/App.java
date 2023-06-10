package com.example.game2048;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Main.fxml"));
        MainController.KeyboardListener keyboardListener = new MainController.KeyboardListener();
        Scene scene = new Scene(fxmlLoader.load(), 800, 955);
        scene.addEventHandler(KeyEvent.KEY_PRESSED, keyboardListener); // 注册按键事件监听器
        stage.setTitle("2048!");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/game.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}