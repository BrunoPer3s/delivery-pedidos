package br.ufes.deliverypedidos.dto.response;

import br.ufes.deliverypedidos.dto.EnderecoDTO;
import java.math.BigDecimal;

public record RestauranteResponse(
        Long id,
        String nome,
        String telefone,
        String categoria,
        BigDecimal taxaEntrega,
        EnderecoDTO endereco) {
}
