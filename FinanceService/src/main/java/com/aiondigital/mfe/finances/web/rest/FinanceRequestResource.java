package com.aiondigital.mfe.finances.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import com.aiondigital.mfe.finances.domain.FinanceRequest;
import com.aiondigital.mfe.finances.repository.FinanceRequestRepository;
import com.aiondigital.mfe.finances.service.FinanceRequestService;
import com.aiondigital.mfe.finances.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
 * REST controller for managing {@link com.aiondigital.mfe.finances.domain.FinanceRequest}.
 */
@RestController
@RequestMapping("/api")
public class FinanceRequestResource {

    private final Logger log = LoggerFactory.getLogger(FinanceRequestResource.class);

    private static final String ENTITY_NAME = "financeServiceFinanceRequest";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FinanceRequestService financeRequestService;

    private final FinanceRequestRepository financeRequestRepository;

    public FinanceRequestResource(FinanceRequestService financeRequestService, FinanceRequestRepository financeRequestRepository) {
        this.financeRequestService = financeRequestService;
        this.financeRequestRepository = financeRequestRepository;
    }

    /**
     * {@code POST  /finance-requests} : Create a new financeRequest.
     *
     * @param financeRequest the financeRequest to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new financeRequest, or with status {@code 400 (Bad Request)} if the financeRequest has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/finance-requests")
    public ResponseEntity<FinanceRequest> createFinanceRequest(@Valid @RequestBody FinanceRequest financeRequest)
        throws URISyntaxException {
        log.debug("REST request to save FinanceRequest : {}", financeRequest);
        if (financeRequest.getId() != null) {
            throw new BadRequestAlertException("A new financeRequest cannot already have an ID", ENTITY_NAME, "idexists");
        }
        FinanceRequest result = financeRequestService.save(financeRequest);
        return ResponseEntity
            .created(new URI("/api/finance-requests/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
            .body(result);
    }

    /**
     * {@code PUT  /finance-requests/:id} : Updates an existing financeRequest.
     *
     * @param id the id of the financeRequest to save.
     * @param financeRequest the financeRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeRequest,
     * or with status {@code 400 (Bad Request)} if the financeRequest is not valid,
     * or with status {@code 500 (Internal Server Error)} if the financeRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/finance-requests/{id}")
    public ResponseEntity<FinanceRequest> updateFinanceRequest(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody FinanceRequest financeRequest
    ) throws URISyntaxException {
        log.debug("REST request to update FinanceRequest : {}, {}", id, financeRequest);
        if (financeRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        FinanceRequest result = financeRequestService.update(financeRequest);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, financeRequest.getId()))
            .body(result);
    }

    /**
     * {@code PATCH  /finance-requests/:id} : Partial updates given fields of an existing financeRequest, field will ignore if it is null
     *
     * @param id the id of the financeRequest to save.
     * @param financeRequest the financeRequest to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated financeRequest,
     * or with status {@code 400 (Bad Request)} if the financeRequest is not valid,
     * or with status {@code 404 (Not Found)} if the financeRequest is not found,
     * or with status {@code 500 (Internal Server Error)} if the financeRequest couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/finance-requests/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FinanceRequest> partialUpdateFinanceRequest(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody FinanceRequest financeRequest
    ) throws URISyntaxException {
        log.debug("REST request to partial update FinanceRequest partially : {}, {}", id, financeRequest);
        if (financeRequest.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, financeRequest.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!financeRequestRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FinanceRequest> result = financeRequestService.partialUpdate(financeRequest);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, financeRequest.getId())
        );
    }

    /**
     * {@code GET  /finance-requests} : get all the financeRequests.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of financeRequests in body.
     */
    @GetMapping("/finance-requests")
    public ResponseEntity<List<FinanceRequest>> getAllFinanceRequests(@org.springdoc.api.annotations.ParameterObject Pageable pageable) {
        log.debug("REST request to get a page of FinanceRequests");
        Page<FinanceRequest> page = financeRequestService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /finance-requests/:id} : get the "id" financeRequest.
     *
     * @param id the id of the financeRequest to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the financeRequest, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/finance-requests/{id}")
    public ResponseEntity<FinanceRequest> getFinanceRequest(@PathVariable String id) {
        log.debug("REST request to get FinanceRequest : {}", id);
        Optional<FinanceRequest> financeRequest = financeRequestService.findOne(id);
        return ResponseUtil.wrapOrNotFound(financeRequest);
    }

    /**
     * {@code DELETE  /finance-requests/:id} : delete the "id" financeRequest.
     *
     * @param id the id of the financeRequest to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/finance-requests/{id}")
    public ResponseEntity<Void> deleteFinanceRequest(@PathVariable String id) {
        log.debug("REST request to delete FinanceRequest : {}", id);
        financeRequestService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build();
    }

    /**
     * {@code SEARCH  /_search/finance-requests?query=:query} : search for the financeRequest corresponding
     * to the query.
     *
     * @param query the query of the financeRequest search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/finance-requests")
    public ResponseEntity<List<FinanceRequest>> searchFinanceRequests(
        @RequestParam String query,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to search for a page of FinanceRequests for query {}", query);
        Page<FinanceRequest> page = financeRequestService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
