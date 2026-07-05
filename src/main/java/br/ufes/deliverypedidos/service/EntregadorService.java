package br.ufes.deliverypedidos.service;

import br.ufes.deliverypedidos.domain.model.Entregador;
import br.ufes.deliverypedidos.dto.request.EntregadorRequest;
import br.ufes.deliverypedidos.dto.response.EntregadorResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.mapper.EntregadorMapper;
import br.ufes.deliverypedidos.repository.EntregadorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EntregadorService {

    private final EntregadorRepository repository;
    private final EntregadorMapper mapper;

    public EntregadorService(EntregadorRepository repository, EntregadorMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public EntregadorResponse criar(EntregadorRequest req) {
        return mapper.toResponse(repository.save(mapper.toEntity(req)));
    }

    @Transactional(readOnly = true)
    public List<EntregadorResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EntregadorResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public EntregadorResponse atualizar(Long id, EntregadorRequest req) {
        Entregador entregador = buscarEntidade(id);
        entregador.setNome(req.nome());
        entregador.setTelefone(req.telefone());
        entregador.setPlacaVeiculo(req.placaVeiculo());
        return mapper.toResponse(entregador);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Entregador buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Entregador não encontrado: " + id));
    }
}
