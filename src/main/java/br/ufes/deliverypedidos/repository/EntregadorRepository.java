package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Entregador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntregadorRepository extends JpaRepository<Entregador, Long> {
}
