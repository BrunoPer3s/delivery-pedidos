package br.ufes.deliverypedidos.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(TransicaoInvalidaException.class)
    public ResponseEntity<ErroResposta> tratarTransicaoInvalida(TransicaoInvalidaException ex) {
        return montar(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(RegraDeNegocioException.class)
    public ResponseEntity<ErroResposta> tratarRegraDeNegocio(RegraDeNegocioException ex) {
        return montar(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            campos.put(erro.getField(), erro.getDefaultMessage());
        }
        ErroResposta corpo = new ErroResposta(
                HttpStatus.BAD_REQUEST.value(), "Dados inválidos", campos, LocalDateTime.now());
        return ResponseEntity.badRequest().body(corpo);
    }

    private ResponseEntity<ErroResposta> montar(HttpStatus status, String mensagem) {
        ErroResposta corpo = new ErroResposta(
                status.value(), status.getReasonPhrase(), mensagem, LocalDateTime.now());
        return ResponseEntity.status(status).body(corpo);
    }
}
