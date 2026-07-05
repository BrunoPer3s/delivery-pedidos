package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.Produto;
import br.ufes.deliverypedidos.domain.model.Restaurante;
import br.ufes.deliverypedidos.dto.request.ProdutoRequest;
import br.ufes.deliverypedidos.dto.response.ProdutoResponse;
import org.springframework.stereotype.Component;

@Component
public class ProdutoMapper {

    public Produto toEntity(ProdutoRequest req, Restaurante restaurante) {
        return new Produto(req.nome(), req.descricao(), req.preco(), req.disponivel(), restaurante);
    }

    public ProdutoResponse toResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.isDisponivel(),
                produto.getRestaurante().getId());
    }
}
