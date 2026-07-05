package br.ufes.deliverypedidos.service;

import br.ufes.deliverypedidos.domain.model.Restaurante;
import br.ufes.deliverypedidos.dto.request.RestauranteRequest;
import br.ufes.deliverypedidos.dto.response.RestauranteResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.mapper.EnderecoMapper;
import br.ufes.deliverypedidos.mapper.RestauranteMapper;
import br.ufes.deliverypedidos.repository.RestauranteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestauranteService {

    private final RestauranteRepository repository;
    private final RestauranteMapper mapper;
    private final EnderecoMapper enderecoMapper;

    public RestauranteService(RestauranteRepository repository, RestauranteMapper mapper,
                              EnderecoMapper enderecoMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.enderecoMapper = enderecoMapper;
    }

    @Transactional
    public RestauranteResponse criar(RestauranteRequest req) {
        return mapper.toResponse(repository.save(mapper.toEntity(req)));
    }

    @Transactional(readOnly = true)
    public List<RestauranteResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public RestauranteResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public RestauranteResponse atualizar(Long id, RestauranteRequest req) {
        Restaurante restaurante = buscarEntidade(id);
        restaurante.setNome(req.nome());
        restaurante.setTelefone(req.telefone());
        restaurante.setCategoria(req.categoria());
        restaurante.setTaxaEntrega(req.taxaEntrega());
        restaurante.setEndereco(enderecoMapper.toEntity(req.endereco()));
        return mapper.toResponse(restaurante);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Restaurante buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Restaurante não encontrado: " + id));
    }
}
