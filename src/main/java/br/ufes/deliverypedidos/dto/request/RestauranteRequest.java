package br.ufes.deliverypedidos.dto.request;

import br.ufes.deliverypedidos.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record RestauranteRequest(
        @NotBlank String nome,
        String telefone,
        String categoria,
        @NotNull @PositiveOrZero BigDecimal taxaEntrega,
        @Valid EnderecoDTO endereco) {
}
