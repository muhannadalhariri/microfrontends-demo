package com.aiondigital.mfe.transfers.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aiondigital.mfe.transfers.IntegrationTest;
import com.aiondigital.mfe.transfers.domain.TransactionDetails;
import com.aiondigital.mfe.transfers.repository.TransactionDetailsRepository;
import com.aiondigital.mfe.transfers.repository.search.TransactionDetailsSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.IterableUtils;
import org.assertj.core.util.IterableUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Integration tests for the {@link TransactionDetailsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TransactionDetailsResourceIT {

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String UPDATED_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transaction-details";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/transaction-details";

    @Autowired
    private TransactionDetailsRepository transactionDetailsRepository;

    @Autowired
    private TransactionDetailsSearchRepository transactionDetailsSearchRepository;

    @Autowired
    private MockMvc restTransactionDetailsMockMvc;

    private TransactionDetails transactionDetails;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionDetails createEntity() {
        TransactionDetails transactionDetails = new TransactionDetails().key(DEFAULT_KEY).value(DEFAULT_VALUE);
        return transactionDetails;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TransactionDetails createUpdatedEntity() {
        TransactionDetails transactionDetails = new TransactionDetails().key(UPDATED_KEY).value(UPDATED_VALUE);
        return transactionDetails;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        transactionDetailsSearchRepository.deleteAll();
        assertThat(transactionDetailsSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        transactionDetailsRepository.deleteAll();
        transactionDetails = createEntity();
    }

    @Test
    void createTransactionDetails() throws Exception {
        int databaseSizeBeforeCreate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        // Create the TransactionDetails
        restTransactionDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isCreated());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        TransactionDetails testTransactionDetails = transactionDetailsList.get(transactionDetailsList.size() - 1);
        assertThat(testTransactionDetails.getKey()).isEqualTo(DEFAULT_KEY);
        assertThat(testTransactionDetails.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void createTransactionDetailsWithExistingId() throws Exception {
        // Create the TransactionDetails with an existing ID
        transactionDetails.setId("existing_id");

        int databaseSizeBeforeCreate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTransactionDetailsMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllTransactionDetails() throws Exception {
        // Initialize the database
        transactionDetailsRepository.save(transactionDetails);

        // Get all the transactionDetailsList
        restTransactionDetailsMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionDetails.getId())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    void getTransactionDetails() throws Exception {
        // Initialize the database
        transactionDetailsRepository.save(transactionDetails);

        // Get the transactionDetails
        restTransactionDetailsMockMvc
            .perform(get(ENTITY_API_URL_ID, transactionDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(transactionDetails.getId()))
            .andExpect(jsonPath("$.key").value(DEFAULT_KEY))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    void getNonExistingTransactionDetails() throws Exception {
        // Get the transactionDetails
        restTransactionDetailsMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingTransactionDetails() throws Exception {
        // Initialize the database
        transactionDetailsRepository.save(transactionDetails);

        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();
        transactionDetailsSearchRepository.save(transactionDetails);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());

        // Update the transactionDetails
        TransactionDetails updatedTransactionDetails = transactionDetailsRepository.findById(transactionDetails.getId()).get();
        updatedTransactionDetails.key(UPDATED_KEY).value(UPDATED_VALUE);

        restTransactionDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedTransactionDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedTransactionDetails))
            )
            .andExpect(status().isOk());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        TransactionDetails testTransactionDetails = transactionDetailsList.get(transactionDetailsList.size() - 1);
        assertThat(testTransactionDetails.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testTransactionDetails.getValue()).isEqualTo(UPDATED_VALUE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<TransactionDetails> transactionDetailsSearchList = IterableUtils.toList(transactionDetailsSearchRepository.findAll());
                TransactionDetails testTransactionDetailsSearch = transactionDetailsSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testTransactionDetailsSearch.getKey()).isEqualTo(UPDATED_KEY);
                assertThat(testTransactionDetailsSearch.getValue()).isEqualTo(UPDATED_VALUE);
            });
    }

    @Test
    void putNonExistingTransactionDetails() throws Exception {
        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        transactionDetails.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, transactionDetails.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchTransactionDetails() throws Exception {
        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        transactionDetails.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionDetailsMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamTransactionDetails() throws Exception {
        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        transactionDetails.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionDetailsMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateTransactionDetailsWithPatch() throws Exception {
        // Initialize the database
        transactionDetailsRepository.save(transactionDetails);

        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();

        // Update the transactionDetails using partial update
        TransactionDetails partialUpdatedTransactionDetails = new TransactionDetails();
        partialUpdatedTransactionDetails.setId(transactionDetails.getId());

        partialUpdatedTransactionDetails.key(UPDATED_KEY).value(UPDATED_VALUE);

        restTransactionDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionDetails))
            )
            .andExpect(status().isOk());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        TransactionDetails testTransactionDetails = transactionDetailsList.get(transactionDetailsList.size() - 1);
        assertThat(testTransactionDetails.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testTransactionDetails.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    void fullUpdateTransactionDetailsWithPatch() throws Exception {
        // Initialize the database
        transactionDetailsRepository.save(transactionDetails);

        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();

        // Update the transactionDetails using partial update
        TransactionDetails partialUpdatedTransactionDetails = new TransactionDetails();
        partialUpdatedTransactionDetails.setId(transactionDetails.getId());

        partialUpdatedTransactionDetails.key(UPDATED_KEY).value(UPDATED_VALUE);

        restTransactionDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTransactionDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedTransactionDetails))
            )
            .andExpect(status().isOk());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        TransactionDetails testTransactionDetails = transactionDetailsList.get(transactionDetailsList.size() - 1);
        assertThat(testTransactionDetails.getKey()).isEqualTo(UPDATED_KEY);
        assertThat(testTransactionDetails.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    void patchNonExistingTransactionDetails() throws Exception {
        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        transactionDetails.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTransactionDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, transactionDetails.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchTransactionDetails() throws Exception {
        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        transactionDetails.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isBadRequest());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamTransactionDetails() throws Exception {
        int databaseSizeBeforeUpdate = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        transactionDetails.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTransactionDetailsMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(transactionDetails))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the TransactionDetails in the database
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteTransactionDetails() throws Exception {
        // Initialize the database
        transactionDetailsRepository.save(transactionDetails);
        transactionDetailsRepository.save(transactionDetails);
        transactionDetailsSearchRepository.save(transactionDetails);

        int databaseSizeBeforeDelete = transactionDetailsRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the transactionDetails
        restTransactionDetailsMockMvc
            .perform(delete(ENTITY_API_URL_ID, transactionDetails.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<TransactionDetails> transactionDetailsList = transactionDetailsRepository.findAll();
        assertThat(transactionDetailsList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(transactionDetailsSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchTransactionDetails() throws Exception {
        // Initialize the database
        transactionDetails = transactionDetailsRepository.save(transactionDetails);
        transactionDetailsSearchRepository.save(transactionDetails);

        // Search the transactionDetails
        restTransactionDetailsMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + transactionDetails.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(transactionDetails.getId())))
            .andExpect(jsonPath("$.[*].key").value(hasItem(DEFAULT_KEY)))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }
}
