package br.ufes.deliverypedidos.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.ufes.deliverypedidos.dto.EnderecoDTO;
import br.ufes.deliverypedidos.dto.request.ProdutoRequest;
import br.ufes.deliverypedidos.dto.request.RestauranteRequest;
import br.ufes.deliverypedidos.dto.response.ProdutoResponse;
import br.ufes.deliverypedidos.dto.response.RestauranteResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ProdutoServiceTest {

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private RestauranteService restauranteService;

    private RestauranteResponse novoRestaurante() {
        return restauranteService.criar(new RestauranteRequest("Cantina", "27000000000", "Italiana",
                new BigDecimal("5.00"), new EnderecoDTO("Rua B", "20", "Centro", "Vitória", "29000-000")));
    }

    @Test
    void criaProdutoVinculadoAoRestaurante() {
        RestauranteResponse restaurante = novoRestaurante();
        ProdutoResponse produto = produtoService.criar(
                new ProdutoRequest("Pizza", "Calabresa", new BigDecimal("40.00"), true, restaurante.id()));
        assertEquals(restaurante.id(), produto.restauranteId());
        assertEquals("Pizza", produtoService.buscarPorId(produto.id()).nome());
    }

    @Test
    void criarComRestauranteInexistenteLancaNaoEncontrado() {
        ProdutoRequest req = new ProdutoRequest("X", "y", new BigDecimal("10.00"), true, 999L);
        assertThrows(RecursoNaoEncontradoException.class, () -> produtoService.criar(req));
    }
}
