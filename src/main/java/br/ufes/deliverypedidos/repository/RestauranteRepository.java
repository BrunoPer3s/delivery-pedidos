package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Restaurante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
}
