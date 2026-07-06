package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Pedido;
import br.ufes.deliverypedidos.domain.model.StatusPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    Page<Pedido> findByStatus(StatusPedido status, Pageable pageable);

    Page<Pedido> findByClienteId(Long clienteId, Pageable pageable);

    Page<Pedido> findByRestauranteId(Long restauranteId, Pageable pageable);

    Page<Pedido> findByEntregadorId(Long entregadorId, Pageable pageable);
}
