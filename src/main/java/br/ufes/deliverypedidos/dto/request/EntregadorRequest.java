package br.ufes.deliverypedidos.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EntregadorRequest(
        @NotBlank String nome,
        String telefone,
        String placaVeiculo) {
}
