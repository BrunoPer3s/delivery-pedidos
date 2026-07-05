package br.ufes.deliverypedidos.exception;

import java.time.LocalDateTime;

public record ErroResposta(int status, String erro, Object mensagem, LocalDateTime timestamp) {
}
