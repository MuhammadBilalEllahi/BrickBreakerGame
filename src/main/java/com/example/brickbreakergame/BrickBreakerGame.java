package com.example.brickbreakergame;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.LinkedList;

public class BrickBreakerGame extends Application {

    private static final int WIDTH = 600;
    private static final int HEIGHT = 400;
    private static final int PADDLE_WIDTH = 100;
    private static final int PADDLE_HEIGHT = 10;
    private static final int BRICK_WIDTH = 60;
    private static final int BRICK_HEIGHT = 20;
    private static final int BALL_RADIUS = 10;
    private static final int BALL_SPEED = 2;

    private Canvas canvas;
    private GraphicsContext gc;
    private LinkedList<Brick> bricks;
    private Paddle paddle;
    private Ball ball;
    private int score;

    private boolean gameOver = false;
    private boolean gameOverDialogShowing = false;


//    Thread t = new Thread(() -> {
//        while (true) {
//            try {
//                Thread.sleep(10);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            update();
//            render();
//        }
//    });
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        bricks = new LinkedList<>();
        paddle = new Paddle((double) (WIDTH - PADDLE_WIDTH) / 2 , HEIGHT - PADDLE_HEIGHT - 10, PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Ball((double) WIDTH / 2, (double) HEIGHT / 1.2, BALL_RADIUS);

        // Generate bricks
        generateBricks();

        VBox root = new VBox(canvas);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                paddle.moveLeft();
            } else if (event.getCode() == KeyCode.RIGHT) {
                paddle.moveRight();
            }
        });

        primaryStage.setTitle("Brick Breaker Game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Game loop

//        t.start();
        startGameLoop();




    }


    private void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gameOver) {
                    update();
                    render();
                }
            }
        };

        gameLoop.start();
    }


    private void showGameOverDialog() {
        gameOverDialogShowing = true;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("Game Over!");
        alert.setContentText("Do you want to restart the game?");

        ButtonType restartButton = new ButtonType("Restart");
        ButtonType exitButton = new ButtonType("Exit");

        alert.getButtonTypes().setAll(restartButton, exitButton);

        alert.setOnHidden(event -> {
            gameOverDialogShowing = false;
            if (alert.getResult() == restartButton) {
                restartGame();
            } else if (alert.getResult() == exitButton) {
                exitGame();
            }
        });

        alert.show();
    }


    private void restartGame() {
        // Reset game state, including paddle, ball, and bricks
        paddle = new Paddle((double) (WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_HEIGHT - 10, PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Ball((double) WIDTH / 2, (double) HEIGHT / 1.2, BALL_RADIUS);
        bricks.clear(); // Clear existing bricks
        generateBricks(); // Generate new bricks
        score = 0;

        // Reset the game over flag to false
        gameOver = false;
    }
    private void exitGame() {
        // Close the application by closing the stage
        primaryStage.close();
    }

    private void generateBricks() {
        int rows = 4;
        int columns = 10;
        int gap = 10;
        int startX = (WIDTH - (BRICK_WIDTH + gap) * columns + gap) / 2;
        int startY = 50;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                Brick brick = new Brick(startX + (BRICK_WIDTH + gap) * col, startY + (BRICK_HEIGHT + gap) * row,
                        BRICK_WIDTH, BRICK_HEIGHT);
                bricks.add(brick);
            }
        }
    }

    private void update() {
        ball.move();
        checkCollisions();
    }

    private void render() {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.WHITE);
        gc.fillText("Score: " + score, 10, 20);

        paddle.draw(gc);
        ball.draw(gc);

        for (Brick brick : bricks) {
            brick.draw(gc);
        }
    }

    private void checkCollisions() {
        // Check paddle collision
        if (ball.intersects(paddle)) {
            ball.reverseY();
        }

        // Check brick collisions
        LinkedList<Brick> toRemove = new LinkedList<>();
        for (Brick brick : bricks) {
            if (ball.intersects(brick)) {
                toRemove.add(brick);
                ball.reverseY();
                score++;
            }
        }
        bricks.removeAll(toRemove);

        // Check wall collisions
        if (ball.getX() <= 0 || ball.getX() >= WIDTH - BALL_RADIUS) {
            ball.reverseX();
        }
        if (ball.getY() <= 0) {
            ball.reverseY();
        }

        // Check game over
        if (ball.getY() >= HEIGHT && !gameOver) {
            System.out.println("Game Over");
            gameOver = true; // Set the game over flag

            // Show the game over dialog if it's not already showing
            if (!gameOverDialogShowing) {
                showGameOverDialog();
            }
        }
    }
    private class Paddle {
        private double x;
        private double y;
        private final double width;
        private final double height;

        public Paddle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void moveLeft() {
            if (x > 0) {
                x -= 20;
            }
        }

        public void moveRight() {
            if (x + width < WIDTH) {
                x += 20;
            }
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(Color.WHITE);
            gc.fillRect(x, y, width, height);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }

    private class Ball {
        private double x;
        private double y;
        private double radius;
        private double dx;
        private double dy;

        public Ball(double x, double y, double radius) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            dx = BALL_SPEED;
            dy = BALL_SPEED;
        }

        public void move() {
            x += dx;
            y += dy;
        }

        public void reverseX() {
            dx = -dx;
        }

        public void reverseY() {
            dy = -dy;
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(Color.WHITE);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getRadius() {
            return radius;
        }

        public boolean intersects(Brick brick) {
            return x + radius >= brick.getX() && x - radius <= brick.getX() + brick.getWidth()
                    && y + radius >= brick.getY() && y - radius <= brick.getY() + brick.getHeight();
        }

        public boolean intersects(Paddle paddle) {
            return x + radius >= paddle.getX() && x - radius <= paddle.getX() + paddle.getWidth()
                    && y + radius >= paddle.getY() && y - radius <= paddle.getY() + paddle.getHeight();
        }
    }

    private class Brick {
        private double x;
        private double y;
        private double width;
        private double height;

        public Brick(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(Color.YELLOW);
            gc.fillRect(x, y, width, height);
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
