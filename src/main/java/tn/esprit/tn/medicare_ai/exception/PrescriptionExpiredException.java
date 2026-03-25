package tn.esprit.tn.medicare_ai.exception;

public class PrescriptionExpiredException extends RuntimeException {
    public PrescriptionExpiredException(String message) {
        super(message);
    }
}

