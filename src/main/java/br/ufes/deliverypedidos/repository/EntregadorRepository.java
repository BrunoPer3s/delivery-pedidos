package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Entregador;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntregadorRepository extends JpaRepository<Entregador, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Entregador> findByUsuarioId(Long usuarioId);
}
