package com.aiondigital.mfe.lookupsservice.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.aiondigital.mfe.lookupsservice.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Card.class);
        Card card1 = new Card();
        card1.setId("id1");
        Card card2 = new Card();
        card2.setId(card1.getId());
        assertThat(card1).isEqualTo(card2);
        card2.setId("id2");
        assertThat(card1).isNotEqualTo(card2);
        card1.setId(null);
        assertThat(card1).isNotEqualTo(card2);
    }
}
