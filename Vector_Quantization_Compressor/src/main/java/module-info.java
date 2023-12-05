module com.example.vector_quantization_compressor {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.vector_quantization_compressor to javafx.fxml;
    exports com.example.vector_quantization_compressor;
}