package br.ufes.deliverypedidos.controller;

import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.request.AtribuirEntregadorRequest;
import br.ufes.deliverypedidos.dto.request.PedidoRequest;
import br.ufes.deliverypedidos.dto.response.PedidoResponse;
import br.ufes.deliverypedidos.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid PedidoRequest req) {
        return service.criar(req, usuario);
    }

    // Visão geral do moderador: todos os pedidos, com filtro opcional por status.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Page<PedidoResponse> listar(@RequestParam(required = false) StatusPedido status,
                                       @PageableDefault(size = 20, sort = "dataHora",
                                               direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(status, pageable);
    }

    // Pedidos do próprio ator logado (cliente, restaurante ou entregador).
    @GetMapping("/meus")
    public Page<PedidoResponse> listarMeus(@AuthenticationPrincipal Usuario usuario,
                                           @PageableDefault(size = 20, sort = "dataHora",
                                                   direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarDoUsuario(usuario, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.podeVerPedido(#id, authentication)")
    public PedidoResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    public PedidoResponse confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @PatchMapping("/{id}/iniciar-preparo")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    public PedidoResponse iniciarPreparo(@PathVariable Long id) {
        return service.iniciarPreparo(id);
    }

    @PatchMapping("/{id}/marcar-pronto")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    public PedidoResponse marcarPronto(@PathVariable Long id) {
        return service.marcarPronto(id);
    }

    @PutMapping("/{id}/entregador")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    public PedidoResponse atribuirEntregador(@PathVariable Long id,
                                             @RequestBody @Valid AtribuirEntregadorRequest req) {
        return service.atribuirEntregador(id, req.entregadorId());
    }

    @PatchMapping("/{id}/despachar")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    public PedidoResponse despachar(@PathVariable Long id) {
        return service.despachar(id);
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasRole('ADMIN') or @posse.entregadorDoPedido(#id, authentication)")
    public PedidoResponse entregar(@PathVariable Long id) {
        return service.entregar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoPedido(#id, authentication) "
            + "or @posse.restauranteDoPedido(#id, authentication)")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
