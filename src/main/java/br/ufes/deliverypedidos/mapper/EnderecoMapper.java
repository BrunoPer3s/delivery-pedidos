package br.ufes.deliverypedidos.mapper;

import br.ufes.deliverypedidos.domain.model.Endereco;
import br.ufes.deliverypedidos.dto.EnderecoDTO;
import org.springframework.stereotype.Component;

@Component
public class EnderecoMapper {

    public Endereco toEntity(EnderecoDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Endereco(dto.logradouro(), dto.numero(), dto.bairro(), dto.cidade(), dto.cep());
    }

    public EnderecoDTO toDTO(Endereco endereco) {
        if (endereco == null) {
            return null;
        }
        return new EnderecoDTO(
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getCep());
    }
}
