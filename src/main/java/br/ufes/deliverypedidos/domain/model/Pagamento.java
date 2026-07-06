package br.ufes.deliverypedidos.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamento")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento forma;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(nullable = false)
    private String detalhe;

    @Column(nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    protected Pagamento() {
    }

    public Pagamento(Pedido pedido, FormaPagamento forma, BigDecimal valor, String detalhe) {
        this.pedido = pedido;
        this.forma = forma;
        this.valor = valor;
        this.detalhe = detalhe;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public FormaPagamento getForma() {
        return forma;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public String getDetalhe() {
        return detalhe;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
}
