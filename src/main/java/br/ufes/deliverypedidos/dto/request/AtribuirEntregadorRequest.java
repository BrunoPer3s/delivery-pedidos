package br.ufes.deliverypedidos.dto.request;

import jakarta.validation.constraints.NotNull;

public record AtribuirEntregadorRequest(@NotNull Long entregadorId) {
}
