package br.ufes.deliverypedidos.service;

import br.ufes.deliverypedidos.dto.response.EventoRastreamentoResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.repository.EventoRastreamentoRepository;
import br.ufes.deliverypedidos.repository.PedidoRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RastreamentoService {

    private final EventoRastreamentoRepository repository;
    private final PedidoRepository pedidoRepository;

    public RastreamentoService(EventoRastreamentoRepository repository, PedidoRepository pedidoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional(readOnly = true)
    public List<EventoRastreamentoResponse> doPedido(Long pedidoId) {
        if (!pedidoRepository.existsById(pedidoId)) {
            throw new RecursoNaoEncontradoException("Pedido não encontrado: " + pedidoId);
        }
        return repository.findByPedidoIdOrderByDataHoraAsc(pedidoId).stream()
                .map(e -> new EventoRastreamentoResponse(e.getStatus(), e.getDescricao(), e.getDataHora()))
                .toList();
    }
}
