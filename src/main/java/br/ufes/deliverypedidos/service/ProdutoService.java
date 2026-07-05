package br.ufes.deliverypedidos.service;

import br.ufes.deliverypedidos.domain.model.Produto;
import br.ufes.deliverypedidos.domain.model.Restaurante;
import br.ufes.deliverypedidos.dto.request.ProdutoRequest;
import br.ufes.deliverypedidos.dto.response.ProdutoResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.mapper.ProdutoMapper;
import br.ufes.deliverypedidos.repository.ProdutoRepository;
import br.ufes.deliverypedidos.repository.RestauranteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;
    private final RestauranteRepository restauranteRepository;
    private final ProdutoMapper mapper;

    public ProdutoService(ProdutoRepository repository, RestauranteRepository restauranteRepository,
                          ProdutoMapper mapper) {
        this.repository = repository;
        this.restauranteRepository = restauranteRepository;
        this.mapper = mapper;
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest req) {
        Restaurante restaurante = buscarRestaurante(req.restauranteId());
        return mapper.toResponse(repository.save(mapper.toEntity(req, restaurante)));
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarPorRestaurante(Long restauranteId) {
        return repository.findByRestauranteId(restauranteId).stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
        Produto produto = buscarEntidade(id);
        produto.setNome(req.nome());
        produto.setDescricao(req.descricao());
        produto.setPreco(req.preco());
        produto.setDisponivel(req.disponivel());
        return mapper.toResponse(produto);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Produto buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + id));
    }

    private Restaurante buscarRestaurante(Long id) {
        return restauranteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Restaurante não encontrado: " + id));
    }
}
