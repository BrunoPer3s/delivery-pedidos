package br.ufes.deliverypedidos.dto.response;

import br.ufes.deliverypedidos.domain.model.StatusPedido;
import java.time.LocalDateTime;

public record EventoRastreamentoResponse(StatusPedido status, String descricao, LocalDateTime dataHora) {
}
