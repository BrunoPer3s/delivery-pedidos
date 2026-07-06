package br.ufes.deliverypedidos.controller;

import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.response.ClienteResponse;
import br.ufes.deliverypedidos.service.ClienteService;
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
@RequestMapping("/clientes")
@Tag(name = "Clientes", description = "Perfil do cliente (autocadastro pelo próprio usuário; ADMIN modera)")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria o perfil do cliente vinculado ao usuário logado")
    public ClienteResponse criar(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid ClienteRequest req) {
        return service.criar(req, usuario);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os clientes (somente ADMIN)")
    public List<ClienteResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoCliente(#id, authentication)")
    @Operation(summary = "Busca um cliente por id (dono ou ADMIN)")
    public ClienteResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoCliente(#id, authentication)")
    @Operation(summary = "Atualiza um cliente (dono ou ADMIN)")
    public ClienteResponse atualizar(@PathVariable Long id, @RequestBody @Valid ClienteRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui um cliente (somente ADMIN)")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
