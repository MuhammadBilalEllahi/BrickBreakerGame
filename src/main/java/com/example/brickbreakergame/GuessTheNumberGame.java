package com.example.brickbreakergame;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Random;

public class GuessTheNumberGame extends Application {

    private int targetNumber;
    private int guessCount;

    private Label resultLabel;
    private TextField guessTextField;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Guess the Number Game");

        Label instructionsLabel = new Label("Guess a number between 1 and 100:");
        resultLabel = new Label();
        guessTextField = new TextField();

        Button guessButton = new Button("Guess");
        guessButton.setOnAction(e -> checkGuess());

        VBox root = new VBox(10);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(instructionsLabel, guessTextField, guessButton, resultLabel);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        startNewGame();
    }

    private void startNewGame() {
        Random random = new Random();
        targetNumber = random.nextInt(100) + 1;
        guessCount = 0;
        resultLabel.setText("");
        guessTextField.setText("");
        guessTextField.setEditable(true);
    }

    private void checkGuess() {
        String guessText = guessTextField.getText();
        int guess;

        try {
            guess = Integer.parseInt(guessText);
        } catch (NumberFormatException e) {
            showAlert("Invalid input! Please enter a valid number.");
            guessTextField.setText("");
            return;
        }

        guessCount++;

        if (guess < targetNumber) {
            resultLabel.setText("Too low! Guesses: " + guessCount);
        } else if (guess > targetNumber) {
            resultLabel.setText("Too high! Guesses: " + guessCount);
        } else {
            resultLabel.setText("Correct! You guessed the number in " + guessCount + " guesses.");
            guessTextField.setEditable(false);
            showAlert("Congratulations! You guessed the number in " + guessCount + " guesses.\n\nLet's play again!");
            startNewGame();
        }

        guessTextField.setText("");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Guess the Number");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
