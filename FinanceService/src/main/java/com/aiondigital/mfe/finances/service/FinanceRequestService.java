package com.aiondigital.mfe.finances.service;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.finances.domain.FinanceRequest;
import com.aiondigital.mfe.finances.repository.FinanceRequestRepository;
import com.aiondigital.mfe.finances.repository.search.FinanceRequestSearchRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for managing {@link FinanceRequest}.
 */
@Service
public class FinanceRequestService {

    private final Logger log = LoggerFactory.getLogger(FinanceRequestService.class);

    private final FinanceRequestRepository financeRequestRepository;

    private final FinanceRequestSearchRepository financeRequestSearchRepository;

    public FinanceRequestService(
        FinanceRequestRepository financeRequestRepository,
        FinanceRequestSearchRepository financeRequestSearchRepository
    ) {
        this.financeRequestRepository = financeRequestRepository;
        this.financeRequestSearchRepository = financeRequestSearchRepository;
    }

    /**
     * Save a financeRequest.
     *
     * @param financeRequest the entity to save.
     * @return the persisted entity.
     */
    public FinanceRequest save(FinanceRequest financeRequest) {
        log.debug("Request to save FinanceRequest : {}", financeRequest);
        FinanceRequest result = financeRequestRepository.save(financeRequest);
        financeRequestSearchRepository.index(result);
        return result;
    }

    /**
     * Update a financeRequest.
     *
     * @param financeRequest the entity to save.
     * @return the persisted entity.
     */
    public FinanceRequest update(FinanceRequest financeRequest) {
        log.debug("Request to update FinanceRequest : {}", financeRequest);
        FinanceRequest result = financeRequestRepository.save(financeRequest);
        financeRequestSearchRepository.index(result);
        return result;
    }

    /**
     * Partially update a financeRequest.
     *
     * @param financeRequest the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<FinanceRequest> partialUpdate(FinanceRequest financeRequest) {
        log.debug("Request to partially update FinanceRequest : {}", financeRequest);

        return financeRequestRepository
            .findById(financeRequest.getId())
            .map(existingFinanceRequest -> {
                if (financeRequest.getUserId() != null) {
                    existingFinanceRequest.setUserId(financeRequest.getUserId());
                }
                if (financeRequest.getTotalAmount() != null) {
                    existingFinanceRequest.setTotalAmount(financeRequest.getTotalAmount());
                }
                if (financeRequest.getInstallmentAmount() != null) {
                    existingFinanceRequest.setInstallmentAmount(financeRequest.getInstallmentAmount());
                }
                if (financeRequest.getInstallmentPeriod() != null) {
                    existingFinanceRequest.setInstallmentPeriod(financeRequest.getInstallmentPeriod());
                }

                return existingFinanceRequest;
            })
            .map(financeRequestRepository::save)
            .map(savedFinanceRequest -> {
                financeRequestSearchRepository.save(savedFinanceRequest);

                return savedFinanceRequest;
            });
    }

    /**
     * Get all the financeRequests.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<FinanceRequest> findAll(Pageable pageable) {
        log.debug("Request to get all FinanceRequests");
        return financeRequestRepository.findAll(pageable);
    }

    /**
     * Get one financeRequest by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    public Optional<FinanceRequest> findOne(String id) {
        log.debug("Request to get FinanceRequest : {}", id);
        return financeRequestRepository.findById(id);
    }

    /**
     * Delete the financeRequest by id.
     *
     * @param id the id of the entity.
     */
    public void delete(String id) {
        log.debug("Request to delete FinanceRequest : {}", id);
        financeRequestRepository.deleteById(id);
        financeRequestSearchRepository.deleteById(id);
    }

    /**
     * Search for the financeRequest corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    public Page<FinanceRequest> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of FinanceRequests for query {}", query);
        return financeRequestSearchRepository.search(query, pageable);
    }
}
