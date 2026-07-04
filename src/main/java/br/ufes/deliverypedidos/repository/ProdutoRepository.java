package br.ufes.deliverypedidos.repository;

import br.ufes.deliverypedidos.domain.model.Produto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findByRestauranteId(Long restauranteId);
}
