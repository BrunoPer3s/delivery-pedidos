package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.ItemPedido;
import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.dto.response.ItemPedidoResponse;
import br.ufes.deliverypedidos.dto.response.PedidoResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PedidoMapper {

    private final EnderecoMapper enderecoMapper;

    public PedidoMapper(EnderecoMapper enderecoMapper) {
        this.enderecoMapper = enderecoMapper;
    }

    public PedidoResponse toResponse(Pedido pedido) {
        List<ItemPedidoResponse> itens = pedido.getItens().stream()
                .map(this::toItemResponse)
                .toList();
        Long entregadorId = pedido.getEntregador() != null ? pedido.getEntregador().getId() : null;
        return new PedidoResponse(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getRestaurante().getId(),
                entregadorId,
                pedido.getStatus(),
                pedido.getValorTotal(),
                pedido.getDataHora(),
                enderecoMapper.toDTO(pedido.getEnderecoEntrega()),
                itens);
    }

    private ItemPedidoResponse toItemResponse(ItemPedido item) {
        return new ItemPedidoResponse(
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getQuantidade(),
                item.getPrecoUnitario(),
                item.getSubtotal());
    }
}
