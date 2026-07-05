package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Cliente;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByEmail(String email);

    boolean existsByUsuarioId(Long usuarioId);

    Optional<Cliente> findByUsuarioId(Long usuarioId);
}
