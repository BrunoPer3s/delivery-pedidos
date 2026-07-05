package br.ufes.deliverypedidos.service;

import br.ufes.deliverypedidos.domain.model.Cliente;
import br.ufes.deliverypedidos.domain.model.Usuario;
import br.ufes.deliverypedidos.dto.request.ClienteRequest;
import br.ufes.deliverypedidos.dto.response.ClienteResponse;
import br.ufes.deliverypedidos.exception.RecursoNaoEncontradoException;
import br.ufes.deliverypedidos.exception.RegraDeNegocioException;
import br.ufes.deliverypedidos.mapper.ClienteMapper;
import br.ufes.deliverypedidos.mapper.EnderecoMapper;
import br.ufes.deliverypedidos.repository.ClienteRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;
    private final EnderecoMapper enderecoMapper;

    public ClienteService(ClienteRepository repository, ClienteMapper mapper, EnderecoMapper enderecoMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.enderecoMapper = enderecoMapper;
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest req, Usuario dono) {
        if (repository.existsByUsuarioId(dono.getId())) {
            throw new RegraDeNegocioException("Este usuário já possui um cliente cadastrado");
        }
        if (repository.existsByEmail(req.email())) {
            throw new RegraDeNegocioException("Já existe um cliente com o e-mail " + req.email());
        }
        Cliente cliente = mapper.toEntity(req);
        cliente.setUsuario(dono);
        return mapper.toResponse(repository.save(cliente));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscarPorId(Long id) {
        return mapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente cliente = buscarEntidade(id);
        if (!cliente.getEmail().equals(req.email()) && repository.existsByEmail(req.email())) {
            throw new RegraDeNegocioException("Já existe um cliente com o e-mail " + req.email());
        }
        cliente.setNome(req.nome());
        cliente.setEmail(req.email());
        cliente.setTelefone(req.telefone());
        cliente.setEndereco(enderecoMapper.toEntity(req.endereco()));
        return mapper.toResponse(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    private Cliente buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente não encontrado: " + id));
    }
}
