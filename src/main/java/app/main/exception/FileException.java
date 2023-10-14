package app.main.exception;

import java.io.Serial;

public class FileException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1673052151925976798L;

    public FileException(String message) {
        super(message);
    }
}
