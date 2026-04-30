package com.bouncingball.angryball;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;


public class BouncingBall extends Circle {
    public static final double COLLISION_FACTOR = 0.7;
    public static final double GRAVITY = 0.5;
    private static final double X_FRICTION_FORCE = 0.05;
    private static final double Y_FRICTION_FORCE = 0.05;
    private double mass = 1;
    private double dy = -10;
    private double dx = 10;
    private double boundX;
    private double boundY;
//    private double heightFromGround;


    public BouncingBall(double radius, double boundX,double boundY){
        this.setRadius(radius);
        this.boundX = boundX;
        this.boundY = boundY - 30;
        // playing animation
        animation.setCycleCount(Timeline.INDEFINITE);
        this.animation.play();
    }

    Timeline animation = new Timeline(
            new KeyFrame(Duration.millis(50), e -> {
//                dy +=(dy > 0) ? (mass * GRAVITY - Y_FRICTION_FORCE) : (mass * GRAVITY + Y_FRICTION_FORCE);
//                dx = (dx > 0) ? dx - X_FRICTION_FORCE : dx + X_FRICTION_FORCE;
                // Gravity and friction
                if (dy > 0){
                    dy += (mass * GRAVITY - Y_FRICTION_FORCE);
                } else if (dy < 0) {
                    dy += (mass * GRAVITY + Y_FRICTION_FORCE);
                } else if(dy == 0){
                    dy += mass * GRAVITY;
                }

                if (dx > 0){
                    dx += (-X_FRICTION_FORCE);
                } else if (dx < 0) {
                    dx += (X_FRICTION_FORCE);
                } // else if(dx == 0){}


                double nextX = this.getCenterX() + dx; // predicted new position
                boolean xBounceCondition1 = nextX - getRadius() <= 0;
                boolean xBounceCondition2 = nextX + getRadius() >= boundX;

                if(xBounceCondition1 || xBounceCondition2){
                    dx *= -1 * COLLISION_FACTOR ;
                    if(xBounceCondition1){
//                        this.setCenterX(0 + getRadius());
                        nextX = 0 + getRadius();
                    } else if(xBounceCondition2){
//                        this.setCenterX(boundX - getRadius());
                        nextX = boundX - getRadius();
                    }

                }

                double nextY = this.getCenterY() + dy; // predicted new position
                boolean yBounceCondition1 = nextY - getRadius() <= 0;
                boolean yBounceCondition2 = nextY + getRadius() >= boundY;

                if(yBounceCondition1 || yBounceCondition2) {
                    dy *= -1 * COLLISION_FACTOR;
                    if(yBounceCondition1){
//                        this.setCenterY(0 + getRadius());
                        nextY = 0 + getRadius();
                    } else if(yBounceCondition2){
//                        this.setCenterY(boundY - getRadius());
                        nextY = boundY - getRadius();
                        if (Math.abs(dy) < 1) {
                            dy = 0;
                        }
                    }

                }
//                this.setCenterX(this.getCenterX() + dx);
//                this.setCenterY(this.getCenterY() + dy);
                this.setCenterX(nextX);
                this.setCenterY(nextY);
//                System.out.println(dy);
            }));


    // Getters and Setters
    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public double getDx() {
        return dx;
    }

    public void setDx(double dx) {
        this.dx = dx;
    }

    public void setMass(double mass){
        this.mass = mass;
    }
    public double getMass(){
        return this.mass;
    }


    // Using an inner Class Builder
    private BouncingBall(Builder builder) { // private Constructor
        this.setRadius(builder.radius);
        this.boundX = builder.boundX;
        this.boundY = builder.boundY;

        this.dx = builder.dx;
        this.dy = builder.dy;
        this.mass = builder.mass;
        this.setFill(builder.color);

        this.setOnDragDetected(e -> {this.dx += 10;});

        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    public static class Builder { // inner builder class
        private double radius = 20;
        private double boundX;
        private double boundY;
        private double dx = 5;
        private double dy = 0;
        private double mass = 1;
        private Color color = Color.RED;

        public Builder bounds(double x, double y) {
            this.boundX = x;
            this.boundY = y;
            return this;
        }

        public Builder radius(double radius) {
            this.radius = radius;
            return this;
        }

        public Builder velocity(double dx, double dy) {
            this.dx = dx;
            this.dy = dy;
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

        public BouncingBall build() {
            return new BouncingBall(this); // use the private Constructor
        }
    }
}

//    private static final double COLLISION_FACTOR = 0.5;
//    private static final double COLLISION_FACTOR = 1;

//Class BallBuilder {
//}


