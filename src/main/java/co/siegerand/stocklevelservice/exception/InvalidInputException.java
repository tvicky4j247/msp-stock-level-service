package co.siegerand.stocklevelservice.exception;

public class InvalidInputException extends RuntimeException {
    
    public InvalidInputException() {
        super();
    }

    public InvalidInputException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidInputException(Throwable err) {
        super(err);
    }
}
