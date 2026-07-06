package br.ufes.deliverypedidos.controller;

import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.request.RestauranteRequest;
import br.ufes.deliverypedidos.dto.response.RestauranteResponse;
import br.ufes.deliverypedidos.service.RestauranteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restaurantes")
@Tag(name = "Restaurantes", description = "Cadastro de restaurantes e catálogo (leitura livre para autenticados)")
public class RestauranteController {

    private final RestauranteService service;

    public RestauranteController(RestauranteService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('RESTAURANTE')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria o perfil do restaurante vinculado ao usuário logado")
    public RestauranteResponse criar(@AuthenticationPrincipal Usuario usuario,
                                     @RequestBody @Valid RestauranteRequest req) {
        return service.criar(req, usuario);
    }

    // Catálogo de restaurantes: qualquer usuário autenticado pode consultar.
    @GetMapping
    @Operation(summary = "Lista o catálogo de restaurantes")
    public List<RestauranteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um restaurante por id")
    public RestauranteResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoRestaurante(#id, authentication)")
    @Operation(summary = "Atualiza um restaurante (dono ou ADMIN)")
    public RestauranteResponse atualizar(@PathVariable Long id, @RequestBody @Valid RestauranteRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui um restaurante (somente ADMIN)")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
