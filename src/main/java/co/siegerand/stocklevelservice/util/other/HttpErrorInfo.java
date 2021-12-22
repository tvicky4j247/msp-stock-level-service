package co.siegerand.stocklevelservice.util.other;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HttpErrorInfo {

    private final HttpStatus httpStatus;
    private final String path;
    private final String message;
    
}
