package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.event.EventoRastreamento;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRastreamentoRepository extends JpaRepository<EventoRastreamento, Long> {

    List<EventoRastreamento> findByPedidoIdOrderByDataHoraAsc(Long pedidoId);
}
