package br.ufes.deliverypedidos.controller;

import br.ufes.deliverypedidos.dto.request.ProdutoRequest;
import br.ufes.deliverypedidos.dto.response.ProdutoResponse;
import br.ufes.deliverypedidos.service.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/produtos")
@Tag(name = "Produtos", description = "Cardápio dos restaurantes (o restaurante dono gerencia seus produtos)")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANTE') and @posse.donoDoRestaurante(#req.restauranteId(), authentication)")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adiciona um produto ao cardápio do próprio restaurante")
    public ProdutoResponse criar(@RequestBody @Valid ProdutoRequest req) {
        return service.criar(req);
    }

    // Cardápio de um restaurante: qualquer usuário autenticado pode consultar.
    @GetMapping
    @Operation(summary = "Lista o cardápio de um restaurante (via ?restauranteId=)")
    public List<ProdutoResponse> listarPorRestaurante(@RequestParam Long restauranteId) {
        return service.listarPorRestaurante(restauranteId);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um produto por id")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoProduto(#id, authentication)")
    @Operation(summary = "Atualiza um produto (restaurante dono ou ADMIN)")
    public ProdutoResponse atualizar(@PathVariable Long id, @RequestBody @Valid ProdutoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoProduto(#id, authentication)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui um produto (restaurante dono ou ADMIN)")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
