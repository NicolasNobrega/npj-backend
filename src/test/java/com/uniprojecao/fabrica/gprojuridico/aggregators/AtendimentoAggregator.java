package com.uniprojecao.fabrica.gprojuridico.aggregators;

import com.google.cloud.Timestamp;
import com.uniprojecao.fabrica.gprojuridico.domains.atendimento.*;
import com.uniprojecao.fabrica.gprojuridico.dto.EnvolvidoDTO;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtendimentoAggregator implements ArgumentsAggregator {
    @Override
    public Object aggregateArguments(
            ArgumentsAccessor accessor,
            ParameterContext context
    ) {
        /* É tido que:
        *
        * A classe abstrata Atendimento possui 7 atributos e 18 campos, sendo:
        *   - 4 String
        *   - 1 Timestamp
        *   - 1 List<EntradaHistorico>, a qual EntradaHistorico possui 5 atributos e 11 campos, sendo:
        *       - 3 String
        *       - 1 Instant
        *       - 1 UsuarioMin (4 atributos/campos, sendo 3 String e 1 Boolean)
        *
        *       Logo, é tido que este atributo envolve 8 campos * n EntradaHi..., sendo "n" um número natural positivo.
        *
        *   - 1 Map<String, Envolvido>, a qual Envolvido possui 2 atributos/campos, sendo:
        *       - 2 String
        *
        *       Logo, é tido que este atributo envolve 2 campos * k chave, sendo "k" um número natural positivo.
        *
        * Total, assumindo que "n" e "k" valem 1:
        *
        *   4 + 1 + (n * (3 + 1 + (4))) + (k * (2)) = 5 + 8 + 2 = 15 campos
        *
        * -------------------------------------------------------------------------------
        *
        * A classe AssistidoCivil possui 8 atributos e 26 campos, sendo:
        *   - 1 FichaCivil, a qual esta abrange:
        *       - 1 ParteContraria (7 atributos/campos, sendo todos String)
        *       - 1 String
        *
        *   Logo, é tido que este atributo envolve 8 campos.
        *
        * Total: 15 campos (da herança) + 8 (da própria classe) = 23 campos
        *
        * OBS: os campos da herança da classe Ficha não foram considerados nessa conta.
        *
        * -------------------------------------------------------------------------------
        *
        * A classe AssistidoTrabalhista possui 8 atributos e 64 campos, sendo:
        *   - 1 FichaTrabalhista, a qual esta abrange:
        *       - 1 Reclamado (4 atributos/campos, sendo 4 String)
        *       - 1 RelacaoEmpregaticia (30 atributos/campos, sendo 15 String, 4 Integer e 11 Boolean)
        *       - 1 DocumentosDepositadosNpj (12 atributos/campos, sendo 11 Boolean e 1 String)
        *       - 1 String
        *
        *   Logo, é tido que este atributo envolve 46 campos.
        *
        * Total: 15 campos (da herança) + 46 (da própria class) = 64 campos
        *
        * OBS: os campos da herança da classe Ficha não foram considerados nessa conta.
        * */
        int accessorSize = accessor.size();

        // Para o atributo "prazoEntregaDocumentos"
        Timestamp formattedDate;
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            formattedDate = Timestamp.of(df.parse(accessor.getString(3)));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Para o atributo "historico"
        var usuario = new Atendimento.EntradaHistorico.UsuarioMin(
                accessor.getString(5),
                accessor.getString(6),
                accessor.getString(7));
        var entrada = new Atendimento.EntradaHistorico(null, accessor.getString(4), null, null, usuario);
        List<Atendimento.EntradaHistorico> historico = new ArrayList<>();
        historico.add(entrada);

        // Para o atributo "envolvidos"
        var estagiario = new EnvolvidoDTO(accessor.getString(8), accessor.getString(9));
        var professor = new EnvolvidoDTO(accessor.getString(10), accessor.getString(11));
        var secretaria = new EnvolvidoDTO(accessor.getString(12), accessor.getString(13));
        Map<String, EnvolvidoDTO> envolvidos = new HashMap<>(Map.of("estagiario", estagiario, "professor", professor, "secretaria", secretaria));

        if (accessorSize == 21) { // Os atributos instante e medida judicial não entram, logo fica 23 - 2


            var ac = new AtendimentoCivil();
            ac.setId(accessor.getString(0));
            ac.setStatus(accessor.getString(1));
            ac.setArea(accessor.getString(2));
            // instante ignorado
            ac.setPrazoEntregaDocumentos(formattedDate);
            ac.setHistorico(historico);
            ac.setEnvolvidos(envolvidos);

            // Para o atributo "ficha"
            var parteContraria = new ParteContraria(
                    accessor.getString(14),
                    accessor.getString(15),
                    accessor.getString(16),
                    accessor.getString(17),
                    accessor.getString(18),
                    accessor.getString(19),
                    accessor.getString(20)
            );
            var ficha = new FichaCivil(null, false, new ArrayList<>(), parteContraria, null); // campos da herança ignorados.

            ac.setFicha(ficha);

            return ac;
        } else if (accessorSize == 64) {
            var at = new AtendimentoTrabalhista();


            return at;
        }

        return null;
    }
}
