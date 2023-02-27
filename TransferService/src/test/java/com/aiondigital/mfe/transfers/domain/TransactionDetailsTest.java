package com.aiondigital.mfe.transfers.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.aiondigital.mfe.transfers.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransactionDetailsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TransactionDetails.class);
        TransactionDetails transactionDetails1 = new TransactionDetails();
        transactionDetails1.setId("id1");
        TransactionDetails transactionDetails2 = new TransactionDetails();
        transactionDetails2.setId(transactionDetails1.getId());
        assertThat(transactionDetails1).isEqualTo(transactionDetails2);
        transactionDetails2.setId("id2");
        assertThat(transactionDetails1).isNotEqualTo(transactionDetails2);
        transactionDetails1.setId(null);
        assertThat(transactionDetails1).isNotEqualTo(transactionDetails2);
    }
}
