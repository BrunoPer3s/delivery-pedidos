package br.ufes.deliverypedidos.controller;

import br.ufes.deliverypedidos.domain.model.StatusPedido;
import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.request.AtribuirEntregadorRequest;
import br.ufes.deliverypedidos.dto.request.PedidoRequest;
import br.ufes.deliverypedidos.dto.response.EventoRastreamentoResponse;
import br.ufes.deliverypedidos.dto.response.PedidoResponse;
import br.ufes.deliverypedidos.service.PedidoService;
import br.ufes.deliverypedidos.service.RastreamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
@Tag(name = "Pedidos", description = "Ciclo de vida do pedido (State), listagens e rastreamento")
public class PedidoController {

    private final PedidoService service;
    private final RastreamentoService rastreamentoService;

    public PedidoController(PedidoService service, RastreamentoService rastreamentoService) {
        this.service = service;
        this.rastreamentoService = rastreamentoService;
    }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria um pedido para o cliente logado (status inicial REALIZADO)")
    public PedidoResponse criar(@AuthenticationPrincipal Usuario usuario, @RequestBody @Valid PedidoRequest req) {
        return service.criar(req, usuario);
    }

    // Visão geral do moderador: todos os pedidos, com filtro opcional por status.
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lista todos os pedidos com filtro opcional por status (somente ADMIN)")
    public Page<PedidoResponse> listar(@RequestParam(required = false) StatusPedido status,
                                       @PageableDefault(size = 20, sort = "dataHora",
                                               direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(status, pageable);
    }

    // Pedidos do próprio ator logado (cliente, restaurante ou entregador).
    @GetMapping("/meus")
    @Operation(summary = "Lista os pedidos do ator logado (escopo automático por papel)")
    public Page<PedidoResponse> listarMeus(@AuthenticationPrincipal Usuario usuario,
                                           @PageableDefault(size = 20, sort = "dataHora",
                                                   direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listarDoUsuario(usuario, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @posse.podeVerPedido(#id, authentication)")
    @Operation(summary = "Busca um pedido por id (quem participa dele ou ADMIN)")
    public PedidoResponse buscar(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    // Histórico de rastreamento do pedido (eventos gravados pelo Observer).
    @GetMapping("/{id}/rastreamento")
    @PreAuthorize("hasRole('ADMIN') or @posse.podeVerPedido(#id, authentication)")
    @Operation(summary = "Histórico de rastreamento do pedido (eventos do Observer)")
    public List<EventoRastreamentoResponse> rastrear(@PathVariable Long id) {
        return rastreamentoService.doPedido(id);
    }

    @PatchMapping("/{id}/confirmar")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    @Operation(summary = "Confirma o pedido: REALIZADO -> CONFIRMADO (restaurante dono ou ADMIN)")
    public PedidoResponse confirmar(@PathVariable Long id) {
        return service.confirmar(id);
    }

    @PatchMapping("/{id}/iniciar-preparo")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    @Operation(summary = "Inicia o preparo: CONFIRMADO -> EM_PREPARO (restaurante dono ou ADMIN)")
    public PedidoResponse iniciarPreparo(@PathVariable Long id) {
        return service.iniciarPreparo(id);
    }

    @PatchMapping("/{id}/marcar-pronto")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    @Operation(summary = "Marca como pronto: EM_PREPARO -> PRONTO (restaurante dono ou ADMIN)")
    public PedidoResponse marcarPronto(@PathVariable Long id) {
        return service.marcarPronto(id);
    }

    @PutMapping("/{id}/entregador")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    @Operation(summary = "Atribui um entregador ao pedido (restaurante dono ou ADMIN)")
    public PedidoResponse atribuirEntregador(@PathVariable Long id,
                                             @RequestBody @Valid AtribuirEntregadorRequest req) {
        return service.atribuirEntregador(id, req.entregadorId());
    }

    @PatchMapping("/{id}/despachar")
    @PreAuthorize("hasRole('ADMIN') or @posse.restauranteDoPedido(#id, authentication)")
    @Operation(summary = "Despacha o pedido: PRONTO -> EM_ROTA (restaurante dono ou ADMIN)")
    public PedidoResponse despachar(@PathVariable Long id) {
        return service.despachar(id);
    }

    @PatchMapping("/{id}/entregar")
    @PreAuthorize("hasRole('ADMIN') or @posse.entregadorDoPedido(#id, authentication)")
    @Operation(summary = "Conclui a entrega: EM_ROTA -> ENTREGUE (entregador atribuído ou ADMIN)")
    public PedidoResponse entregar(@PathVariable Long id) {
        return service.entregar(id);
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoPedido(#id, authentication) "
            + "or @posse.restauranteDoPedido(#id, authentication)")
    @Operation(summary = "Cancela o pedido (cliente dono, restaurante dono ou ADMIN)")
    public PedidoResponse cancelar(@PathVariable Long id) {
        return service.cancelar(id);
    }
}
