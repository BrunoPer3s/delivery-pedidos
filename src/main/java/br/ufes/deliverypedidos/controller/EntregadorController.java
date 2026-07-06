package br.ufes.deliverypedidos.controller;

import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.request.EntregadorRequest;
import br.ufes.deliverypedidos.dto.response.EntregadorResponse;
import br.ufes.deliverypedidos.service.EntregadorService;
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
@RequestMapping("/entregadores")
@Tag(name = "Entregadores", description = "Cadastro de entregadores (autocadastro; ADMIN/RESTAURANTE consultam)")
public class EntregadorController {

    private final EntregadorService service;

    public EntregadorController(EntregadorService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ENTREGADOR')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria o perfil do entregador vinculado ao usuário logado")
    public EntregadorResponse criar(@AuthenticationPrincipal Usuario usuario,
                                    @RequestBody @Valid EntregadorRequest req) {
        return service.criar(req, usuario);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE')")
    @Operation(summary = "Lista os entregadores (ADMIN ou RESTAURANTE)")
    public List<EntregadorResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RESTAURANTE') or @posse.donoDoEntregador(#id, authentication)")
    @Operation(summary = "Busca um entregador por id (ADMIN, RESTAURANTE ou o próprio)")
    public EntregadorResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoEntregador(#id, authentication)")
    @Operation(summary = "Atualiza um entregador (o próprio ou ADMIN)")
    public EntregadorResponse atualizar(@PathVariable Long id, @RequestBody @Valid EntregadorRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui um entregador (somente ADMIN)")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
