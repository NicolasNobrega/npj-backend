package com.uniprojecao.fabrica.gprojuridico.dto.assistido;

import com.uniprojecao.fabrica.gprojuridico.domains.assistido.AssistidoTrabalhista;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AssistidoTrabalhistaDTO extends AssistidoDTO {
    @NotNull
    private Ctps ctps;

    @NotBlank
    private String pis;

    @NotNull
    private Boolean empregadoAtualmente;

    public AssistidoTrabalhistaDTO(AssistidoTrabalhista a) {
        super(
                a.getNome(),
                a.getRg(),
                a.getCpf(),
                a.getNacionalidade(),
                a.getEscolaridade(),
                a.getEstadoCivil(),
                a.getProfissao(),
                a.getTelefone(),
                a.getEmail(),
                new Filiacao(a.getFiliacao().getMae(), a.getFiliacao().getPai()),
                a.getRemuneracao(),
                new Endereco(a.getEndereco().getLogradouro(), a.getEndereco().getBairro(), a.getEndereco().getNumero(), a.getEndereco().getComplemento(), a.getEndereco().getCep(), a.getEndereco().getCidade()));
        ctps = new Ctps(a.getCtps().getNumero(), a.getCtps().getSerie(), a.getCtps().getUf());
        pis = a.getPis();
        empregadoAtualmente = a.getEmpregadoAtualmente();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class Ctps {
        @NotBlank
        private String numero;

        @NotBlank
        private String serie;

        @NotBlank
        @Size(min = 2, max = 2)
        private String uf;
    }
}
