package br.ufes.deliverypedidos.dto.request;

import br.ufes.deliverypedidos.dto.EnderecoDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ClienteRequest(
        @NotBlank String nome,
        @NotBlank @Email String email,
        String telefone,
        @Valid EnderecoDTO endereco) {
}
