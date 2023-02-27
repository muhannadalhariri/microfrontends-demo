package com.aiondigital.mfe.lookupsservice.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.aiondigital.mfe.lookupsservice.IntegrationTest;
import com.aiondigital.mfe.lookupsservice.domain.Card;
import com.aiondigital.mfe.lookupsservice.repository.CardRepository;
import com.aiondigital.mfe.lookupsservice.repository.search.CardSearchRepository;
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
 * Integration tests for the {@link CardResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CardResourceIT {

    private static final String DEFAULT_NAME_AR = "AAAAAAAAAA";
    private static final String UPDATED_NAME_AR = "BBBBBBBBBB";

    private static final String DEFAULT_NAME_EN = "AAAAAAAAAA";
    private static final String UPDATED_NAME_EN = "BBBBBBBBBB";

    private static final Integer DEFAULT_CARD_TYPE_ID = 1;
    private static final Integer UPDATED_CARD_TYPE_ID = 2;

    private static final String DEFAULT_CARD_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_CARD_REFERENCE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/cards";

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardSearchRepository cardSearchRepository;

    @Autowired
    private MockMvc restCardMockMvc;

    private Card card;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Card createEntity() {
        Card card = new Card()
            .nameAr(DEFAULT_NAME_AR)
            .nameEn(DEFAULT_NAME_EN)
            .cardTypeId(DEFAULT_CARD_TYPE_ID)
            .cardReference(DEFAULT_CARD_REFERENCE);
        return card;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Card createUpdatedEntity() {
        Card card = new Card()
            .nameAr(UPDATED_NAME_AR)
            .nameEn(UPDATED_NAME_EN)
            .cardTypeId(UPDATED_CARD_TYPE_ID)
            .cardReference(UPDATED_CARD_REFERENCE);
        return card;
    }

    @AfterEach
    public void cleanupElasticSearchRepository() {
        cardSearchRepository.deleteAll();
        assertThat(cardSearchRepository.count()).isEqualTo(0);
    }

    @BeforeEach
    public void initTest() {
        cardRepository.deleteAll();
        card = createEntity();
    }

    @Test
    void createCard() throws Exception {
        int databaseSizeBeforeCreate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        // Create the Card
        restCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isCreated());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeCreate + 1);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore + 1);
            });
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getNameAr()).isEqualTo(DEFAULT_NAME_AR);
        assertThat(testCard.getNameEn()).isEqualTo(DEFAULT_NAME_EN);
        assertThat(testCard.getCardTypeId()).isEqualTo(DEFAULT_CARD_TYPE_ID);
        assertThat(testCard.getCardReference()).isEqualTo(DEFAULT_CARD_REFERENCE);
    }

    @Test
    void createCardWithExistingId() throws Exception {
        // Create the Card with an existing ID
        card.setId("existing_id");

        int databaseSizeBeforeCreate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());

        // An entity with an existing ID cannot be created, so this API call must fail
        restCardMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeCreate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void getAllCards() throws Exception {
        // Initialize the database
        cardRepository.save(card);

        // Get all the cardList
        restCardMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(card.getId())))
            .andExpect(jsonPath("$.[*].nameAr").value(hasItem(DEFAULT_NAME_AR)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].cardTypeId").value(hasItem(DEFAULT_CARD_TYPE_ID)))
            .andExpect(jsonPath("$.[*].cardReference").value(hasItem(DEFAULT_CARD_REFERENCE)));
    }

    @Test
    void getCard() throws Exception {
        // Initialize the database
        cardRepository.save(card);

        // Get the card
        restCardMockMvc
            .perform(get(ENTITY_API_URL_ID, card.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(card.getId()))
            .andExpect(jsonPath("$.nameAr").value(DEFAULT_NAME_AR))
            .andExpect(jsonPath("$.nameEn").value(DEFAULT_NAME_EN))
            .andExpect(jsonPath("$.cardTypeId").value(DEFAULT_CARD_TYPE_ID))
            .andExpect(jsonPath("$.cardReference").value(DEFAULT_CARD_REFERENCE));
    }

    @Test
    void getNonExistingCard() throws Exception {
        // Get the card
        restCardMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    void putExistingCard() throws Exception {
        // Initialize the database
        cardRepository.save(card);

        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        cardSearchRepository.save(card);
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());

        // Update the card
        Card updatedCard = cardRepository.findById(card.getId()).get();
        updatedCard.nameAr(UPDATED_NAME_AR).nameEn(UPDATED_NAME_EN).cardTypeId(UPDATED_CARD_TYPE_ID).cardReference(UPDATED_CARD_REFERENCE);

        restCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCard.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCard))
            )
            .andExpect(status().isOk());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getNameAr()).isEqualTo(UPDATED_NAME_AR);
        assertThat(testCard.getNameEn()).isEqualTo(UPDATED_NAME_EN);
        assertThat(testCard.getCardTypeId()).isEqualTo(UPDATED_CARD_TYPE_ID);
        assertThat(testCard.getCardReference()).isEqualTo(UPDATED_CARD_REFERENCE);
        await()
            .atMost(5, TimeUnit.SECONDS)
            .untilAsserted(() -> {
                int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
                assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
                List<Card> cardSearchList = IterableUtils.toList(cardSearchRepository.findAll());
                Card testCardSearch = cardSearchList.get(searchDatabaseSizeAfter - 1);
                assertThat(testCardSearch.getNameAr()).isEqualTo(UPDATED_NAME_AR);
                assertThat(testCardSearch.getNameEn()).isEqualTo(UPDATED_NAME_EN);
                assertThat(testCardSearch.getCardTypeId()).isEqualTo(UPDATED_CARD_TYPE_ID);
                assertThat(testCardSearch.getCardReference()).isEqualTo(UPDATED_CARD_REFERENCE);
            });
    }

    @Test
    void putNonExistingCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        card.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, card.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithIdMismatchCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        card.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                put(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void putWithMissingIdPathParamCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        card.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void partialUpdateCardWithPatch() throws Exception {
        // Initialize the database
        cardRepository.save(card);

        int databaseSizeBeforeUpdate = cardRepository.findAll().size();

        // Update the card using partial update
        Card partialUpdatedCard = new Card();
        partialUpdatedCard.setId(card.getId());

        partialUpdatedCard.nameAr(UPDATED_NAME_AR).cardReference(UPDATED_CARD_REFERENCE);

        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCard))
            )
            .andExpect(status().isOk());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getNameAr()).isEqualTo(UPDATED_NAME_AR);
        assertThat(testCard.getNameEn()).isEqualTo(DEFAULT_NAME_EN);
        assertThat(testCard.getCardTypeId()).isEqualTo(DEFAULT_CARD_TYPE_ID);
        assertThat(testCard.getCardReference()).isEqualTo(UPDATED_CARD_REFERENCE);
    }

    @Test
    void fullUpdateCardWithPatch() throws Exception {
        // Initialize the database
        cardRepository.save(card);

        int databaseSizeBeforeUpdate = cardRepository.findAll().size();

        // Update the card using partial update
        Card partialUpdatedCard = new Card();
        partialUpdatedCard.setId(card.getId());

        partialUpdatedCard
            .nameAr(UPDATED_NAME_AR)
            .nameEn(UPDATED_NAME_EN)
            .cardTypeId(UPDATED_CARD_TYPE_ID)
            .cardReference(UPDATED_CARD_REFERENCE);

        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCard.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCard))
            )
            .andExpect(status().isOk());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        Card testCard = cardList.get(cardList.size() - 1);
        assertThat(testCard.getNameAr()).isEqualTo(UPDATED_NAME_AR);
        assertThat(testCard.getNameEn()).isEqualTo(UPDATED_NAME_EN);
        assertThat(testCard.getCardTypeId()).isEqualTo(UPDATED_CARD_TYPE_ID);
        assertThat(testCard.getCardReference()).isEqualTo(UPDATED_CARD_REFERENCE);
    }

    @Test
    void patchNonExistingCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        card.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, card.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithIdMismatchCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        card.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, UUID.randomUUID().toString())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(card))
            )
            .andExpect(status().isBadRequest());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void patchWithMissingIdPathParamCard() throws Exception {
        int databaseSizeBeforeUpdate = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        card.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(card)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Card in the database
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeUpdate);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore);
    }

    @Test
    void deleteCard() throws Exception {
        // Initialize the database
        cardRepository.save(card);
        cardRepository.save(card);
        cardSearchRepository.save(card);

        int databaseSizeBeforeDelete = cardRepository.findAll().size();
        int searchDatabaseSizeBefore = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeBefore).isEqualTo(databaseSizeBeforeDelete);

        // Delete the card
        restCardMockMvc
            .perform(delete(ENTITY_API_URL_ID, card.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Card> cardList = cardRepository.findAll();
        assertThat(cardList).hasSize(databaseSizeBeforeDelete - 1);
        int searchDatabaseSizeAfter = IterableUtil.sizeOf(cardSearchRepository.findAll());
        assertThat(searchDatabaseSizeAfter).isEqualTo(searchDatabaseSizeBefore - 1);
    }

    @Test
    void searchCard() throws Exception {
        // Initialize the database
        card = cardRepository.save(card);
        cardSearchRepository.save(card);

        // Search the card
        restCardMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + card.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(card.getId())))
            .andExpect(jsonPath("$.[*].nameAr").value(hasItem(DEFAULT_NAME_AR)))
            .andExpect(jsonPath("$.[*].nameEn").value(hasItem(DEFAULT_NAME_EN)))
            .andExpect(jsonPath("$.[*].cardTypeId").value(hasItem(DEFAULT_CARD_TYPE_ID)))
            .andExpect(jsonPath("$.[*].cardReference").value(hasItem(DEFAULT_CARD_REFERENCE)));
    }
}
