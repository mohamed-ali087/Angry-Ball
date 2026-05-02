package com.bouncingball.angryball;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import java.util.ArrayList;

public class BouncingBall extends Circle {
    private static double refreshRate = 16; // animation update rate // used in other attributes for keeping speed and acceleration same independent on animation rate
    private static double refreshRateFactor = refreshRate / 50;
    public static final double MAX_SPEED = 1000 * refreshRateFactor;
    public static final double COLLISION_FACTOR = 0.9;
    public static final double GRAVITY = 0.5 * refreshRateFactor;
    private static final double X_FRICTION_FORCE = 0.05 * refreshRateFactor;
    private static final double Y_FRICTION_FORCE = 0.05 * refreshRateFactor;
    private double mass = 1;
    private double vY = -10 * refreshRateFactor;
    private double vX = 10 * refreshRateFactor;
    private ArrayList<Pane> boundingPanes;
    private ArrayList<Line> linesObserved;
    private ArrayList<BouncingBall> ballsObserved;
    private double nextX;
    private double nextY;

    private double[] calculateNextPoint(){
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
//        nextX = this.getCenterX() + vX; // predicted new position
//        nextY = this.getCenterY() + vY; // predicted new position
        return new double[] {this.getCenterX() + vX, this.getCenterY() + vY};

    }

    public void applyNextPoint() {
        double[] C = calculateNextPoint();
        this.nextX = C[0];
        this.nextY = C[1];
    }

    public double[] paneCollision(){
        double nextX = this.nextX; // replace class attributes with local variables, apply later.
        double nextY = this.nextY;
        double vX = this.vX;
        double vY = this.vY;
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
        return new double[]{nextX, nextY, vX, vY};
    }

    public void applyPaneCollision(){
        double[] C = paneCollision();
        this.nextX = C[0];
        this.nextY = C[1];
        this.vX    = C[2];
        this.vY    = C[3];
    }

    public double[] lineCollision() {
        double nextX = this.nextX;
        double nextY = this.nextY;
        double vX = this.vX;
        double vY = this.vY;

        for (Line line : this.linesObserved) {
            // AI gen
            double x1 = line.getStartX(), y1 = line.getStartY();
            double x2 = line.getEndX(),   y2 = line.getEndY();

            // Line direction vector
            double lx = x2 - x1;
            double ly = y2 - y1;
            double lineLen = Math.sqrt(lx * lx + ly * ly);
            if (lineLen == 0) continue;

            // Line unit normal (perpendicular) — pointing "up" relative to line direction
            double nx = -ly / lineLen;
            double ny =  lx / lineLen;

            // Vector from line start to ball center
            double bx = nextX - x1;
            double by = nextY - y1;

            // Project ball onto line direction to find closest point (clamped to segment)
            double t = (bx * lx + by * ly) / (lineLen * lineLen);
            t = Math.max(0, Math.min(1, t)); // clamp to [0,1] so it stays on segment

            // Closest point on line segment to ball center
            double closestX = x1 + t * lx;
            double closestY = y1 + t * ly;

            // Distance from ball center to closest point
            double dx = nextX - closestX;
            double dy = nextY - closestY;
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist < getRadius() && dist > 0) {
                // Use actual penetration normal (from closest point toward ball center)
                double penNx = dx / dist;
                double penNy = dy / dist;

                // Push ball out of the line
                double overlap = getRadius() - dist;
                nextX += penNx * overlap;
                nextY += penNy * overlap;

                // Only bounce if ball is moving into the line
                double vDotN = vX * penNx + vY * penNy;
                if (vDotN < 0) {
                    // Reflect velocity over the penetration normal
                    vX -= (1 + COLLISION_FACTOR) * vDotN * penNx;
                    vY -= (1 + COLLISION_FACTOR) * vDotN * penNy;
                }
            }
        }

        return new double[]{nextX, nextY, vX, vY};
    }

    public void applyLineCollision() {
        double[] C = lineCollision();
        this.nextX = C[0];
        this.nextY = C[1];
        this.vX    = C[2];
        this.vY    = C[3];
    }

    public double[] ballCollision(){
        double nextX = this.nextX;
        double nextY = this.nextY;
        double vX = this.vX;
        double vY = this.vY;
        for (BouncingBall ball : this.ballsObserved) {
            double dx = ball.getCenterX() - this.getCenterX(); // Delta X between two balls centers
            double dy = ball.nextY - nextY; // Delta Y between two balls centers
            double dist = Math.sqrt(dx * dx + dy * dy); // abs distance between two balls
            double minDist = this.getRadius() + ball.getRadius(); // minimum allowed distance

            if (dist < minDist && dist > 0) {

                // unit vectors for vector from ball 2 to ball 1 centers
                double nx = dx / dist; // cos theta
                double ny = dy / dist; // sin theta

                // overlapping prevention
                double overlap = (minDist - dist) / 2.0;
                nextX -= overlap * nx;
                nextY -= overlap * ny;

                // get velocities in the normal axis of collision
                double vA_n = vX * nx + vY * ny;
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
                    vX  += dVA_n * nx;
                    vY  += dVA_n * ny;

                    // Apply velocity change to the other ball (necessary for sync) // may double effect
                    ball.setvX(ball.getvX() + dVB_n * nx);
                    ball.setvY(ball.getvY() + dVB_n * ny);
                }
            }
        }

        return new double[]{nextX, nextY, vX, vY};
    }

    public void applyBallCollision(){
            double[] C = ballCollision();
            this.nextX = C[0];
            this.nextY = C[1];
            this.vX    = C[2];
            this.vY    = C[3];
    }

    public void enableCollision(){
        applyPaneCollision();
        applyLineCollision();
        applyBallCollision();
    }

    public void applyPhysics(){
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
    }

    public void update(){
//        // ******* checking Pane Boundries Collision *********
//        paneCollision();
//
//        // ************ Checking Collision for Lines *********
//        lineCollisioin();
//
//        // ************ Checking Collision for observed Balls *********
//        ballCollision();

        // Gravity and friction

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

    public void setRefreshRate(double refreshRate){
        BouncingBall.refreshRate = refreshRate;
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
