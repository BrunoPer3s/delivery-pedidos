package br.ufes.deliverypedidos.dto.response;

import java.math.BigDecimal;

public record ProdutoResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        boolean disponivel,
        Long restauranteId) {
}
