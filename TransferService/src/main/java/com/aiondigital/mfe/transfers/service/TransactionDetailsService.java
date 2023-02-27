package com.aiondigital.mfe.transfers.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.transfers.domain.TransactionDetails;
import com.aiondigital.mfe.transfers.repository.TransactionDetailsRepository;
import com.aiondigital.mfe.transfers.repository.search.TransactionDetailsSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link TransactionDetails}.
 */
@Service
public class TransactionDetailsService {

    private final Logger log = LoggerFactory.getLogger(TransactionDetailsService.class);

    private final TransactionDetailsRepository transactionDetailsRepository;

    private final TransactionDetailsSearchRepository transactionDetailsSearchRepository;

    public TransactionDetailsService(
        TransactionDetailsRepository transactionDetailsRepository,
        TransactionDetailsSearchRepository transactionDetailsSearchRepository
    ) {
        this.transactionDetailsRepository = transactionDetailsRepository;
        this.transactionDetailsSearchRepository = transactionDetailsSearchRepository;
    }

    /**
     * Save a transactionDetails.
     *
     * @param transactionDetails the entity to save.
     * @return the persisted entity.
     */
    public TransactionDetails save(TransactionDetails transactionDetails) {
        log.debug("Request to save TransactionDetails : {}", transactionDetails);
        TransactionDetails result = transactionDetailsRepository.save(transactionDetails);
        transactionDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Update a transactionDetails.
     *
     * @param transactionDetails the entity to save.
     * @return the persisted entity.
     */
    public TransactionDetails update(TransactionDetails transactionDetails) {
        log.debug("Request to update TransactionDetails : {}", transactionDetails);
        TransactionDetails result = transactionDetailsRepository.save(transactionDetails);
        transactionDetailsSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a transactionDetails.
     *
     * @param transactionDetails the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TransactionDetails> partialUpdate(TransactionDetails transactionDetails) {
        log.debug("Request to partially update TransactionDetails : {}", transactionDetails);

        return transactionDetailsRepository
            .findById(transactionDetails.getId())
            .map(existingTransactionDetails -> {
                if (transactionDetails.getKey() != null) {
                    existingTransactionDetails.setKey(transactionDetails.getKey());
                }
                if (transactionDetails.getValue() != null) {
                    existingTransactionDetails.setValue(transactionDetails.getValue());
                }

                return existingTransactionDetails;
            })
            .map(transactionDetailsRepository::save)
            .map(savedTransactionDetails -> {
                transactionDetailsSearchRepository.save(savedTransactionDetails);

                return savedTransactionDetails;
            });
    }

    /**
     * Get all the transactionDetails.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<TransactionDetails> findAll(Pageable pageable) {
        log.debug("Request to get all TransactionDetails");
        return transactionDetailsRepository.findAll(pageable);
    }

    /**
     * Get one transactionDetails by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<TransactionDetails> findOne(String id) {
        log.debug("Request to get TransactionDetails : {}", id);
        return transactionDetailsRepository.findById(id);
    }

    /**
     * Delete the transactionDetails by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete TransactionDetails : {}", id);
        transactionDetailsRepository.deleteById(id);
        transactionDetailsSearchRepository.deleteById(id);
    }

    /**
     * Search for the transactionDetails corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<TransactionDetails> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of TransactionDetails for query {}", query);
        return transactionDetailsSearchRepository.search(query, pageable);
    }
}
