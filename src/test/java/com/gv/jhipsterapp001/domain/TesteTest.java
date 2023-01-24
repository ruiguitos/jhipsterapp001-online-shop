package com.gv.jhipsterapp001.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gv.jhipsterapp001.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TesteTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Teste.class);
        Teste teste1 = new Teste();
        teste1.setId(1L);
        Teste teste2 = new Teste();
        teste2.setId(teste1.getId());
        assertThat(teste1).isEqualTo(teste2);
        teste2.setId(2L);
        assertThat(teste1).isNotEqualTo(teste2);
        teste1.setId(null);
        assertThat(teste1).isNotEqualTo(teste2);
    }
}
