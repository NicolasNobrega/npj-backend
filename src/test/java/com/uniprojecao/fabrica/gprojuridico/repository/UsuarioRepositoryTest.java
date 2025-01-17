package com.uniprojecao.fabrica.gprojuridico.repository;

import com.uniprojecao.fabrica.gprojuridico.Utils;
import com.uniprojecao.fabrica.gprojuridico.domains.enums.FilterType;
import com.uniprojecao.fabrica.gprojuridico.domains.usuario.Usuario;
import com.uniprojecao.fabrica.gprojuridico.dto.QueryFilter;
import com.uniprojecao.fabrica.gprojuridico.interfaces.CsvToUsuario;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import static com.uniprojecao.fabrica.gprojuridico.Utils.Clazz;
import static com.uniprojecao.fabrica.gprojuridico.Utils.getFirestore;
import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS) // Sem esta annotation, o atributo "count" não é incrementado sequencialmente.
class UsuarioRepositoryTest {

    private final UsuarioRepository underTest = new UsuarioRepository();
    private final Utils utils = new Utils();
    private Integer count = 0;

    public UsuarioRepositoryTest() {
        underTest.firestore = getFirestore();
    }

    @BeforeEach
    void setUp() {
        utils.seedDatabase(count, Clazz.USUARIO);
    }

    @Test
    void findAll() {
        QueryFilter queryFilter = new QueryFilter("role", "PROFESSOR", FilterType.EQUAL);

        var list1 = underTest.findAll(20, null);
        assertEquals(6, list1.size());

        var list2 = underTest.findAll(20, queryFilter);
        assertEquals(2, list2.size());

        count++;
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/usuarios.csv")
    void findById(@CsvToUsuario Usuario usuario) {
        String id = usuario.getEmail();

        Usuario result = underTest.findById(id);
        assertEquals(result, usuario);

        count++;
    }

    @AfterEach
    void tearDown() {
        if (count == 7) utils.clearDatabase(null, Clazz.USUARIO);
    }
}