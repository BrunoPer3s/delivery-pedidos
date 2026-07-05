package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Restaurante;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Restaurante> findByUsuarioId(Long usuarioId);
}
