package com.gv.jhipsterapp001.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.gv.jhipsterapp001.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PalhaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Palha.class);
        Palha palha1 = new Palha();
        palha1.setId(1L);
        Palha palha2 = new Palha();
        palha2.setId(palha1.getId());
        assertThat(palha1).isEqualTo(palha2);
        palha2.setId(2L);
        assertThat(palha1).isNotEqualTo(palha2);
        palha1.setId(null);
        assertThat(palha1).isNotEqualTo(palha2);
    }
}
