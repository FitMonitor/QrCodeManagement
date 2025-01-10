package deti.fitmonitor.qrcode.exceptions;

import java.security.NoSuchAlgorithmException;

public class SecureRandomGenerationException extends RuntimeException {
    public SecureRandomGenerationException(String message, NoSuchAlgorithmException cause) {
        super(message, cause);
    }
}