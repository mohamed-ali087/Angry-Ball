package com.bouncingball.angryball;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import java.util.ArrayList;

public class BouncingBall extends Circle {
    public static final double MAX_SPEED = 1000;
    public static final double COLLISION_FACTOR = 0.7;
    public static final double GRAVITY = 0.5;
    private static final double X_FRICTION_FORCE = 0.05;
    private static final double Y_FRICTION_FORCE = 0.05;
    private double mass = 1;
    private double vY = -10;
    private double vX = 10;
    private ArrayList<Pane> boundingPanes;
    private ArrayList<Line> linesObserved;
    private ArrayList<BouncingBall> ballsObserved;
    private double nextX;
    private double nextY;


    //    Timeline animation = new Timeline(
//            new KeyFrame(Duration.millis(50), e -> {
    public void update(){
        // max speed
        if (vX > MAX_SPEED){
            vX = MAX_SPEED;
        }
        if (vY > MAX_SPEED){
            vY = MAX_SPEED;
        }
        // remove small velocity
        if (Math.abs(vX) < 0.01) vX = 0;
        if (Math.abs(vY) < 0.01) vY = 0;
        // next Points
        nextX = this.getCenterX() + vX; // predicted new position
        nextY = this.getCenterY() + vY; // predicted new position

        // ******* checking Pane Boundries Collision *********
        for(Pane pane : boundingPanes){
            double boundX = pane.getWidth();
            double boundY = pane.getHeight();
            double paneXOrigin = pane.localToScene(0, 0).getX();
            double paneYOrigin = pane.localToScene(0, 0).getY();

            boolean xBounceCondition1 = nextX - getRadius() <= paneXOrigin;
            boolean xBounceCondition2 = nextX + getRadius() >= boundX;

            if(xBounceCondition1 || xBounceCondition2){
                vX *= -1 * COLLISION_FACTOR ;
                if(xBounceCondition1){
                    nextX = paneXOrigin + getRadius();
                } else if(xBounceCondition2){
                    nextX = boundX - getRadius();
                }

            }

            boolean yBounceCondition1 = nextY - getRadius() <= paneYOrigin;
            boolean yBounceCondition2 = nextY + getRadius() >= boundY;

            if(yBounceCondition1 || yBounceCondition2) {
                vY *= -1 * COLLISION_FACTOR;
                if(yBounceCondition1){
                    nextY = paneYOrigin + getRadius();
                } else if(yBounceCondition2){
                    nextY = boundY - getRadius();
                    if (Math.abs(vY) < 1) {
                        vY = 0;
                    }
                }

            }
        }

        // ************ Checking Collision for Lines *********
        for(Line line : this.linesObserved){

        }

        // ************ Checking Collision for observed Balls *********
        for (BouncingBall ball : this.ballsObserved) {
            double dx = ball.getCenterX() - this.getCenterX(); // Delta X between two balls centers
            double dy = ball.nextY - this.nextY; // Delta Y between two balls centers
            double dist = Math.sqrt(dx * dx + dy * dy); // abs distance between two balls
            double minDist = this.getRadius() + ball.getRadius(); // minimum allowed distance

            if (dist < minDist && dist > 0) {

                // unit vectors for vector from ball 2 to ball 1 centers
                double nx = dx / dist; // cos theta
                double ny = dy / dist; // sin theta

                // overlapping prevention
                double overlap = (minDist - dist) / 2.0;
                this.nextX -= overlap * nx;
                this.nextY -= overlap * ny;

                // get velocities in the normal axis of collision
                double vA_n = this.vX * nx + this.vY * ny;
                double vB_n = ball.getvX() * nx + ball.getvY() * ny;


                // Only apply if the balls are going to collide // Only resolve if balls are actually approaching (not already separating)
                if (vA_n - vB_n > 0) {

                    double mA = this.mass;
                    double mB = ball.getMass();
                    double e = COLLISION_FACTOR; // coefficient of restitution

                    // Applying collision formulas
                    double totalMass = mA + mB;
                    double newVA_n = ((mA - e * mB) * vA_n + (1 + e) * mB * vB_n) / totalMass;
                    double newVB_n = ((mB - e * mA) * vB_n + (1 + e) * mA * vA_n) / totalMass;

                    double dVA_n = newVA_n - vA_n; // normal speed difference
                    double dVB_n = newVB_n - vB_n; // normal speed difference

                    // apply on X and Y velocities
                    this.vX  += dVA_n * nx;
                    this.vY  += dVA_n * ny;

                    // Apply velocity change to the other ball (necessary for sync) // may double effect
                    ball.setvX(ball.getvX() + dVB_n * nx);
                    ball.setvY(ball.getvY() + dVB_n * ny);
                }
            }
        }

        // Gravity and friction
        if (vY > 0){
            vY += (mass * GRAVITY - Y_FRICTION_FORCE);
        } else if (vY < 0) {
            vY += (mass * GRAVITY + Y_FRICTION_FORCE);
        } else if(vY == 0){
            vY += mass * GRAVITY;
        }

        if (vX > 0){
            vX += (-X_FRICTION_FORCE);
        } else if (vX < 0) {
            vX += (X_FRICTION_FORCE);
        } // else if(dx == 0){}


        this.setCenterX(nextX);
        this.setCenterY(nextY);
    }


