import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

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

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        gc = canvas.getGraphicsContext2D();
        bricks = new LinkedList<>();
        paddle = new Paddle((WIDTH - PADDLE_WIDTH) / 2, HEIGHT - PADDLE_HEIGHT - 10, PADDLE_WIDTH, PADDLE_HEIGHT);
        ball = new Ball(WIDTH / 2, HEIGHT / 2, BALL_RADIUS);

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
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                update();
                render();
            }
        }).start();
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
        if (ball.getY() >= HEIGHT) {
            System.out.println("Game Over");
            System.exit(0);
        }
    }

    private class Paddle {
        private double x;
        private double y;
        private double width;
        private double height;

        public Paddle(double x, double y, double width, double height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void moveLeft() {
            if (x > 0) {
                x -= 5;
            }
        }

        public void moveRight() {
            if (x + width < WIDTH) {
                x += 5;
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
