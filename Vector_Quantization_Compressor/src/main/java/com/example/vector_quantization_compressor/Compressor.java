package com.example.vector_quantization_compressor;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Random;
import javax.imageio.ImageIO;

public class Compressor {
    private static final int NEIGHBORHOOD_SIZE = 5; // Size of the subset used to initialize the codebook

    public void kclusterBigT(int[] pixels, int codebookSize, int maxIterations) {
        int[] codebook = initializeCodebook(pixels, codebookSize);

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            int[] clusterSize = new int[codebookSize];
            int[] clusterSum = new int[codebookSize];

            // Assign pixels to the closest cluster
            for (int pixel : pixels) {
                int closestColorIndex = findColor(pixel, codebook);
                clusterSize[closestColorIndex]++;
                clusterSum[closestColorIndex] += pixel;
            }

            // Update the codebook
            for (int i = 0; i < codebookSize; i++) {
                if (clusterSize[i] != 0) {
                    codebook[i] = clusterSum[i] / clusterSize[i];
                }
            }
        }
    }

    public int findColor(int pixelValue, int[] codebook) {
        int minDistance = Integer.MAX_VALUE;
        int closestColorIndex = -1;

        for (int i = 0; i < codebook.length; i++) {
            int distance = Math.abs(pixelValue - codebook[i]);
            if (distance < minDistance) {
                minDistance = distance;
                closestColorIndex = i;
            }
        }

        return closestColorIndex;
    }

    public void compressT(String inputImagePath, String outputImagePath, String binaryOutputFilePath, int codebookSize, int maxIterations) throws IOException {
        // Convert image to 2D array
        int[][] pixels = ImageUtil.convertImageTo2DArray(inputImagePath);

        // Flatten the 2D array to a 1D array
        int[] flattenedPixels = Arrays.stream(pixels).flatMapToInt(Arrays::stream).toArray();

        // Perform k-means clustering
        kclusterBigT(flattenedPixels, codebookSize, maxIterations);

        // Generate compressed image and binary file
        BufferedImage compressedImage = generateCompressedImage(pixels, findNearestColors(flattenedPixels, codebookSize));
        saveCompressedImage(compressedImage, outputImagePath);

        generateBinaryFile(pixels, codebookSize, binaryOutputFilePath);
    }

    private int[] initializeCodebook(int[] pixels, int codebookSize) {
        Random random = new Random();
        int[] subset = new int[NEIGHBORHOOD_SIZE];
        int[] codebook = new int[codebookSize];

        for (int i = 0; i < NEIGHBORHOOD_SIZE; i++) {
            subset[i] = pixels[random.nextInt(pixels.length)];
        }

        // Sort the subset
        Arrays.sort(subset);

        // Initialize codebook by selecting equidistant colors from the sorted subset
        for (int i = 0; i < codebookSize; i++) {
            int index = i * (NEIGHBORHOOD_SIZE - 1) / (codebookSize - 1);
            codebook[i] = subset[index];
        }

        return codebook;
    }

    private int[][] findNearestColors(int[] pixels, int codebookSize) {
        int[][] nearestColors = new int[pixels.length][codebookSize];

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            for (int j = 0; j < codebookSize; j++) {
                nearestColors[i][j] = findColor(pixel, new int[]{codebookSize});
            }
        }

        return nearestColors;
    }

    private BufferedImage generateCompressedImage(int[][] pixels, int[][] nearestColors) {
        BufferedImage compressedImage = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                compressedImage.setRGB(j, i, nearestColors[i][j]);
            }
        }

        return compressedImage;
    }

    private void saveCompressedImage(BufferedImage compressedImage, String outputImagePath) throws IOException {
        File outputImageFile = new File(outputImagePath);
        ImageIO.write(compressedImage, "png", outputImageFile);
    }

    private void generateBinaryFile(int[][] pixels, int codebookSize, String binaryOutputFilePath) throws IOException {
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream(binaryOutputFilePath))) {
            for (int i = 0; i < pixels.length; i++) {
                for (int j = 0; j < pixels[0].length; j++) {
                    outputStream.writeInt(findColor(pixels[i][j], new int[]{codebookSize}));
                }
            }
        }
    }
}
