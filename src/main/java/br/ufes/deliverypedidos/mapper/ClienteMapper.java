package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.Cliente;
import br.ufes.deliverypedidos.domain.model.Endereco;
import br.ufes.deliverypedidos.dto.EnderecoDTO;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.response.ClienteResponse;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequest req) {
        return new Cliente(req.nome(), req.email(), req.telefone(), toEndereco(req.endereco()));
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                toEnderecoDTO(cliente.getEndereco()));
    }

    public Endereco toEndereco(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Endereco(dto.logradouro(), dto.numero(), dto.bairro(), dto.cidade(), dto.cep());
    }

    private EnderecoDTO toEnderecoDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoDTO(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getCep());
    }
}
