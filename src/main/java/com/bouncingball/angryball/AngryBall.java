package com.bouncingball.angryball;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;

public class AngryBall extends Application {

    static final Color[] colorList = {Color.RED, Color.CORAL, Color.PURPLE, Color.GRAY, Color.GREEN, Color.YELLOWGREEN, Color.PINK, Color.DARKBLUE, Color.DARKCYAN, Color.DARKGOLDENROD};

    @Override
    public void start(Stage stage) throws Exception {
        // root Pane
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 500, 600);

        // Ball Pane
        Pane ballPane = new Pane();
        root.setCenter(ballPane);

        //      Lines
        // lines Array
        ArrayList<Line> lines = new ArrayList<>();

        // ground line
        Line lineGround = new Line(0, root.getHeight() -30,
                root.getWidth(), root.getHeight() - 30);
        lineGround.setStroke(Color.BLUE);
        lines.add(lineGround);
        ballPane.getChildren().addAll(lines);

        //      balls
        ArrayList<BouncingBall> balls = new ArrayList<>();

        // buttons
        HBox buttonsPane = new HBox();
        buttonsPane.setAlignment(Pos.CENTER);
        Button btnAddBall = new Button("Add Ball");
        Button btnAddLine = new Button("Add Random Line");

        btnAddBall.setOnAction(e -> {
            BouncingBall newBall= new BouncingBall.Builder()
                    .radius(30)
                    .addPane(ballPane)
                    .velocity(3, -5)
                    .mass(1)
                    .color(colorList[(int) (Math.ceil(Math.random() * 9))])
                    .build();

            for(Line line: lines){
                newBall.addLine(line);
            }
            for (BouncingBall ball: balls){
                newBall.addBall(ball);
                ball.addBall(newBall);
            }

            newBall.enableThrowingOnDrag(true, ballPane);

            balls.add(newBall);


            newBall.setCenterX(root.getWidth()/2);
            newBall.setCenterY(root.getHeight()/2);
            ballPane.getChildren().add(newBall);
        });

        btnAddLine.setOnAction(e -> {
            Line randomLine = new Line(
                    Math.random() * scene.getWidth(),
                    Math.random() * scene.getHeight(),
                    Math.random() * scene.getWidth(),
                    Math.random() * scene.getHeight()
            );
            randomLine.setStroke(colorList[(int) (Math.ceil(Math.random() * 9))]);
            randomLine.setStrokeWidth(2);

            for(BouncingBall ball : balls){
                ball.addLine(randomLine);
            }
            lines.add(randomLine);
            ballPane.getChildren().add(randomLine);
        });

        buttonsPane.getChildren().addAll(btnAddBall, btnAddLine);
        root.setBottom(buttonsPane);

        // animation
        Timeline animation = new Timeline(
                new KeyFrame(Duration.millis(Physics.refreshRate), e -> {
                    for (BouncingBall ball : balls){
                        ball.easyCalc();
                        ball.update();
                        if(Math.abs(ball.getvX()) < 0.01 && Math.abs(ball.getvY()) < 0.01){
                            ballPane.getChildren().removeIf(node -> node instanceof Polyline);
                        }
                    }
                })
        );
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();


        stage.setScene(scene);
        stage.setTitle("Angry Ball");
        stage.show();
    }

    static void main() {
        launch();
    }
}
