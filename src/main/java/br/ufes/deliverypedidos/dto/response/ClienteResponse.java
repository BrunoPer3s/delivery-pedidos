package br.ufes.deliverypedidos.dto.response;

import br.ufes.deliverypedidos.dto.EnderecoDTO;

public record ClienteResponse(Long id, String nome, String email, String telefone, EnderecoDTO endereco) {
}
