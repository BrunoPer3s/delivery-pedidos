package br.ufes.deliverypedidos.domain.model;

import br.ufes.deliverypedidos.domain.state.EstadoPedido;
import br.ufes.deliverypedidos.domain.state.EstadoPedidoFactory;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "restaurante_id")
    private Restaurante restaurante;

    @ManyToOne
    @JoinColumn(name = "entregador_id")
    private Entregador entregador;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.REALIZADO;

    @Transient
    private EstadoPedido estado;

    @Embedded
    private Endereco enderecoEntrega;

    @Column(nullable = false)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    protected Pedido() {
    }

    public Pedido(Cliente cliente, Restaurante restaurante, Endereco enderecoEntrega) {
        this.cliente = cliente;
        this.restaurante = restaurante;
        this.enderecoEntrega = enderecoEntrega;
    }

    public void adicionarItem(ItemPedido item) {
        item.setPedido(this);
        itens.add(item);
        recalcularTotal();
    }

    private void recalcularTotal() {
        BigDecimal totalItens = itens.stream()
                .map(ItemPedido::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal taxa = (restaurante != null && restaurante.getTaxaEntrega() != null)
                ? restaurante.getTaxaEntrega()
                : BigDecimal.ZERO;
        this.valorTotal = totalItens.add(taxa);
    }

    public void confirmar() {
        estadoAtual().confirmar();
    }

    public void iniciarPreparo() {
        estadoAtual().iniciarPreparo();
    }

    public void marcarPronto() {
        estadoAtual().marcarPronto();
    }

    public void despachar() {
        estadoAtual().despachar();
    }

    public void entregar() {
        estadoAtual().entregar();
    }

    public void cancelar() {
        estadoAtual().cancelar();
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
        this.status = estado.getStatus();
    }

    private EstadoPedido estadoAtual() {
        if (estado == null) {
            estado = EstadoPedidoFactory.criar(this, status);
        }
        return estado;
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public Entregador getEntregador() {
        return entregador;
    }

    public void setEntregador(Entregador entregador) {
        this.entregador = entregador;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public Endereco getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
