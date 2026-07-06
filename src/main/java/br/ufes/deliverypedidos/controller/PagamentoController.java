package br.ufes.deliverypedidos.controller;

import br.ufes.deliverypedidos.dto.request.PagamentoRequest;
import br.ufes.deliverypedidos.dto.response.PagamentoResponse;
import br.ufes.deliverypedidos.service.PagamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pedidos/{pedidoId}/pagamento")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or @posse.donoDoPedido(#pedidoId, authentication)")
    @ResponseStatus(HttpStatus.CREATED)
    public PagamentoResponse pagar(@PathVariable Long pedidoId, @RequestBody @Valid PagamentoRequest req) {
        return service.pagar(pedidoId, req.forma());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or @posse.podeVerPedido(#pedidoId, authentication)")
    public PagamentoResponse buscar(@PathVariable Long pedidoId) {
        return service.buscarPorPedido(pedidoId);
    }
}
