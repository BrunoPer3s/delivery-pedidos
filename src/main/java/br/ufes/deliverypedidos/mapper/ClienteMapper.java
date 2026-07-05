package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.Cliente;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.response.ClienteResponse;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    private final EnderecoMapper enderecoMapper;

    public ClienteMapper(EnderecoMapper enderecoMapper) {
        this.enderecoMapper = enderecoMapper;
    }

    public Cliente toEntity(ClienteRequest req) {
        return new Cliente(req.nome(), req.email(), req.telefone(), enderecoMapper.toEntity(req.endereco()));
    }

    public ClienteResponse toResponse(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                enderecoMapper.toDTO(cliente.getEndereco()));
    }
}
