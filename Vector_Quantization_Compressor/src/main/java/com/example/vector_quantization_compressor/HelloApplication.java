package com.example.vector_quantization_compressor;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    private String selectedFilePath = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("LzW Compressor");

        // Create the first scene (full screen before the delay)
        VBox firstScene = new VBox();
        firstScene.setStyle(
                "-fx-background-image: url('logo.jpg'); " +
                        "-fx-background-size: cover; " +
                        "-fx-alignment: center;"
        );
        Label welcomeLabel = new Label("Welcome to LzW Compressor");
        welcomeLabel.setStyle(
                "-fx-font-size: 45px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: white;"
        );
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint(""); // Empty string to hide the exit hint

        firstScene.getChildren().add(welcomeLabel);

        // Create the second scene (normal screen after the delay)
        VBox secondScene = new VBox();
        secondScene.setStyle(
                "-fx-background-image: url('logo.jpg'); " +
                        "-fx-background-size: 1200 675; " +
                        "-fx-alignment: center;"
        );

        Button browseButton = new Button("Browse");
        browseButton.setStyle(
                "-fx-margin: 45 0 0 47%;" +
                        "-fx-font-size: 1.2em;" +
                        "-fx-min-width: 100px;" +
                        "-fx-min-height: 40px;"
        );
        browseButton.setAlignment(Pos.CENTER);
        Label fileLabel = new Label("");
        Label fileLabel2 = new Label("");
        fileLabel.setStyle(
                "-fx-text-fill: white; " +
                        "-fx-translate-x: 20%; " +
                        "-fx-translate-y: -100px; " +
                        "-fx-font-size: 1.2em;"
        );

        browseButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select a File");

            java.io.File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                selectedFilePath = selectedFile.getAbsolutePath();
                fileLabel2.setText(selectedFilePath);
                System.out.println(selectedFilePath);
                fileLabel.setText("File name: "+selectedFile.getName());
            }
        });
        selectedFilePath = fileLabel2.getText();


        HBox buttonsContainer = new HBox();
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.setStyle(
                "-fx-margin: 140 0 60 0;"
        );

        Button compressButton = new Button("Compress");
//        scaleButtonAndLabel(compressButton);
        compressButton.setStyle(
                "-fx-margin: 0 0 0 35%;" +
                        "-fx-min-width: 100px;" +
                        "-fx-min-height: 40px;"
        );
        compressButton.setOnAction(event -> {
            // TODO: Add your compress event here
            try {
                String inputImagePath = "/run/media/phantom/New Volume/University/Data Compression/Assignments/Vector_Quantization_Compressor/src/main/resources/logo.jpg";
                String outputImagePath = "/run/media/phantom/New Volume/University/Data Compression/Assignments/Vector_Quantization_Compressor/src/main/resources/com/example/compressed.png";
                String binaryOutputFilePath = "/run/media/phantom/New Volume/University/Data Compression/Assignments/Vector_Quantization_Compressor/src/main/resources/com/example/compressed_data.bin";
                int codebookSize = 16;
                int maxIterations = 10;

                Compressor compressor = new Compressor();
                compressor.compressT(inputImagePath, outputImagePath, binaryOutputFilePath, codebookSize, maxIterations);

                System.out.println("Compression completed successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        Button decompressButton = new Button("Decompress");
//        scaleButtonAndLabel(decompressButton);
        decompressButton.setStyle(
                "-fx-margin: 0 35 0 0;" +
                        "-fx-min-width: 100px;" +
                        "-fx-min-height: 40px;"
        );
        decompressButton.setOnAction(event -> {
            // TODO: Add your decompress event here

        });

        buttonsContainer.getChildren().addAll(compressButton, decompressButton);

        secondScene.getChildren().addAll(browseButton, fileLabel, buttonsContainer);

        // Create the scenes
        Scene scene1 = new Scene(firstScene, 1200, 675);
        Scene scene2 = new Scene(secondScene, 1200, 675);

        // Transition to the second scene after a delay of 3500 ms
        PauseTransition delay = new PauseTransition(Duration.millis(3500));
        delay.setOnFinished(e -> {
            primaryStage.setFullScreen(false); // Return to normal screen
            primaryStage.setScene(scene2);
            primaryStage.setResizable(false);
        });
        delay.play();

        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    private void scaleButtonAndLabel(Button button) {
        Scale scale = new Scale(1.5, 1.5);
        button.getTransforms().add(scale);
        Label label = (Label) button.getGraphic();
        if (label != null) {
            label.getTransforms().add(scale);
        }
    }
}
