package com.aiondigital.mfe.transfers.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.transfers.domain.TransactionDetails;
import com.aiondigital.mfe.transfers.repository.TransactionDetailsRepository;
import com.aiondigital.mfe.transfers.service.TransactionDetailsService;
import com.aiondigital.mfe.transfers.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.aiondigital.mfe.transfers.domain.TransactionDetails}.
 */
@RestController
@RequestMapping("/api")
public class TransactionDetailsResource {

    private final Logger log = LoggerFactory.getLogger(TransactionDetailsResource.class);

    private static final String ENTITY_NAME = "transferServiceTransactionDetails";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TransactionDetailsService transactionDetailsService;

    private final TransactionDetailsRepository transactionDetailsRepository;

    public TransactionDetailsResource(
        TransactionDetailsService transactionDetailsService,
        TransactionDetailsRepository transactionDetailsRepository
    ) {
        this.transactionDetailsService = transactionDetailsService;
        this.transactionDetailsRepository = transactionDetailsRepository;
    }

    /**
     * {@code POST  /transaction-details} : Create a new transactionDetails.
     *
     * @param transactionDetails the transactionDetails to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new transactionDetails, or with status {@code 400 (Bad Request)} if the transactionDetails has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/transaction-details")
    public ResponseEntity<TransactionDetails> createTransactionDetails(@RequestBody TransactionDetails transactionDetails)
        throws URISyntaxException {
        log.debug("REST request to save TransactionDetails : {}", transactionDetails);
        if (transactionDetails.getId() != null) {
            throw new BadRequestAlertException("A new transactionDetails cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TransactionDetails result = transactionDetailsService.save(transactionDetails);
        return ResponseEntity
            .created(new URI("/api/transaction-details/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /transaction-details/:id} : Updates an existing transactionDetails.
     *
     * @param id the id of the transactionDetails to save.
     * @param transactionDetails the transactionDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionDetails,
     * or with status {@code 400 (Bad Request)} if the transactionDetails is not valid,
     * or with status {@code 500 (Internal Server Error)} if the transactionDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/transaction-details/{id}")
    public ResponseEntity<TransactionDetails> updateTransactionDetails(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody TransactionDetails transactionDetails
    ) throws URISyntaxException {
        log.debug("REST request to update TransactionDetails : {}, {}", id, transactionDetails);
        if (transactionDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        TransactionDetails result = transactionDetailsService.update(transactionDetails);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionDetails.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /transaction-details/:id} : Partial updates given fields of an existing transactionDetails, field will ignore if it is null
     *
     * @param id the id of the transactionDetails to save.
     * @param transactionDetails the transactionDetails to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated transactionDetails,
     * or with status {@code 400 (Bad Request)} if the transactionDetails is not valid,
     * or with status {@code 404 (Not Found)} if the transactionDetails is not found,
     * or with status {@code 500 (Internal Server Error)} if the transactionDetails couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/transaction-details/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TransactionDetails> partialUpdateTransactionDetails(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody TransactionDetails transactionDetails
    ) throws URISyntaxException {
        log.debug("REST request to partial update TransactionDetails partially : {}, {}", id, transactionDetails);
        if (transactionDetails.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, transactionDetails.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!transactionDetailsRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TransactionDetails> result = transactionDetailsService.partialUpdate(transactionDetails);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, transactionDetails.getId())
        );
    }

    /**
     * {@code GET  /transaction-details} : get all the transactionDetails.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of transactionDetails in body.
     */
    @GetMapping("/transaction-details")
    public ResponseEntity<List<TransactionDetails>> getAllTransactionDetails(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get a page of TransactionDetails");
        Page<TransactionDetails> page = transactionDetailsService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /transaction-details/:id} : get the "id" transactionDetails.
     *
     * @param id the id of the transactionDetails to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the transactionDetails, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/transaction-details/{id}")
    public ResponseEntity<TransactionDetails> getTransactionDetails(@PathVariable String id) {
        log.debug("REST request to get TransactionDetails : {}", id);
        Optional<TransactionDetails> transactionDetails = transactionDetailsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(transactionDetails);
    }

    /**
     * {@code DELETE  /transaction-details/:id} : delete the "id" transactionDetails.
     *
     * @param id the id of the transactionDetails to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/transaction-details/{id}")
    public ResponseEntity<Void> deleteTransactionDetails(@PathVariable String id) {
        log.debug("REST request to delete TransactionDetails : {}", id);
        transactionDetailsService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/transaction-details?query=:query} : search for the transactionDetails corresponding
     * to the query.
     *
     * @param query the query of the transactionDetails search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/transaction-details")
    public ResponseEntity<List<TransactionDetails>> searchTransactionDetails(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of TransactionDetails for query {}", query);
        Page<TransactionDetails> page = transactionDetailsService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
