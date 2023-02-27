package com.aiondigital.mfe.finances.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aiondigital.mfe.finances.IntegrationTest;
import com.aiondigital.mfe.finances.domain.FinanceRequest;
import com.aiondigital.mfe.finances.repository.FinanceRequestRepository;
import com.aiondigital.mfe.finances.repository.search.FinanceRequestSearchRepository;
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
 * Integration tests for the {@link FinanceRequestResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FinanceRequestResourceIT {

    private static final String DEFAULT_USER_ID = "AAAAAAAAAA";
    private static final String UPDATED_USER_ID = "BBBBBBBBBB";

    private static final Double DEFAULT_TOTAL_AMOUNT = 1D;
    private static final Double UPDATED_TOTAL_AMOUNT = 2D;

    private static final Double DEFAULT_INSTALLMENT_AMOUNT = 1D;
    private static final Double UPDATED_INSTALLMENT_AMOUNT = 2D;

    private static final Integer DEFAULT_INSTALLMENT_PERIOD = 1;
    private static final Integer UPDATED_INSTALLMENT_PERIOD = 2;

    private static final String ENTITY_API_URL = "/api/finance-requests";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/finance-requests";

    @Autowired
    private FinanceRequestRepository financeRequestRepository;

    @Autowired
    private FinanceRequestSearchRepository financeRequestSearchRepository;

    @Autowired
    private MockMvc restFinanceRequestMockMvc;

    private FinanceRequest financeRequest;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceRequest createEntity() {
        FinanceRequest financeRequest = new FinanceRequest()
            .userId(DEFAULT_USER_ID)
            .totalAmount(DEFAULT_TOTAL_AMOUNT)
            .installmentAmount(DEFAULT_INSTALLMENT_AMOUNT)
            .installmentPeriod(DEFAULT_INSTALLMENT_PERIOD);
        return financeRequest;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FinanceRequest createUpdatedEntity() {
        FinanceRequest financeRequest = new FinanceRequest()
            .userId(UPDATED_USER_ID)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .installmentAmount(UPDATED_INSTALLMENT_AMOUNT)
            .installmentPeriod(UPDATED_INSTALLMENT_PERIOD);
        return financeRequest;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        financeRequestSearchRepository.deleteAll();
        assertThat(financeRequestSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        financeRequestRepository.deleteAll();
        financeRequest = createEntity();
    }

    @Test
    void createFinanceRequest() throws Exception {
        int databaseSizeBeforeCreate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        // Create the FinanceRequest
        restFinanceRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isCreated());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        FinanceRequest testFinanceRequest = financeRequestList.get(financeRequestList.size() - 1);
        assertThat(testFinanceRequest.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testFinanceRequest.getTotalAmount()).isEqualTo(DEFAULT_TOTAL_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentAmount()).isEqualTo(DEFAULT_INSTALLMENT_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentPeriod()).isEqualTo(DEFAULT_INSTALLMENT_PERIOD);
    }

    @Test
    void createFinanceRequestWithExistingId() throws Exception {
        // Create the FinanceRequest with an existing ID
        financeRequest.setId("existing_id");

        int databaseSizeBeforeCreate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restFinanceRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkUserIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        // set the field null
        financeRequest.setUserId(null);

        // Create the FinanceRequest, which fails.

        restFinanceRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkTotalAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        // set the field null
        financeRequest.setTotalAmount(null);

        // Create the FinanceRequest, which fails.

        restFinanceRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkInstallmentAmountIsRequired() throws Exception {
        int databaseSizeBeforeTest = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        // set the field null
        financeRequest.setInstallmentAmount(null);

        // Create the FinanceRequest, which fails.

        restFinanceRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void checkInstallmentPeriodIsRequired() throws Exception {
        int databaseSizeBeforeTest = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        // set the field null
        financeRequest.setInstallmentPeriod(null);

        // Create the FinanceRequest, which fails.

        restFinanceRequestMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeTest);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllFinanceRequests() throws Exception {
        // Initialize the database
        financeRequestRepository.save(financeRequest);

        // Get all the financeRequestList
        restFinanceRequestMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeRequest.getId())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].installmentAmount").value(hasItem(DEFAULT_INSTALLMENT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].installmentPeriod").value(hasItem(DEFAULT_INSTALLMENT_PERIOD)));
    }

    @Test
    void getFinanceRequest() throws Exception {
        // Initialize the database
        financeRequestRepository.save(financeRequest);

        // Get the financeRequest
        restFinanceRequestMockMvc
            .perform(get(ENTITY_API_URL_ID, financeRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(financeRequest.getId()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID))
            .andExpect(jsonPath("$.totalAmount").value(DEFAULT_TOTAL_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.installmentAmount").value(DEFAULT_INSTALLMENT_AMOUNT.doubleValue()))
            .andExpect(jsonPath("$.installmentPeriod").value(DEFAULT_INSTALLMENT_PERIOD));
    }

    @Test
    void getNonExistingFinanceRequest() throws Exception {
        // Get the financeRequest
        restFinanceRequestMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingFinanceRequest() throws Exception {
        // Initialize the database
        financeRequestRepository.save(financeRequest);

        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();
        financeRequestSearchRepository.save(financeRequest);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());

        // Update the financeRequest
        FinanceRequest updatedFinanceRequest = financeRequestRepository.findById(financeRequest.getId()).get();
        updatedFinanceRequest
            .userId(UPDATED_USER_ID)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .installmentAmount(UPDATED_INSTALLMENT_AMOUNT)
            .installmentPeriod(UPDATED_INSTALLMENT_PERIOD);

        restFinanceRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFinanceRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedFinanceRequest))
            )
            .andExpect(status().isOk());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        FinanceRequest testFinanceRequest = financeRequestList.get(financeRequestList.size() - 1);
        assertThat(testFinanceRequest.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testFinanceRequest.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentAmount()).isEqualTo(UPDATED_INSTALLMENT_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentPeriod()).isEqualTo(UPDATED_INSTALLMENT_PERIOD);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<FinanceRequest> financeRequestSearchList = IterableUtils.toList(financeRequestSearchRepository.findAll());
                FinanceRequest testFinanceRequestSearch = financeRequestSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testFinanceRequestSearch.getUserId()).isEqualTo(UPDATED_USER_ID);
                assertThat(testFinanceRequestSearch.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
                assertThat(testFinanceRequestSearch.getInstallmentAmount()).isEqualTo(UPDATED_INSTALLMENT_AMOUNT);
                assertThat(testFinanceRequestSearch.getInstallmentPeriod()).isEqualTo(UPDATED_INSTALLMENT_PERIOD);
            });
    }

    @Test
    void putNonExistingFinanceRequest() throws Exception {
        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        financeRequest.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, financeRequest.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchFinanceRequest() throws Exception {
        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        financeRequest.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRequestMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamFinanceRequest() throws Exception {
        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        financeRequest.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRequestMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(financeRequest)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateFinanceRequestWithPatch() throws Exception {
        // Initialize the database
        financeRequestRepository.save(financeRequest);

        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();

        // Update the financeRequest using partial update
        FinanceRequest partialUpdatedFinanceRequest = new FinanceRequest();
        partialUpdatedFinanceRequest.setId(financeRequest.getId());

        partialUpdatedFinanceRequest.installmentPeriod(UPDATED_INSTALLMENT_PERIOD);

        restFinanceRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinanceRequest))
            )
            .andExpect(status().isOk());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        FinanceRequest testFinanceRequest = financeRequestList.get(financeRequestList.size() - 1);
        assertThat(testFinanceRequest.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testFinanceRequest.getTotalAmount()).isEqualTo(DEFAULT_TOTAL_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentAmount()).isEqualTo(DEFAULT_INSTALLMENT_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentPeriod()).isEqualTo(UPDATED_INSTALLMENT_PERIOD);
    }

    @Test
    void fullUpdateFinanceRequestWithPatch() throws Exception {
        // Initialize the database
        financeRequestRepository.save(financeRequest);

        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();

        // Update the financeRequest using partial update
        FinanceRequest partialUpdatedFinanceRequest = new FinanceRequest();
        partialUpdatedFinanceRequest.setId(financeRequest.getId());

        partialUpdatedFinanceRequest
            .userId(UPDATED_USER_ID)
            .totalAmount(UPDATED_TOTAL_AMOUNT)
            .installmentAmount(UPDATED_INSTALLMENT_AMOUNT)
            .installmentPeriod(UPDATED_INSTALLMENT_PERIOD);

        restFinanceRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFinanceRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedFinanceRequest))
            )
            .andExpect(status().isOk());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        FinanceRequest testFinanceRequest = financeRequestList.get(financeRequestList.size() - 1);
        assertThat(testFinanceRequest.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testFinanceRequest.getTotalAmount()).isEqualTo(UPDATED_TOTAL_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentAmount()).isEqualTo(UPDATED_INSTALLMENT_AMOUNT);
        assertThat(testFinanceRequest.getInstallmentPeriod()).isEqualTo(UPDATED_INSTALLMENT_PERIOD);
    }

    @Test
    void patchNonExistingFinanceRequest() throws Exception {
        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        financeRequest.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFinanceRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, financeRequest.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchFinanceRequest() throws Exception {
        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        financeRequest.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRequestMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isBadRequest());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamFinanceRequest() throws Exception {
        int databaseSizeBeforeUpdate = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        financeRequest.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFinanceRequestMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(financeRequest))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the FinanceRequest in the database
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteFinanceRequest() throws Exception {
        // Initialize the database
        financeRequestRepository.save(financeRequest);
        financeRequestRepository.save(financeRequest);
        financeRequestSearchRepository.save(financeRequest);

        int databaseSizeBeforeDelete = financeRequestRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the financeRequest
        restFinanceRequestMockMvc
            .perform(delete(ENTITY_API_URL_ID, financeRequest.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<FinanceRequest> financeRequestList = financeRequestRepository.findAll();
        assertThat(financeRequestList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(financeRequestSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchFinanceRequest() throws Exception {
        // Initialize the database
        financeRequest = financeRequestRepository.save(financeRequest);
        financeRequestSearchRepository.save(financeRequest);

        // Search the financeRequest
        restFinanceRequestMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + financeRequest.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(financeRequest.getId())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID)))
            .andExpect(jsonPath("$.[*].totalAmount").value(hasItem(DEFAULT_TOTAL_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].installmentAmount").value(hasItem(DEFAULT_INSTALLMENT_AMOUNT.doubleValue())))
            .andExpect(jsonPath("$.[*].installmentPeriod").value(hasItem(DEFAULT_INSTALLMENT_PERIOD)));
    }
}
