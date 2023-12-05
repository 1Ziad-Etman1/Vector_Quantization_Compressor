package com.example.vector_quantization_compressor;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Decompressor {
    private final int codebookSize;

    public Decompressor(int codebookSize) {
        this.codebookSize = codebookSize;
    }

    public void decompress(String compressedPath, String outputPath) throws IOException {
        // Load the compressed data from the binary file
        try (DataInputStream inputStream = new DataInputStream(new FileInputStream(compressedPath))) {
            int[] compressedData = new int[inputStream.available() / 4]; // Assuming each value is 4 bytes (int)

            for (int i = 0; i < compressedData.length; i++) {
                compressedData[i] = inputStream.readInt();
            }

            // Convert 1D array to 2D array
            int[][] flatPixels = new int[compressedData.length / codebookSize][codebookSize];
            for (int i = 0; i < compressedData.length; i += codebookSize) {
                System.arraycopy(compressedData, i, flatPixels[i / codebookSize], 0, codebookSize);
            }

            // Flatten the chunks back to the original shape
            int[] pixels = new int[flatPixels.length * flatPixels[0].length];
            int index = 0;
            for (int[] chunk : flatPixels) {
                System.arraycopy(chunk, 0, pixels, index, codebookSize);
                index += codebookSize;
            }

            // Create a ByteBuffer to convert the int array to a byte array
            ByteBuffer byteBuffer = ByteBuffer.allocate(pixels.length * 4);
            for (int value : pixels) {
                byteBuffer.putInt(value);
            }

            // Write the byte array to the output file
            try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                outputStream.write(byteBuffer.array());
            }
        }
    }
}

