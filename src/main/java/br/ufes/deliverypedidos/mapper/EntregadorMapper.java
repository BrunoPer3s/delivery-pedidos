package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.Entregador;
import br.ufes.deliverypedidos.dto.request.EntregadorRequest;
import br.ufes.deliverypedidos.dto.response.EntregadorResponse;
import org.springframework.stereotype.Component;

@Component
public class EntregadorMapper {

    public Entregador toEntity(EntregadorRequest req) {
        return new Entregador(req.nome(), req.telefone(), req.placaVeiculo());
    }

    public EntregadorResponse toResponse(Entregador entregador) {
        return new EntregadorResponse(
                entregador.getId(),
                entregador.getNome(),
                entregador.getTelefone(),
                entregador.getPlacaVeiculo());
    }
}
