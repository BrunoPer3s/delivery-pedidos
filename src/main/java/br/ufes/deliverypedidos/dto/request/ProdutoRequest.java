package br.ufes.deliverypedidos.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank String nome,
        String descricao,
        @NotNull @Positive BigDecimal preco,
        boolean disponivel,
        @NotNull Long restauranteId) {
}
