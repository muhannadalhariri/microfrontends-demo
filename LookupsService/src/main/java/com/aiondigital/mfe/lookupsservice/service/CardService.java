package com.aiondigital.mfe.lookupsservice.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.lookupsservice.domain.Card;
import com.aiondigital.mfe.lookupsservice.repository.CardRepository;
import com.aiondigital.mfe.lookupsservice.repository.search.CardSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Card}.
 */
@Service
public class CardService {

    private final Logger log = LoggerFactory.getLogger(CardService.class);

    private final CardRepository cardRepository;

    private final CardSearchRepository cardSearchRepository;

    public CardService(CardRepository cardRepository, CardSearchRepository cardSearchRepository) {
        this.cardRepository = cardRepository;
        this.cardSearchRepository = cardSearchRepository;
    }

    /**
     * Save a card.
     *
     * @param card the entity to save.
     * @return the persisted entity.
     */
    public Card save(Card card) {
        log.debug("Request to save Card : {}", card);
        Card result = cardRepository.save(card);
        cardSearchRepository.index(result);
        return result;
    }

    /**
     * Update a card.
     *
     * @param card the entity to save.
     * @return the persisted entity.
     */
    public Card update(Card card) {
        log.debug("Request to update Card : {}", card);
        Card result = cardRepository.save(card);
        cardSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a card.
     *
     * @param card the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Card> partialUpdate(Card card) {
        log.debug("Request to partially update Card : {}", card);

        return cardRepository
            .findById(card.getId())
            .map(existingCard -> {
                if (card.getNameAr() != null) {
                    existingCard.setNameAr(card.getNameAr());
                }
                if (card.getNameEn() != null) {
                    existingCard.setNameEn(card.getNameEn());
                }
                if (card.getCardTypeId() != null) {
                    existingCard.setCardTypeId(card.getCardTypeId());
                }
                if (card.getCardReference() != null) {
                    existingCard.setCardReference(card.getCardReference());
                }

                return existingCard;
            })
            .map(cardRepository::save)
            .map(savedCard -> {
                cardSearchRepository.save(savedCard);

                return savedCard;
            });
    }

    /**
     * Get all the cards.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Card> findAll(Pageable pageable) {
        log.debug("Request to get all Cards");
        return cardRepository.findAll(pageable);
    }

    /**
     * Get one card by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Card> findOne(String id) {
        log.debug("Request to get Card : {}", id);
        return cardRepository.findById(id);
    }

    /**
     * Delete the card by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Card : {}", id);
        cardRepository.deleteById(id);
        cardSearchRepository.deleteById(id);
    }

    /**
     * Search for the card corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Card> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Cards for query {}", query);
        return cardSearchRepository.search(query, pageable);
    }
}
