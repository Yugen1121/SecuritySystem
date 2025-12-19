package application;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.OutputStream;
import java.io.IOException;

public class Console extends OutputStream {

    private final TextArea console;

    public Console(TextArea console) {
        this.console = console;
    }

    @Override
    public void write(int b) throws IOException {
        Platform.runLater(() ->
            console.appendText(String.valueOf((char) b))
        );
    }
}
