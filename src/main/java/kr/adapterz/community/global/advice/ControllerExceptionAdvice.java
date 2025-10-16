package kr.adapterz.community.global.advice;

import kr.adapterz.community.global.dto.ExceptionResponse;
import kr.adapterz.community.global.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerExceptionAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {
        log.warn("기본 예외 발생");
        return ResponseEntity.
                status(e.getErrorCode().getStatus())
                .body(ExceptionResponse.fail(
                        e.getErrorCode().getStatus().value(),
                        e.getErrorCode().getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(
            MethodArgumentNotValidException e) {

        BindingResult bindingResult = e.getBindingResult();
        String message = bindingResult.getFieldErrors().get(0).getDefaultMessage();

        log.warn("Validation 실패: {}", message);

        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", message));
    }

}
