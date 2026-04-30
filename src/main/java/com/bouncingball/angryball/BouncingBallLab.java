package com.bouncingball.angryball;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;

public class BouncingBallLab extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 500, 600);

        // Ball
        Pane ballPane = new Pane();
//        BouncingBall ball = new BouncingBall(30, root.getWidth(), root.getHeight());
        BouncingBall ball = new BouncingBall.Builder()
                .radius(30)
                .bounds(root.getWidth(), root.getHeight() - 30)
                .velocity(10, -15)
                .mass(1)
                .build();


        ball.setCenterX(root.getWidth()/2);
        ball.setCenterY(root.getHeight()/2);
        ballPane.getChildren().add(ball);

        root.setCenter(ballPane);

        // line in getheight - 30
        Line lineGround = new Line(0, root.getHeight() -30,
                 root.getWidth(), root.getHeight() - 30);
        lineGround.setStroke(Color.BLUE);
        ballPane.getChildren().add(lineGround);

        // buttons
        HBox buttonsPane = new HBox();
        Button btnStart = new Button("start");
        Button btnStop = new Button("stop");
        Button btnReverse = new Button("reverse");
        Button btnPushX = new Button("Push X");
        Button btnPushY = new Button("Push Y");

        btnStart.setOnAction(e -> {
            ball.animation.play();
        });
        btnStop.setOnAction(e -> {
            ball.animation.stop();
        });
        btnReverse.setOnAction(e -> {
            ball.setDx(- ball.getDx());
            ball.setDy(- ball.getDy());
        });
        btnPushX.setOnAction(e -> {
            ball.setDx(ball.getDx() + 10);
        });
        btnPushY.setOnAction(e -> {
            ball.setDy(ball.getDy() - 10);
        });

        buttonsPane.getChildren().addAll(btnStart, btnStop, btnReverse, btnPushX, btnPushY);

        root.setBottom(buttonsPane);

        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Bouncing Ball");
        stage.show();
    }

    static void main() {
        launch();
    }
}
