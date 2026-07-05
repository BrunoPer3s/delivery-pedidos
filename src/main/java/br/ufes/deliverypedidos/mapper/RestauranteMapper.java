package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.Restaurante;
import br.ufes.deliverypedidos.dto.request.RestauranteRequest;
import br.ufes.deliverypedidos.dto.response.RestauranteResponse;
import org.springframework.stereotype.Component;

@Component
public class RestauranteMapper {

    private final EnderecoMapper enderecoMapper;

    public RestauranteMapper(EnderecoMapper enderecoMapper) {
        this.enderecoMapper = enderecoMapper;
    }

    public Restaurante toEntity(RestauranteRequest req) {
        return new Restaurante(req.nome(), req.telefone(), req.categoria(), req.taxaEntrega(),
                enderecoMapper.toEntity(req.endereco()));
    }

    public RestauranteResponse toResponse(Restaurante restaurante) {
        return new RestauranteResponse(
                restaurante.getId(),
                restaurante.getNome(),
                restaurante.getTelefone(),
                restaurante.getCategoria(),
                restaurante.getTaxaEntrega(),
                enderecoMapper.toDTO(restaurante.getEndereco()));
    }
}
