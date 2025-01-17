package com.uniprojecao.fabrica.gprojuridico.data;

import com.uniprojecao.fabrica.gprojuridico.domains.Endereco;
import com.uniprojecao.fabrica.gprojuridico.domains.assistido.Assistido;
import com.uniprojecao.fabrica.gprojuridico.domains.assistido.AssistidoCivil;
import com.uniprojecao.fabrica.gprojuridico.domains.assistido.AssistidoTrabalhista;

import java.util.List;

public class AssistidoData {
    public static List<Assistido> seedWithAssistido() {
        return List.of(
                new AssistidoCivil("Cleyton Pina Auzier", "14.762.687-0", "288.610.170-29", "Brasileiro", "Superior", "Casado", "Advogado", "(61) 92413-0161", "cleyton.pina@example.com", new Assistido.Filiacao("Thayanna Moraes Elias", "José Mário Sena Santos"), "R$ 9.000", new Endereco("Quadra QN 514 Conjunto", "6"), "Brasília", "21/02/1985", 3),
                new AssistidoCivil("Carmen Azevedo Dores", "30.578.754-8", "037.886.591-90", "Brasileira", "Superior", "Casada", "Professora", "(61) 92152-4592", "carmen.azevedo@example.com", new Assistido.Filiacao("Rosiméri Aguiar Rubi Teixeira", "Bento Leonicio da Mota Ervano"), "R$ 5.200", new Endereco("Quadra QS 6 Conjunto", "240B"), "Brasília", "14/04/1993", 1),
                new AssistidoTrabalhista("Rogério Bezerra Brito", "48.433.953-9", "223.128.851-66", "Brasileiro", "Mestrado", "Solteiro", "Gerente financeiro", "(61) 93872-0425", "rogerio.bezerra@example.com", new Assistido.Filiacao("Kamila Ervano Quintanilha", "Osvaldo dos Anjos Cordeiro"), "R$ 11.100", new Endereco("Área ADE Conjunto", "10"), new AssistidoTrabalhista.Ctps("0921002", "001-0", "DF"), "276.65825.76-7", true)
        );
    }
}
