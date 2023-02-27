package com.aiondigital.mfe.finances.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.aiondigital.mfe.finances.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FinanceRequestTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(FinanceRequest.class);
        FinanceRequest financeRequest1 = new FinanceRequest();
        financeRequest1.setId("id1");
        FinanceRequest financeRequest2 = new FinanceRequest();
        financeRequest2.setId(financeRequest1.getId());
        assertThat(financeRequest1).isEqualTo(financeRequest2);
        financeRequest2.setId("id2");
        assertThat(financeRequest1).isNotEqualTo(financeRequest2);
        financeRequest1.setId(null);
        assertThat(financeRequest1).isNotEqualTo(financeRequest2);
    }
}
