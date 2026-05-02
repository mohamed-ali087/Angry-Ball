// this is a testing file
package com.bouncingball.angryball;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BouncingBallLab extends Application {

    static final Color[] colorList = {Color.RED, Color.CORAL, Color.PURPLE, Color.GRAY, Color.GREEN, Color.YELLOWGREEN};

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 500, 600);

        // Ball
        Pane ballPane = new Pane();
//        BouncingBall ball = new BouncingBall(30, root.getWidth(), root.getHeight());
        BouncingBall ball = new BouncingBall.Builder()
                .radius(30)
                .addPane(ballPane)
                .velocity(5, -5)
                .mass(1)
                .build();


        ball.setCenterX(root.getWidth()/2);
        ball.setCenterY(root.getHeight()/2);
        ballPane.getChildren().add(ball);

        BouncingBall ball2 = new BouncingBall.Builder()
                .radius(30)
                .addPane(ballPane)
                .addBall(ball)
                .velocity(3, -5)
                .mass(1)
                .color(Color.PURPLE)
                .build();


        ball.addBall(ball2);

        ball2.setCenterX(root.getWidth()/2);
        ball2.setCenterY(root.getHeight()/2);
        ballPane.getChildren().add(ball2);

        root.setCenter(ballPane);

        // line in getheight - 10
        Line lineGround = new Line(0, root.getHeight() -30,
                root.getWidth(), root.getHeight() - 30);
        lineGround.setStroke(Color.BLUE);
        ballPane.getChildren().add(lineGround);
        ball.addLine(lineGround);
        ball2.addLine(lineGround);

        // another line
        Line randomLine = new Line(
                Math.random() * scene.getWidth(),
                Math.random() * scene.getHeight(),
                Math.random() * scene.getWidth(),
                Math.random() * scene.getHeight()
        );
        randomLine.setStroke(Color.BLUE);
        randomLine.setStrokeWidth(2);
        ballPane.getChildren().add(randomLine);
        ball.addLine(randomLine);
        ball2.addLine(randomLine);

        // buttons
        HBox buttonsPane = new HBox();
        Button btnStart = new Button("start");
        Button btnStop = new Button("stop");
        Button btnReverse = new Button("reverse");
        Button btnPushX = new Button("Push X");
        Button btnPushY = new Button("Push Y");

        ball.enableThrowingOnDrag();
        ball2.enableThrowingOnDrag();
        // animation
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(16), e -> {
                    ball.easyCalc();
                    ball2.easyCalc();
                    ball.update();
                    ball2.update();
                })
        );
        animation.setCycleCount(Timeline.INDEFINITE);

        btnStart.setOnAction(e -> {
            animation.play();
        });
        btnStop.setOnAction(e -> {
            animation.stop();
        });
        btnReverse.setOnAction(e -> {
            ball.setvX(- ball.getvX());
            ball.setvY(- ball.getvY());
        });
        btnPushX.setOnAction(e -> {
            ball.setvX(ball.getvX() + 10);
        });
        btnPushY.setOnAction(e -> {
            ball.setvY(ball.getvY() - 10);
        });

        buttonsPane.getChildren().addAll(btnStart, btnStop, btnReverse, btnPushX, btnPushY);

        root.setBottom(buttonsPane);

//        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Bouncing Ball");
        stage.show();
    }

    static void main() {
        launch();
    }
}