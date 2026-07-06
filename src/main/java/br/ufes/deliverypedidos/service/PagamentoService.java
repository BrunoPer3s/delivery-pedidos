package br.ufes.deliverypedidos.service;

import br.ufes.deliverypedidos.domain.model.FormaPagamento;
import br.ufes.deliverypedidos.domain.model.Pagamento;
import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.domain.strategy.EstrategiaPagamento;
import br.ufes.deliverypedidos.domain.strategy.ResultadoPagamento;
import br.ufes.deliverypedidos.dto.response.PagamentoResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.exception.RegraDeNegocioException;
import br.ufes.deliverypedidos.mapper.PagamentoMapper;
import br.ufes.deliverypedidos.repository.PagamentoRepository;
import br.ufes.deliverypedidos.repository.PedidoRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Contexto do padrão Strategy: mantém as estratégias de pagamento indexadas por
 * forma (coletadas via injeção do Spring) e delega o processamento à escolhida.
 */
@Service
public class PagamentoService {

    private final PagamentoRepository repository;
    private final PedidoRepository pedidoRepository;
    private final PagamentoMapper mapper;
    private final Map<FormaPagamento, EstrategiaPagamento> estrategias = new EnumMap<>(FormaPagamento.class);

    public PagamentoService(PagamentoRepository repository, PedidoRepository pedidoRepository,
                            PagamentoMapper mapper, List<EstrategiaPagamento> estrategias) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.mapper = mapper;
        estrategias.forEach(estrategia -> this.estrategias.put(estrategia.getForma(), estrategia));
    }

    @Transactional
    public PagamentoResponse pagar(Long pedidoId, FormaPagamento forma) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido não encontrado: " + pedidoId));
        if (pedido.getStatus() == StatusPedido.CANCELADO) {
            throw new RegraDeNegocioException("Não é possível pagar um pedido cancelado");
        }
        if (repository.existsByPedidoId(pedidoId)) {
            throw new RegraDeNegocioException("O pedido " + pedidoId + " já foi pago");
        }
        EstrategiaPagamento estrategia = estrategias.get(forma);
        if (estrategia == null) {
            throw new RegraDeNegocioException("Forma de pagamento não suportada: " + forma);
        }
        ResultadoPagamento resultado = estrategia.pagar(pedido.getValorTotal());
        Pagamento pagamento = new Pagamento(pedido, forma, resultado.valorFinal(), resultado.detalhe());
        return mapper.toResponse(repository.save(pagamento));
    }

    @Transactional(readOnly = true)
    public PagamentoResponse buscarPorPedido(Long pedidoId) {
        return repository.findByPedidoId(pedidoId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Pagamento não encontrado para o pedido: " + pedidoId));
    }
}
