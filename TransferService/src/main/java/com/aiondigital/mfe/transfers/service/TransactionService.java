package com.aiondigital.mfe.transfers.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.transfers.domain.Transaction;
import com.aiondigital.mfe.transfers.repository.TransactionRepository;
import com.aiondigital.mfe.transfers.repository.search.TransactionSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link Transaction}.
 */
@Service
public class TransactionService {

    private final Logger log = LoggerFactory.getLogger(TransactionService.class);

    private final TransactionRepository transactionRepository;

    private final TransactionSearchRepository transactionSearchRepository;

    public TransactionService(TransactionRepository transactionRepository, TransactionSearchRepository transactionSearchRepository) {
        this.transactionRepository = transactionRepository;
        this.transactionSearchRepository = transactionSearchRepository;
    }

    /**
     * Save a transaction.
     *
     * @param transaction the entity to save.
     * @return the persisted entity.
     */
    public Transaction save(Transaction transaction) {
        log.debug("Request to save Transaction : {}", transaction);
        Transaction result = transactionRepository.save(transaction);
        transactionSearchRepository.index(result);
        return result;
    }

    /**
     * Update a transaction.
     *
     * @param transaction the entity to save.
     * @return the persisted entity.
     */
    public Transaction update(Transaction transaction) {
        log.debug("Request to update Transaction : {}", transaction);
        Transaction result = transactionRepository.save(transaction);
        transactionSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a transaction.
     *
     * @param transaction the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Transaction> partialUpdate(Transaction transaction) {
        log.debug("Request to partially update Transaction : {}", transaction);

        return transactionRepository
            .findById(transaction.getId())
            .map(existingTransaction -> {
                if (transaction.getReferenceId() != null) {
                    existingTransaction.setReferenceId(transaction.getReferenceId());
                }
                if (transaction.getUserId() != null) {
                    existingTransaction.setUserId(transaction.getUserId());
                }

                return existingTransaction;
            })
            .map(transactionRepository::save)
            .map(savedTransaction -> {
                transactionSearchRepository.save(savedTransaction);

                return savedTransaction;
            });
    }

    /**
     * Get all the transactions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Transaction> findAll(Pageable pageable) {
        log.debug("Request to get all Transactions");
        return transactionRepository.findAll(pageable);
    }

    /**
     * Get one transaction by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<Transaction> findOne(String id) {
        log.debug("Request to get Transaction : {}", id);
        return transactionRepository.findById(id);
    }

    /**
     * Delete the transaction by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete Transaction : {}", id);
        transactionRepository.deleteById(id);
        transactionSearchRepository.deleteById(id);
    }

    /**
     * Search for the transaction corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<Transaction> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Transactions for query {}", query);
        return transactionSearchRepository.search(query, pageable);
    }
}
