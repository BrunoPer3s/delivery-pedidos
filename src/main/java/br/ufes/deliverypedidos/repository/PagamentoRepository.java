package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Pagamento;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    boolean existsByPedidoId(Long pedidoId);

    Optional<Pagamento> findByPedidoId(Long pedidoId);
}