    // Getters and Setters
    public double getvY() {
        return vY;
    }

    public void setvY(double vY) {
        this.vY = vY;
    }

    public double getvX() {
        return vX;
    }

    public void setvX(double vX) {
        this.vX = vX;
    }

    public void setMass(double mass){
        this.mass = mass;
    }
    public double getMass(){
        return this.mass;
    }

    public void addBall(BouncingBall ball){
        this.ballsObserved.add(ball);
    }


    // Using an inner Class Builder
    private BouncingBall(Builder builder) { // private Constructor
        this.setRadius(builder.radius);
        this.vX = builder.vX;
        this.vY = builder.vY;
        this.mass = builder.mass;
        this.setFill(builder.color);
        this.boundingPanes = builder.boundingPanes;
        this.linesObserved = builder.linesObserved;
        this.ballsObserved = builder.ballsObserved;
        this.setCenterX(builder.centerX);
        this.setCenterY(builder.centerY);

        this.setOnDragDetected(e -> {this.vX += 10;});

//        animation.setCycleCount(Timeline.INDEFINITE);
//        animation.play();
    }

    public static class Builder { // inner builder class
        private double radius = 20;
        private double vX = 5;
        private double vY = 0;
        private double mass = 1;
        private Color color = Color.RED;
        private final ArrayList<Pane> boundingPanes = new ArrayList<>();
        private final ArrayList<Line> linesObserved = new ArrayList<>();
        private final ArrayList<BouncingBall> ballsObserved = new ArrayList<>();
        private double centerX;
        private double centerY;

        public Builder center(double x, double y){
            this.centerX = x;
            this.centerY = y;
            return this;
        }

        public Builder radius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder velocity(double vX, double vY) {
            this.vX = vX;
            this.vY = vY;
            return this;
        }

        public Builder mass(double mass) {
            this.mass = mass;
            return this;
        }

        public Builder color(Color color){
            this.color = color;
            return this;
        }


        public Builder addPanes(ArrayList<Pane> panes){
            this.boundingPanes.addAll(panes);
            return this;
        }


        public Builder addPane(Pane pane){
            this.boundingPanes.add(pane);
            return this;
        }

        public Builder addLines(ArrayList<Line> lines){
            this.linesObserved.addAll(lines);
            return this;
        }

        public Builder addLine(Line line){
            this.linesObserved.add(line);
            return this;
        }

        public Builder addBalls(ArrayList<BouncingBall> balls){
            this.ballsObserved.addAll(balls);
            return this;
        }

        public Builder addBall(BouncingBall ball){
            this.ballsObserved.add(ball);
            return this;
        }


        public BouncingBall build() {
            return new BouncingBall(this); // use the private Constructor
        }
    }

}
