package co.siegerand.stocklevelservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import co.siegerand.stocklevelservice.util.other.HttpErrorInfo;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(InvalidInputException.class)
    public @ResponseBody HttpErrorInfo handleInvalidInputException(ServerHttpRequest request, InvalidInputException ex) {
        return generateErrorInfo(HttpStatus.UNPROCESSABLE_ENTITY, request, ex);
    }


    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public @ResponseBody HttpErrorInfo handleNotFoundException(ServerHttpRequest request, NotFoundException ex) {
        return generateErrorInfo(HttpStatus.NOT_FOUND, request, ex);
    }

    // UTIL METHODS
    private HttpErrorInfo generateErrorInfo(HttpStatus status, ServerHttpRequest request, RuntimeException ex) {
        String path = request.getURI().toString();
        String message = ex.getMessage();

        logger.info("Returning HTTP error status code: {} for path: {}, message: {}", status.value(), path, message);
        return new HttpErrorInfo(status, path, message);
    }

}
