package app.main.exception;

import java.io.Serial;

public class ExportException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 8637595432075070369L;

    public ExportException(String message) {
        super(message);
    }
}
